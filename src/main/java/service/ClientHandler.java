package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(isr);) {
            String line = reader.readLine();
            StringBuilder requestBuilder = new StringBuilder();
            while (!line.isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                line = reader.readLine();
            }

            String request = requestBuilder.toString();
            System.out.println("Received request \n" + request);
            HttpRequest httpRequest = new HttpRequest(request);
            String method = httpRequest.method;
            String uri = httpRequest.uri;
            HttpRespose httpRespose;
            String response;

            if (method.equals("GET") && uri.equals("/user-agent")) {
                String content = httpRequest.headers.get("User-Agent");

                httpRespose = new HttpRespose("200", "OK", content);
                httpRespose.headers.put("Content-Type", "text/plain");
                httpRespose.headers.put("Content-Length", Integer.toString(content.length()));

                response = httpRespose.getResponse();
            } else if (method.equals("GET") && uri.startsWith("/echo/") && uri.split("/").length == 3) {
                String content = uri.split("/")[2];

                httpRespose = new HttpRespose("200", "OK", content);
                httpRespose.headers.put("Content-Type", "text/plain");
                httpRespose.headers.put("Content-Length", Integer.toString(content.length()));

                response = httpRespose.getResponse();
            } else if (method.equals("GET") && uri.equals("/")) {
                httpRespose = new HttpRespose("200", "OK", "");

                response = httpRespose.getResponse();
            } else {
                response = "HTTP/1.1 404 Not Found\r\n\r\n";
            }
            clientSocket.getOutputStream().write(response.getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
