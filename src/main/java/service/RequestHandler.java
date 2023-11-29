package service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.GZIPOutputStream;

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
            response = generateResponse(OK, new byte[0]);
        } else {
            response = generateResponse(NOT_FOUND_RESPONSE, new byte[0]);
        }

        if (GET.equals(method) && httpRequest.containsEncoding("gzip")) {
            try {
                handleCompression(httpRequest, response);
            } catch (Exception e) {
                System.out.println("Exception at compression handler: " + e.getMessage());
                e.printStackTrace(); // Add this line for more detailed error information
            }
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
                Files.write(path, httpRequest.body);
                return generateResponse("201 Created", new byte[0]);
            } catch (IOException e) {
                System.out.println("Exception - at file creation " + e.getMessage());
                return generateResponse("500 Internal Server Error", new byte[0]);
            }
        }
        return generateResponse(NOT_FOUND_RESPONSE, new byte[0]);
    }

    private static HttpRespose handleGetFileRequest(String uri, String directoryPath) {
        String pathString = uri.replaceFirst("/files/", directoryPath);
        Path path = Paths.get(pathString);
        File file = new File(pathString);

        if (file.exists() && !file.isDirectory()) {
            try {
                byte[] content = Files.readAllBytes(path);
                HttpRespose httpRespose = new HttpRespose(OK, content);
                httpRespose.setHeaders(CONTENT_TYPE_HEADER, OCTET_STREAM);
                httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Long.toString(file.length()));
                return httpRespose;
            } catch (IOException e) {
                System.out.println("Exception at file read " + e.getMessage());
                return generateResponse("500 Internal Server Error", new byte[0]);
            }
        }
        return generateResponse(NOT_FOUND_RESPONSE, new byte[0]);
    }

    private static HttpRespose handleUserAgentRequest(HttpRequest httpRequest) {
        String contentString = httpRequest.headers.get("User-Agent");
        byte[] content = contentString.getBytes(StandardCharsets.UTF_8);
        if (content == null) {
            return generateResponse("400 Bad Request", "User-Agent header not found".getBytes(StandardCharsets.UTF_8));
        }
        HttpRespose httpRespose = new HttpRespose(OK, content);
        httpRespose.setHeaders(CONTENT_TYPE_HEADER, TEXT_PLAIN);
        httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Integer.toString(contentString.length()));
        return httpRespose;
    }

    private static HttpRespose handleEchoRequest(String uri) {
        String contentString = uri.split("/")[2];
        byte[] content = contentString.getBytes(StandardCharsets.UTF_8);
        HttpRespose httpRespose = new HttpRespose(OK, content);
        httpRespose.setHeaders(CONTENT_TYPE_HEADER, TEXT_PLAIN);
        httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Integer.toString(contentString.length()));
        // return httpRespose.getResponse();
        return httpRespose;
    }

    private static void handleCompression(HttpRequest httpRequest, HttpRespose httpRespose) throws Exception {
        if (httpRespose.body.length==0) {
            return; // No need to compress empty responses
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(baos)) {
            gzip.write(httpRespose.body);
        }

        byte[] compressedBytes = baos.toByteArray();
        httpRespose.body = compressedBytes; // Change this line
        httpRespose.setHeaders(CONTENT_ENCODING, "gzip");
        httpRespose.setHeaders(CONTENT_LENGTH_HEADER, Integer.toString(compressedBytes.length));
    }

    private static HttpRespose generateResponse(String status, byte[] body) {
        return new HttpRespose(status, body);
    }
}