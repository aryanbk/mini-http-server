package service;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRespose {

    public static final String RESPONSE_404 = "HTTP/1.1 404 Not Found\r\n\r\n";
    public static final String VERSION = "HTTP/1.1 ";
    String version;
    String code;
    String message;
    LinkedHashMap<String, String> headers;
    String responseBody;

    // public HttpRespose(String code, String message, LinkedHashMap<String, String>
    // headers, String responseBody) {
    // this.version = "1.1";
    // this.code = code;
    // this.message = message;
    // this.headers = headers;
    // this.responseBody = responseBody;
    // }

    // public HttpRespose(String version, String code, String message,
    // LinkedHashMap<String, String> headers,
    // String responseBody) {
    // this.version = version;
    // this.code = code;
    // this.message = message;
    // this.headers = headers;
    // this.responseBody = responseBody;
    // }

    // public HttpRespose(String version, String code, String message, String
    // responseBody) {
    // this.version = version;
    // this.code = code;
    // this.message = message;
    // this.headers = new LinkedHashMap<>();
    // this.responseBody = responseBody;
    // }

    public HttpRespose(String code, String message, String responseBody) {
        this.version = "1.1";
        this.code = code;
        this.message = message;
        this.headers = new LinkedHashMap<>();
        this.responseBody = responseBody;
    }

    public HttpRespose(String codeAndMessage, String responseBody) {
        this.code = codeAndMessage.substring(0, 3);
        this.message = codeAndMessage.substring(4, codeAndMessage.length());
        this.responseBody = responseBody;
        this.headers = new LinkedHashMap<>();
    }

    public void setHeaders(String key, String values) {
        headers.put(key, values);
    }

    public String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(VERSION).append(code).append(" ").append(message).append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        responseBuilder.append("\r\n");
        responseBuilder.append(responseBody);

        return responseBuilder.toString();
    }
}
