import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Task1 {
    public static void main(String[] args) throws IOException {
        try (Socket socket = new Socket()){
            socket.connect(new InetSocketAddress("me.utm.md",80),2000);
            Scanner scanner = new Scanner(socket.getInputStream());
            while (scanner.hasNextLine()){
                System.out.println(scanner.nextLine());
            }
        }
    }
}
