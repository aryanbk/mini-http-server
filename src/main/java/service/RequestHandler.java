package service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler {

    public static String handleRequest(String request, String[] args) {
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
                String content;
                try {
                    content = Files.readString(path);
                    httpRespose = new HttpRespose("200", "OK", content);
                    httpRespose.setHeaders("application/octet-stream", Long.toString(file.length()));
                    response = httpRespose.getResponse();
                } catch (Exception e) {
                    System.out.println("Exception at file read " + e.getMessage());
                }

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

        return response;
    }
}
