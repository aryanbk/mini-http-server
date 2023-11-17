import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    try (ServerSocket serverSocket = new ServerSocket(4221)) {
      serverSocket.setReuseAddress(true);

      while (true) {
        try (Socket clienSocket = serverSocket.accept()) {
          String httpResponse200;
          String httpResponse404 = "HTTP/1.1 404 Not Found\r\n\r\n";

          InputStreamReader isr = new InputStreamReader(clienSocket.getInputStream());
          BufferedReader reader = new BufferedReader(isr);
          String line = reader.readLine();

          for (int lineNumber = 0; !line.isEmpty(); ++lineNumber) {
            if (lineNumber == 0) {
              String[] parts = line.split(" ");
              String urlPath;

              if (parts[0].equals("GET")) {
                urlPath = parts[1];
                String[] resources = urlPath.split("/");

                if (resources.length == 3 && resources[1].equals("echo")) {
                  httpResponse200 = MessageFormat.format(
                      "HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\nContent-Length: {0}\r\n\r\n{1}",
                      resources[2].length(), resources[2]);
                  clienSocket.getOutputStream().write(httpResponse200.getBytes("UTF-8"));
                } else {
                  clienSocket.getOutputStream().write(httpResponse404.getBytes("UTF-8"));
                }
              }
            }

            line = reader.readLine();
          }

          // clienSocket.getOutputStream().write(httpResponse200.getBytes("UTF-8"));
        } catch (Exception e) {
          System.out.println("ClientSocket Exception: " + e.getMessage());
        }
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
