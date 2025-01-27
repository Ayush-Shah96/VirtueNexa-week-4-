import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileOrganizer extends JFrame implements ActionListener {

    private JTextField sourceDirectoryField;
    private JButton browseSourceButton;
    private JButton organizeButton;
    private JTextArea logArea;

    public FileOrganizer() {
        super("File Organizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Create GUI components
        JPanel northPanel = new JPanel(new FlowLayout());
        JLabel sourceDirectoryLabel = new JLabel("Source Directory:");
        sourceDirectoryField = new JTextField(30);
        browseSourceButton = new JButton("Browse");
        northPanel.add(sourceDirectoryLabel);
        northPanel.add(sourceDirectoryField);
        northPanel.add(browseSourceButton);

        logArea = new JTextArea(15, 40);
        logArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logArea);

        JPanel southPanel = new JPanel(new FlowLayout());
        organizeButton = new JButton("Organize Files");
        southPanel.add(organizeButton);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        browseSourceButton.addActionListener(this);
        organizeButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == browseSourceButton) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File selectedDirectory = fileChooser.getSelectedFile();
                sourceDirectoryField.setText(selectedDirectory.getAbsolutePath());
            }
        } else if (e.getSource() == organizeButton) {
            String sourceDirectory = sourceDirectoryField.getText();
            if (!sourceDirectory.isEmpty()) {
                organizeFiles(sourceDirectory);
            } else {
                showError("Please select a source directory.");
            }
        }
    }

    private void organizeFiles(String sourceDirectory) {
        try {
            Files.walk(Paths.get(sourceDirectory))
                    .forEach(path -> {
                        if (Files.isRegularFile(path)) {
                            String extension = getFileExtension(path.toString());
                            String destinationDirectory = getDestinationDirectory(extension);

                            if (!destinationDirectory.isEmpty()) {
                                try {
                                    Path targetPath = Paths.get(destinationDirectory, path.getFileName().toString());
                                    Files.move(path, targetPath, StandardCopyOption.REPLACE_EXISTING);
                                    log("Moved " + path + " to " + targetPath);
                                } catch (IOException ex) {
                                    logError("Error moving file: " + ex.getMessage());
                                }
                            }
                        }
                    });
        } catch (IOException ex) {
            logError("Error organizing files: " + ex.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1);
        } else {
            return "";
        }
    }

    private String getDestinationDirectory(String extension) {
        // Define your destination directory mapping here
        // Example:
        Map<String, String> extensionsToDirectories = new HashMap<>();
        extensionsToDirectories.put("jpg", "images");
        extensionsToDirectories.put("jpeg", "images");
        extensionsToDirectories.put("png", "images");
        extensionsToDirectories.put("gif", "images");
        extensionsToDirectories.put("mp3", "music");
        extensionsToDirectories.put("wav", "music");
        extensionsToDirectories.put("mp4", "videos");
        extensionsToDirectories.put("avi", "videos");
        extensionsToDirectories.put("doc", "documents");
        extensionsToDirectories.put("docx", "documents");
        extensionsToDirectories.put("pdf", "documents");
        extensionsToDirectories.put("txt", "documents");

        return extensionsToDirectories.getOrDefault(extension.toLowerCase(), "");
    }

    private void log(String message) {
        logArea.append(message + "\n");
    }

    private void logError(String message) {
        logArea.append("Error: " + message + "\n");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FileOrganizer();
        });
    }
}

