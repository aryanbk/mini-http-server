package service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RequestHandler {

    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String OK = "200 OK";
    private static final String NOT_FOUND_RESPONSE = "404 Not Found";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String OCTET_STREAM = "application/octet-stream";
    private static final String TEXT_PLAIN = "text/plain";
    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    private static final String CONTENT_ENCODING = "Content-Encoding";

    public static HttpRespose handleRequest(HttpRequest httpRequest, String[] args) {
        // HttpRequest httpRequest = new HttpRequest(request);
        String method = httpRequest.method;
        String uri = httpRequest.uri;

        // String response = generateResponse(NOT_FOUND_RESPONSE, "");
        // String response = HttpRespose.RESPONSE_404;
        HttpRespose response;

        String directoryPath = getDirectoryPath(args);

        if (POST.equals(method) && uri.startsWith("/files/") && directoryPath != null) {
            response = handlePostRequest(uri, httpRequest, directoryPath);
        } else if (GET.equals(method) && uri.startsWith("/files/") && directoryPath != null) {
            response = handleGetFileRequest(uri, directoryPath);
        } else if (GET.equals(method) && uri.equals("/user-agent")) {
            response = handleUserAgentRequest(httpRequest);
        } else if (GET.equals(method) && uri.startsWith("/echo/") && uri.split("/").length == 3) {
            response = handleEchoRequest(uri);
        } else if (GET.equals(method) && uri.equals("/")) {
            response = generateResponse(OK, "");
        } else {
            response = generateResponse(NOT_FOUND_RESPONSE, "");
        }

        if (GET.equals(method) && httpRequest.headers.getOrDefault(ACCEPT_ENCODING, "").equals("gzip")) {
            handleCompression(httpRequest, response);
            // response.setHeaders(CONTENT_ENCODING, "gzip");
        }

        return response;
    }

    private static String getDirectoryPath(String[] args) {
        for (int i = 0; i < args.length - 1; ++i) {
            if (args[i].equals("--directory")) {
                return args[i + 1];
            }
        }
        return null;
    }

    private static HttpRespose handlePostRequest(String uri, HttpRequest httpRequest, String directoryPath) {
        String pathString = uri.replaceFirst("/files/", directoryPath);
        Path path = Paths.get(pathString);
        File file = new File(pathString);

        if (!file.exists() && new File(directoryPath).isDirectory()
                && OCTET_STREAM.equals(httpRequest.headers.get(CONTENT_TYPE_HEADER))) {
            try {
                Files.write(path, httpRequest.body.getBytes(StandardCharsets.UTF_8));
                return generateResponse("201 Created", "");
            } catch (IOException e) {
                System.out.println("Exception - at file creation " + e.getMessage());
                return generateResponse("500 Internal Server Error", "");
            }
        }
        return generateResponse(NOT_FOUND_RESPONSE, "");
    }

    private static HttpRespose handleGetFileRequest(String uri, String directoryPath) {
        String pathString = uri.replaceFirst("/files/", directoryPath);
        Path path = Paths.get(pathString);
        File file = new File(pathString);

        if (file.exists() && !file.isDirectory()) {
            try {
                String content = Files.readString(path);
                HttpRespose httpRespose = new HttpRespose(OK, content);
                httpRespose.setHeaders(CONTENT_TYPE_HEADER, OCTET_STREAM);
                httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Long.toString(file.length()));
                return httpRespose;
            } catch (IOException e) {
                System.out.println("Exception at file read " + e.getMessage());
                return generateResponse("500 Internal Server Error", "");
            }
        }
        return generateResponse(NOT_FOUND_RESPONSE, "");
    }

    private static HttpRespose handleUserAgentRequest(HttpRequest httpRequest) {
        String content = httpRequest.headers.get("User-Agent");
        if (content == null) {
            return generateResponse("400 Bad Request", "User-Agent header not found");
        }
        HttpRespose httpRespose = new HttpRespose(OK, content);
        httpRespose.setHeaders(CONTENT_TYPE_HEADER, TEXT_PLAIN);
        httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Integer.toString(content.length()));
        return httpRespose;
    }

    private static HttpRespose handleEchoRequest(String uri) {
        String content = uri.split("/")[2];
        HttpRespose httpRespose = new HttpRespose(OK, content);
        httpRespose.setHeaders(CONTENT_TYPE_HEADER, TEXT_PLAIN);
        httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Integer.toString(content.length()));
        // return httpRespose.getResponse();
        return httpRespose;
    }

    private static void handleCompression(HttpRequest httpRequest, HttpRespose httpRespose) {
        httpRespose.setHeaders(CONTENT_ENCODING, "gzip");
    }

    private static HttpRespose generateResponse(String status, String body) {
        return new HttpRespose(status, body);
    }
}
