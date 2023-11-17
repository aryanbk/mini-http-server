package service;

import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
    String request;
    String method;
    String uri;
    String httpVersion;
    Map<String, String> headers;

    public HttpRequest(String request) {
        this.request = request;
        headers = new HashMap<>();

        String[] requestLines = request.split("\r\n");
        // String requestLine = requestLines[0];
        String[] requestLineParts = requestLines[0].split(" ");
        this.method = requestLineParts[0];
        this.uri = requestLineParts[1];
        this.httpVersion = requestLineParts[2];

        for (int i = 1; i < requestLines.length; i++) {
            String header = requestLines[i];
            if (header.isEmpty()) {
                break;
            }
            String[] headerParts = header.split(": ");
            if (headerParts.length > 1) {
                String headerName = headerParts[0];
                String headerValue = headerParts[1];
                headers.put(headerName, headerValue);
                System.out.println("headerName and headerValue " + headerName + ": " + headerValue);
            }
        }
    }
}
