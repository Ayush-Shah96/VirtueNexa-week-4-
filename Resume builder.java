import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ResumeBuilder extends JFrame implements ActionListener {

    private JComboBox<String> templateComboBox;
    private JTextField nameField, phoneField, emailField, summaryField;
    private JTextArea experienceArea, educationArea, skillsArea;
    private JButton generateButton, saveButton;
    private JFileChooser fileChooser;

    private Map<String, String> templates;

    public ResumeBuilder() {
        super("Resume Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize templates (replace with actual template files)
        templates = new HashMap<>();
        templates.put("Template 1", "path/to/template1.html");
        templates.put("Template 2", "path/to/template2.html");
        templates.put("Template 3", "path/to/template3.html");

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(2, 2));
        northPanel.add(new JLabel("Template:"));
        templateComboBox = new JComboBox<>(templates.keySet().toArray(new String[0]));
        northPanel.add(templateComboBox);
        northPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        northPanel.add(nameField);
        northPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField(20);
        northPanel.add(new JLabel("Email:"));
        emailField = new JTextField(20);
        northPanel.add(summaryField = new JTextField(20));
        northPanel.add(new JLabel("Summary:"));

        JPanel centerPanel = new JPanel(new GridLayout(3, 1));
        centerPanel.add(new JLabel("Experience:"));
        experienceArea = new JTextArea(5, 20);
        centerPanel.add(new JScrollPane(experienceArea));
        centerPanel.add(new JLabel("Education:"));
        educationArea = new JTextArea(5, 20);
        centerPanel.add(new JScrollPane(educationArea));
        centerPanel.add(new JLabel("Skills:"));
        skillsArea = new JTextArea(5, 20);
        centerPanel.add(new JScrollPane(skillsArea));

        JPanel southPanel = new JPanel();
        generateButton = new JButton("Generate Resume");
        saveButton = new JButton("Save Resume");
        southPanel.add(generateButton);
        southPanel.add(saveButton);

        // Initialize file chooser
        fileChooser = new JFileChooser();

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        generateButton.addActionListener(this);
        saveButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == generateButton) {
            generateResume();
        } else if (e.getSource() == saveButton) {
            saveResume();
        }
    }

    private void generateResume() {
        String templateName = (String) templateComboBox.getSelectedItem();
        String templatePath = templates.get(templateName);

        if (templatePath == null) {
            showError("Template not found.");
            return;
        }

        try {
            String resumeContent = loadTemplate(templatePath);

            // Replace placeholders with user data
            resumeContent = resumeContent
                    .replace("{NAME}", nameField.getText())
                    .replace("{PHONE}", phoneField.getText())
                    .replace("{EMAIL}", emailField.getText())
                    .replace("{SUMMARY}", summaryField.getText())
                    .replace("{EXPERIENCE}", experienceArea.getText())
                    .replace("{EDUCATION}", educationArea.getText())
                    .replace("{SKILLS}", skillsArea.getText());

            // Display or preview the generated resume (e.g., in a separate window)
            JOptionPane.showMessageDialog(this, resumeContent, "Generated Resume", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException ex) {
            showError("Error loading template: " + ex.getMessage());
        }
    }

    private void saveResume() {
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(generateResumeContent());
                writer.close();
                JOptionPane.showMessageDialog(this, "Resume saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                showError("Error saving resume: " + ex.getMessage());
            }
        }
    }

    private String loadTemplate(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    private String generateResumeContent() {
        // Same logic as in generateResume() to create the resume content
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String args) {
        SwingUtilities.invokeLater(() -> {
            new ResumeBuilder();
        });
    }
}

