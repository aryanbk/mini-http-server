import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import service.ClientHandler;

public class Main {
  public static void main(String[] args) {
    System.out.println("Logs from your program will appear here!");
    try (ServerSocket serverSocket = new ServerSocket(4221)) {
      serverSocket.setReuseAddress(true);

      while (true) {
        Socket clienSocket = serverSocket.accept();
        new Thread(new ClientHandler(clienSocket)).start();
        // try (Socket clienSocket = serverSocket.accept()) {
        // new Thread(new ClientHandler(clienSocket)).start();
        // } catch (Exception e) {
        // System.out.println("ClientSocket Exception: " + e.getMessage());
        // }
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
