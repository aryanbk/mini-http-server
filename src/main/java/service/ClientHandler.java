package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClientHandler implements Runnable {
    private String[] args;
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ClientHandler(Socket clientSocket, String[] args) {
        this.clientSocket = clientSocket;
        this.args = args;
    }

    @Override
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(isr);) {
            String line = reader.readLine();
            StringBuilder requestBuilder = new StringBuilder();
            while (!line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                line = reader.readLine();
            }

            String request = requestBuilder.toString();
            System.out.println("Received request \n" + request);
            HttpRequest httpRequest = new HttpRequest(request);
            String method = httpRequest.method;
            String uri = httpRequest.uri;
            HttpRespose httpRespose;
            String response = "HTTP/1.1 404 Not Found\r\n\r\n";

            if ("GET".equals(method) && uri.startsWith("/files/") && args.length != 0) {
                String parentPath = "";
                for (int i = 0; i < args.length - 1; ++i) {
                    if (args[i].equals("--directory")) {
                        parentPath = args[i + 1];
                        break;
                    }
                }
                String pathString = uri.replaceFirst("/files/", parentPath);
                Path path = Paths.get(pathString);
                System.out.println("pathString: " + pathString);
                File file = new File(pathString);
                if (file.exists() && !file.isDirectory()) {
                    String content = Files.readString(path);
                    httpRespose = new HttpRespose("200", "OK", content);
                    httpRespose.setHeaders("application/octet-stream", Long.toString(file.length()));
                    response = httpRespose.getResponse();
                }
            } else if (method.equals("GET") && uri.equals("/user-agent")) {
                String content = httpRequest.headers.get("User-Agent");
                httpRespose = new HttpRespose("200", "OK", content);
                httpRespose.setHeaders("text/plain", Integer.toString(content.length()));
                response = httpRespose.getResponse();
            } else if (method.equals("GET") && uri.startsWith("/echo/") && uri.split("/").length == 3) {
                String content = uri.split("/")[2];
                httpRespose = new HttpRespose("200", "OK", content);
                httpRespose.setHeaders("text/plain", Integer.toString(content.length()));
                response = httpRespose.getResponse();
            } else if (method.equals("GET") && uri.equals("/")) {
                httpRespose = new HttpRespose("200", "OK", "");

                response = httpRespose.getResponse();
            }

            clientSocket.getOutputStream().write(response.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
