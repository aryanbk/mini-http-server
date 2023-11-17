package service;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRespose {
    String version;
    String code;
    String message;
    LinkedHashMap<String, String> headers;
    String responseBody;

    public HttpRespose(String code, String message, LinkedHashMap<String, String> headers, String responseBody) {
        this.version = "1.1";
        this.code = code;
        this.message = message;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpRespose(String version, String code, String message, LinkedHashMap<String, String> headers,
            String responseBody) {
        this.version = version;
        this.code = code;
        this.message = message;
        this.headers = headers;
        this.responseBody = responseBody;
    }

    public HttpRespose(String version, String code, String message, String responseBody) {
        this.version = version;
        this.code = code;
        this.message = message;
        this.headers = new LinkedHashMap<>();
        this.responseBody = responseBody;
    }

    public HttpRespose(String code, String message, String responseBody) {
        this.version = "1.1";
        this.code = code;
        this.message = message;
        this.headers = new LinkedHashMap<>();
        this.responseBody = responseBody;
    }

    public String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append("HTTP/1.1 ").append(code).append(" ").append(message).append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        responseBuilder.append(responseBody);

        return responseBuilder.toString();
    }

    public String getRespose404() {
        return "HTTP/1.1 404 Not Found\r\n\r\n";
    }
}
