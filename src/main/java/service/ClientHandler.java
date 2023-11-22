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
import java.util.Arrays;

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
            // String line = reader.readLine();
            // StringBuilder requestBuilder = new StringBuilder();
            // // while (line != null && !line.isEmpty()) {
            // // requestBuilder.append(line).append("\r\n");
            // // line = reader.readLine();
            // // }
            // boolean prevNull = false;
            // for (int i = 0; i < 100; ++i) {
            // requestBuilder.append(line);
            // if (line != null && !line.isEmpty()) {
            // if (prevNull) {
            // break;
            // }
            // prevNull = true;
            // }

            // System.out.println(i + " length " + line.length() + " request line " + line);
            // line = reader.readLine();
            // }

            String line;
            int contentLength = 0;
            StringBuilder requestBuilder = new StringBuilder();

            while (!(line = reader.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");

                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                    requestBuilder.append("\r\n").append(reader.readLine()); // passing blank line
                    if (contentLength > 0) {
                        char[] body = new char[contentLength];
                        reader.read(body, 0, contentLength);
                        requestBuilder.append(body);
                        System.out.println("content length " + contentLength);
                        System.out.println("body " + Arrays.toString(body));
                    }
                    break;
                }
            }

            String request = requestBuilder.toString();

            // // while (line != null && !line.isEmpty()) {
            // // requestBuilder.append(line).append("\r\n");
            // // if (line.startsWith("Content-Length:")) {
            // // contentLength = Integer.parseInt(line.split(": ")[1].trim());
            // // }
            // // line = reader.readLine();
            // // }
            // requestBuilder.append("\r\n");
            // // Check for Content-Length header to read the body
            // String requestWithoutBody = requestBuilder.toString();
            // // int contentLength = getContentLength(requestWithoutBody);

            // HttpRequest httpRequest = new HttpRequest(requestWithoutBody);
            // // Read body if Content-Length is specified
            // String contentLengthString =
            // httpRequest.headers.getOrDefault("content-length", "0");
            // int contentLength = Integer.parseInt(contentLengthString);
            // if (contentLength > 0) {
            // char[] body = new char[contentLength];
            // reader.read(body, 0, contentLength);
            // // requestBuilder.append(body);
            // httpRequest.body = new String(body);
            // }

            // String fullRequest = requestBuilder.toString();
            // System.out.println("Full HTTP Request:");
            // System.out.println(fullRequest);

            // String request = requestBuilder.toString();
            // System.out.println("Received request \n" + request);
            HttpRequest httpRequest = new HttpRequest(request);
            String method = httpRequest.method;
            String uri = httpRequest.uri;
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
