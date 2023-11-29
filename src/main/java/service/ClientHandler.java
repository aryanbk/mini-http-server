package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientHandler implements Runnable {
    private String[] args;
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public ClientHandler(Socket clientSocket, String[] args) {
        this.clientSocket = clientSocket;
        this.args = args;
    }

    @Override
    public void run() {
        try (InputStream is = clientSocket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

            String line;
            int contentLength = 0;
            StringBuilder requestBuilder = new StringBuilder();

            while (!(line = reader.readLine()).isEmpty()) {
                requestBuilder.append(line).append("\r\n");
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }
            requestBuilder.append("\r\n"); // End of headers

            // Read the body
            byte[] body = new byte[contentLength];
            is.read(body, 0, contentLength);
            System.out.println("body- " + new String(body, StandardCharsets.UTF_8));

            HttpRequest httpRequest = new HttpRequest(requestBuilder.toString(), body);

            HttpRespose response = RequestHandler.handleRequest(httpRequest, args);

            clientSocket.getOutputStream().write(response.getResponse());

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