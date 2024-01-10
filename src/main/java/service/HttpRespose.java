package service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
// import java.util.Arrays;
// import java.util.Base64;

public class HttpRespose {

    public static final String RESPONSE_404 = "HTTP/1.1 404 Not Found\r\n\r\n";
    public static final String VERSION = "HTTP/1.1 ";
    private static final String CONTENT_ENCODING = "Content-Encoding";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    String version;
    String code;
    String message;
    LinkedHashMap<String, String> headers;
    byte[] body;

    // public HttpRespose(String code, String message, LinkedHashMap<String, String>
    // headers, String body) {
    // this.version = "1.1";
    // this.code = code;
    // this.message = message;
    // this.headers = headers;
    // this.body = body;
    // }

    // public HttpRespose(String version, String code, String message,
    // LinkedHashMap<String, String> headers,
    // String body) {
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

    public HttpRespose(String code, String message, String body) {
        this.version = "1.1";
        this.code = code;
        this.message = message;
        this.headers = new LinkedHashMap<>();
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    public HttpRespose(String codeAndMessage, String body) {
        this.code = codeAndMessage.substring(0, 3);
        this.message = codeAndMessage.substring(4, codeAndMessage.length());
        this.body = body.getBytes(StandardCharsets.UTF_8);
        this.headers = new LinkedHashMap<>();
    }

    public void setHeaders(String key, String values) {
        headers.put(key, values);
    }

    // public String getResponse() throws IOException {
    // if (headers.getOrDefault(CONTENT_ENCODING, "").equals("gzip")) {
    // handleCompression();
    // }
    // StringBuilder responseBuilder = new StringBuilder();
    // responseBuilder.append(VERSION).append(code).append("
    // ").append(message).append("\r\n");
    // for (Map.Entry<String, String> header : headers.entrySet()) {
    // responseBuilder.append(header.getKey()).append(":
    // ").append(header.getValue()).append("\r\n");
    // }
    // responseBuilder.append("\r\n");
    // responseBuilder.append(body);

    // printResponse();
    // return responseBuilder.toString();
    // }

    public byte[] getResponse() throws IOException {
        if (headers.getOrDefault(CONTENT_ENCODING, "").equals("gzip")) {
            handleCompression();
        }

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

    private void handleCompression() throws IOException {
        setHeaders(CONTENT_ENCODING, "gzip");
        body = compress(body);
        setHeaders(CONTENT_LENGTH_HEADER, Long.toString(body.length));
        System.out.println("compressed body ->" + body);
    }

    // public String compress(String str) throws IOException {
    // if (str == null || str.length() == 0) {
    // return str;
    // }

    // ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    // GZIPOutputStream gzipOutputStream = new
    // GZIPOutputStream(byteArrayOutputStream);

    // gzipOutputStream.write(str.getBytes("UTF-8"));
    // gzipOutputStream.close();
    // byteArrayOutputStream.close();

    // byte[] compressedBytes = byteArrayOutputStream.toByteArray();
    // System.out.println(Arrays.toString(compressedBytes));
    // String resBody = new String(compressedBytes, StandardCharsets.UTF_16);
    // System.out.println(resBody);
    // byte[] resByte = resBody.getBytes("UTF-08");
    // System.out.println(Arrays.toString(resByte));
    // return Base64.getEncoder().encodeToString(compressedBytes);
    // }

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try (GZIPOutputStream gzipOS = new GZIPOutputStream(bos)) {
            gzipOS.write(data);
        }
        byte[] compressedBytes = bos.toByteArray();
        System.out.println(Arrays.toString(compressedBytes));
        return compressedBytes;
    }

    void printResponse() {
        System.out.println(
                "\n-------respose start-------\n" + code + "\n" + message + "\n" + headers + "\n"
                        + body.toString() + " " + new String(body, StandardCharsets.UTF_8)
                        + "\n-------respose end-------\n");
    }
}
