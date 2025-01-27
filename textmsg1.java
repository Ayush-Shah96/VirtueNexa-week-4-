import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {

    private int port;
    private List<ClientHandler> clients;

    public ChatServer(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                clientHandler.start();
            }

        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public void broadcastUserList() {
        StringBuilder userList = new StringBuilder("USER_LIST:");
        for (ClientHandler client : clients) {
            userList.append(client.getUsername()).append(",");
        }
        if (userList.length() > 10) {
            userList.deleteCharAt(userList.length() - 1); // Remove trailing comma
        }
        for (ClientHandler client : clients) {
            client.sendMessage(userList.toString());
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);
        broadcastUserList();
    }

    public static void main(String[] args) {
        int port = 8000; // Default port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number.");
                System.exit(1);
            }
        }
        ChatServer server = new ChatServer(port);
        server.start();
    }
}

// ClientHandler class
class ClientHandler extends Thread {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private ChatServer server;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        try {
            username = in.readLine();
            server.broadcastUserList();

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("FILE:")) {
                    // Handle file transfer
                    String filename = message.substring(5);
                    System.out.println("Receiving file: " + filename);
                    File file = new File(filename);
                    FileOutputStream fos = new FileOutputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    InputStream is = socket.getInputStream();
                    while ((bytesRead = is.read(buffer))

