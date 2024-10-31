import java.io.*;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 8888);

            // Mengirim pesan ke server
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
            writer.write("PING!");
            writer.newLine();
            writer.flush();

            // Menutup socket client
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}