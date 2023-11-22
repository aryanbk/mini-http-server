package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
                BufferedReader reader = new BufferedReader(isr)) {

            String line;
            int contentLength = 0;
            StringBuilder requestBuilder = new StringBuilder();

            while (!(line = reader.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            requestBuilder.append("\r\n"); // End of headers

            // Read the body
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                requestBuilder.append(body);
                System.out.println("Content Length: " + contentLength);
                System.out.println("Body: " + new String(body));
            }

            HttpRequest httpRequest = new HttpRequest(requestBuilder.toString());
            String method = httpRequest.method;
            String uri = httpRequest.uri;

            // --------------------- response handling
            HttpRespose httpRespose;
            String response = "HTTP/1.1 404 Not Found\r\n\r\n";

            if (method.equals("POST") && uri.startsWith("/files/") && args.length != 0) {
                // System.out.println("\n\nPOST");
                String directoryPath = "";
                for (int i = 0; i < args.length - 1; ++i) {
                    if (args[i].equals("--directory")) {
                        directoryPath = args[i + 1];
                        break;
                    }
                }
                String pathString = uri.replaceFirst("/files/", directoryPath);
                Path path = Paths.get(pathString);
                System.out.println("pathString: " + pathString);
                File file = new File(pathString);
                if (!file.exists() && new File(directoryPath).isDirectory()
                        && httpRequest.headers.getOrDefault("Content-Type", "").equals("application/octet-stream")) {
                    String content = httpRequest.body;
                    try {
                        Files.write(path, content.getBytes(StandardCharsets.UTF_8));
                        httpRespose = new HttpRespose("201", "Created", "");
                        response = httpRespose.getResponse();
                    } catch (Exception e) {
                        System.out.println("Exception - at file creation " + e.getMessage());
                    }
                }

            } else if ("GET".equals(method) && uri.startsWith("/files/") && args.length != 0) {
                String directoryPath = "";
                for (int i = 0; i < args.length - 1; ++i) {
                    if (args[i].equals("--directory")) {
                        directoryPath = args[i + 1];
                        break;
                    }
                }
                String pathString = uri.replaceFirst("/files/", directoryPath);
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
