import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatClientGUI extends JFrame implements ActionListener, Runnable {

    private JTextField messageField;
    private JTextArea chatArea;
    private JButton sendButton;
    private JButton fileButton;
    private JFileChooser fileChooser;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private List<String> onlineUsers;

    public ChatClientGUI() {
        super("Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Create GUI components
        JPanel northPanel = new JPanel(new BorderLayout());
        messageField = new JTextField(30);
        sendButton = new JButton("Send");
        fileButton = new JButton("File");
        northPanel.add(messageField, BorderLayout.CENTER);
        northPanel.add(sendButton, BorderLayout.EAST);
        northPanel.add(fileButton, BorderLayout.WEST);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize file chooser
        fileChooser = new JFileChooser();

        // Add listeners
        sendButton.addActionListener(this);
        fileButton.addActionListener(this);

        // Initialize online users list
        onlineUsers = new ArrayList<>();

        setVisible(true);
    }

    public void connectToServer(String serverAddress, int port, String username) {
        try {
            this.username = username;
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to server
            out.println(username);

            // Start listening for messages
            Thread thread = new Thread(this);
            thread.start();

        } catch (IOException e) {
            showError("Error connecting to server: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("USER_LIST")) {
                    // Update online users list
                    String[] users = message.substring(10).split(",");
                    updateOnlineUsers(users);
                } else {
                    // Display received message
                    displayMessage(message);
                }
            }
        } catch (IOException e) {
            showError("Error receiving messages: " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendButton) {
            String message = messageField.getText();
            if (!message.isEmpty()) {
                out.println(message);
                messageField.setText("");
            }
        } else if (e.getSource() == fileButton) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                sendFile(file);
            }
        }
    }

    private void sendFile(File file) {
        try {
            // Send file name to server
            out.println("FILE:" + file.getName());

            // Send file content
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[4096];
            int bytesRead;
            OutputStream os = socket.getOutputStream();
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            fis.close();
            os.flush();

            displayMessage("File sent: " + file.getName());

        } catch (IOException e) {
            showError("Error sending file: " + e.getMessage());
        }
    }

    private void displayMessage(String message) {
        chatArea.append(message + "\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void updateOnlineUsers(String[] users) {
        onlineUsers.clear();
        for (String user : users) {
            onlineUsers.add(user);
        }
        // Update user list UI (e.g., in a separate panel)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChatClientGUI client = new ChatClientGUI();
            // Get server address, port, and username from user input
            String serverAddress = JOptionPane.showInputDialog("Enter server address:");
            String portString = JOptionPane.showInputDialog("Enter server port:");
            int port = Integer.parseInt(portString);
            String username = JOptionPane.showInputDialog("Enter your username:");
            client.connectToServer(serverAddress, port, username);
        });
    }
}

