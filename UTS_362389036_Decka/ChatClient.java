import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket clientSocket;
    private ObjectInputStream in;

    public ChatClient(String ip, int port, String username) {
        try {
            clientSocket = new Socket(ip, port);
            in = new ObjectInputStream(clientSocket.getInputStream());
            System.out.println("Connected to server: " + ip + ":" + port);
            sendUsername(username);
        } catch (UnknownHostException e) {
            System.err.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        }
    }

    public void sendUsername(String username) throws IOException {
        OutputStream out = clientSocket.getOutputStream();
        out.write(username.getBytes());
        out.flush();
    }

    public void sendMessage(String message) throws IOException {
        out = clientSocket.getOutputStream();
        out.write(message.getBytes());
        out.flush();
    }

    public String receiveMessage() throws IOException, ClassNotFoundException {
        return (String) in.readObject();
    }

    public void disconnect() throws IOException {
        in.close();
        clientSocket.close();
    }

    private OutputStream out;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String ip = "localhost";
        int port = 1234;
        String username = "Client1";

        ChatClient client = new ChatClient(ip, port, username);
        client.sendMessage("PING!");

        while (true) {
            String message = client.receiveMessage();
            System.out.println("Received from server: " + message);
        }
    }
}