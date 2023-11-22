package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
        try (InputStreamReader isr = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(isr)) {

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
            if (contentLength > 0) {
                char[] body = new char[contentLength];
                reader.read(body, 0, contentLength);
                requestBuilder.append(body);
                System.out.println("Content Length: " + contentLength);
                System.out.println("Body: " + new String(body));
            }

            HttpRequest httpRequest = new HttpRequest(requestBuilder.toString());
            HttpRespose response = RequestHandler.handleRequest(httpRequest, args);

            clientSocket.getOutputStream().write(response.getResponse().getBytes("UTF-8"));

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
