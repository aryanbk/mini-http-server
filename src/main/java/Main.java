import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    try {
      ServerSocket serverSocket = new ServerSocket(4221);

      serverSocket.setReuseAddress(true);
      int spinCount = 0;

      while (true) {
        try (Socket clienSocket = serverSocket.accept()) {
          String httpResponse = "HTTP/1.1 200 OK\r\n\r\n";
          clienSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
        } catch (Exception e) {
          System.out.println(e);
        }
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
