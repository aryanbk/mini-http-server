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
        Socket clienSocket = serverSocket.accept();
        InputStreamReader isr = new InputStreamReader(clienSocket.getInputStream());
        BufferedReader reader = new BufferedReader(isr);
        String line = reader.readLine();
        while (!line.isEmpty()) {
          System.out.println(line);
          line = reader.readLine();
        }
        System.out.println("Spin...." + spinCount);
        ++spinCount;
      }

    } catch (IOException e) {
      System.out.println("IOException: " + e.getMessage());
    }
  }
}
