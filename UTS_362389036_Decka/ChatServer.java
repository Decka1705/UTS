import java.io.*;
import java.net.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private ServerSocket serverSocket;
    private ExecutorService executorService;
    private ChatServer.Client[] clients;

    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(2);
        System.out.println("Server listening on port " + port);
    }

    public void start() throws IOException {
        while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connected: " + clientSocket.getInetAddress());

            // Menangani koneksi client secara bersamaan
            executorService.submit(() -> {
                try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
                    String message = (String) in.readObject();
                    System.out.println("Received from client: " + message);

                    // Mengirim pesan ke client lain
                    for (Socket otherClient : getConnectedClients()) {
                        if (otherClient!= clientSocket) {
                            sendMessage(otherClient, message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });

            // Menutup socket client
            clientSocket.close();
        }
    }

    private void sendMessage(Socket socket, String message) throws IOException {
        OutputStream out = socket.getOutputStream();
        out.write(message.getBytes());
        out.flush();
    }

    private Set<Socket> getConnectedClients() {
        Set<Socket> connectedClients = new HashSet<>();
        for (Client client : clients) {
            connectedClients.add(client.clientSocket);
        }
        return connectedClients;
    }

    public void stop() throws IOException {
        serverSocket.close();
        executorService.shutdown();
        for (Client client : clients) {
            client.clientSocket.close();
        }
    }

    private class Client {
        public Socket clientSocket;
    }

    public static void main(String[] args) throws IOException {
        int port = 1234;
        ChatServer server = new ChatServer(port);
        server.start();
    }
}