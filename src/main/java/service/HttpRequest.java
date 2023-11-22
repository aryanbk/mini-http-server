package service;

import java.util.Map;
import java.util.HashMap;

public class HttpRequest {
    String request;
    String method;
    String uri;
    String httpVersion;
    Map<String, String> headers;
    String body;

    public HttpRequest(String request) {
        this.request = request;
        System.out.println("request\n" + request + "\n---");
        headers = new HashMap<>();

        String[] requestLines = request.split("\r\n");
        System.out.println(request);
        System.out.println("requestLines");
        for (int i = 0; i < requestLines.length; ++i) {
            System.out.println(i);
            System.out.println(requestLines[i]);
        }
        String[] requestLineParts = requestLines[0].split(" ");
        this.method = requestLineParts[0];
        this.uri = requestLineParts[1];
        this.httpVersion = requestLineParts[2];

        int i = 1;
        for (; i < requestLines.length; i++) {
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

        // Body starts after the empty line, so increment i to skip the empty line
        i++;
        if (i < requestLines.length) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (; i < requestLines.length; i++) {
                bodyBuilder.append(requestLines[i]).append("\r\n");
            }
            this.body = bodyBuilder.toString().trim();
        } else {
            this.body = "";
        }
        System.out.println("request body: " + body);
    }
}
