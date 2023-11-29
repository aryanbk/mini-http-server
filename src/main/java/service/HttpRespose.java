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
    byte[] body;

    // public HttpRespose(String code, String message, LinkedHashMap<String, String>
    // headers, byte[] body) {
    // this.version = "1.1";
    // this.code = code;
    // this.message = message;
    // this.headers = headers;
    // this.body = body;
    // }

    // public HttpRespose(String version, String code, String message,
    // LinkedHashMap<String, String> headers,
    // byte[] body) {
    // this.version = version;
    // this.code = code;
    // this.message = message;
    // this.headers = headers;
    // this.body = body;
    // }

    // public HttpRespose(String version, String code, String message, String
    // body) {
    // this.version = version;
    // this.code = code;
    // this.message = message;
    // this.headers = new LinkedHashMap<>();
    // this.body = body;
    // }

    public HttpRespose(String code, String message, byte[] body) {
        this.version = "1.1";
        this.code = code;
        this.message = message;
        this.headers = new LinkedHashMap<>();
        this.body = body;
    }

    public HttpRespose(String codeAndMessage, byte[] body) {
        this.code = codeAndMessage.substring(0, 3);
        this.message = codeAndMessage.substring(4, codeAndMessage.length());
        this.body = body;
        this.headers = new LinkedHashMap<>();
    }

    public void setHeaders(String key, String values) {
        headers.put(key, values);
    }

    public byte[] getResponse() {
        StringBuilder headerBuilder = new StringBuilder();
        headerBuilder.append(VERSION).append(code).append(" ").append(message).append("\r\n");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            headerBuilder.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        headerBuilder.append("\r\n");

        byte[] headerBytes = headerBuilder.toString().getBytes();
        byte[] responseBytes = new byte[headerBytes.length + body.length];
        
        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

        printResponse();
        return responseBytes;
    }

    void printResponse() {
        System.out.println(
                "\n-------respose start-------\n" + code + "\n" + message + "\n" + headers + "\n" + new String(body)
                        + "\n-------respose end-------\n");
    }
}