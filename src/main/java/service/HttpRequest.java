package service;

import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpRequest {

    private static final String ACCEPT_ENCODING = "Accept-Encoding";
    String request;
    String method;
    String uri;
    String httpVersion;
    Map<String, String> headers;
    byte[] body;

    public HttpRequest(String request, byte[] body) {
        this.request = request;
        // System.out.println("request\n" + request + "\n---");
        headers = new HashMap<>();

        String[] requestLines = request.split("\r\n");
        // System.out.println(request);
        // System.out.println("requestLines");
        for (int i = 0; i < requestLines.length; ++i) {
            // System.out.println(i);
            // System.out.println(requestLines[i]);
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
                String headerName = headerParts[0].trim();
                String headerValue = headerParts[1].trim();
                headers.put(headerName, headerValue);
                // System.out.println("headerName and headerValue " + headerName + ": " +
                // headerValue);
            }
        }

        // // Body starts after the empty line, so increment i to skip the empty line
        // i++;
        // if (i < requestLines.length) {
        //     StringBuilder bodyBuilder = new StringBuilder();
        //     for (; i < requestLines.length; i++) {
        //         bodyBuilder.append(requestLines[i]).append("\r\n");
        //     }
        //     this.body = bodyBuilder.toString().trim();
        // } else {
        //     this.body = "";
        // }

        this.body = body;

        // System.out.println("request body: " + body);
        printRequest();
    }

    public boolean containsEncoding(String encoding) {
        // regex to trim and split
        String[] encodingsStrings = headers.getOrDefault(ACCEPT_ENCODING, "").split("\\s*,\\s*");
        for (int i = 0; i < encodingsStrings.length; ++i) {
            if (encodingsStrings[i].equals(encoding)) {
                return true;
            }
        }
        return false;
    }

    void printRequest() {
        System.out.println(
                "\n-------request start-------\n" + method + "\n" + uri + "\n" + httpVersion + "\n" + headers + "\n"
                        + new String(body, StandardCharsets.UTF_8)
                        + "\n-------request end-------\n");
    }
}
