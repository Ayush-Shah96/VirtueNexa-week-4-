import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ElectronicVotingSystem extends JFrame implements ActionListener {

    private JComboBox<String> candidateComboBox;
    private JButton voteButton;
    private JTextArea voterLogArea;
    private JTextArea resultArea;

    private String dbUrl = "jdbc:mysql://localhost:3306/voting_system"; // Replace with your database URL
    private String dbUser = "your_username"; // Replace with your database username
    private String dbPassword = "your_password"; // Replace with your database password

    public ElectronicVotingSystem() {
        super("Electronic Voting System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Create GUI components
        JPanel northPanel = new JPanel(new FlowLayout());
        JLabel candidateLabel = new JLabel("Select Candidate:");
        candidateComboBox = new JComboBox<>(); // Populate with candidates from database
        voteButton = new JButton("Vote");
        northPanel.add(candidateLabel);
        northPanel.add(candidateComboBox);
        northPanel.add(voteButton);

        voterLogArea = new JTextArea(15, 40);
        voterLogArea.setEditable(false);
        JScrollPane voterLogScrollPane = new JScrollPane(voterLogArea);

        resultArea = new JTextArea(15, 40);
        resultArea.setEditable(false);
        JScrollPane resultScrollPane = new JScrollPane(resultArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, voterLogScrollPane, resultScrollPane);
        splitPane.setDividerLocation(400);

        add(northPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        // Add listener
        voteButton.addActionListener(this);

        // Load candidates from database
        loadCandidates();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == voteButton) {
            String selectedCandidate = (String) candidateComboBox.getSelectedItem();
            if (selectedCandidate != null) {
                try {
                    // 1. Record vote in database (Replace with secure hashing or encryption)
                    String voterId = "12345"; // Replace with actual voter ID authentication
                    String sql = "INSERT INTO votes (voter_id, candidate_name) VALUES (?, ?)";
                    try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                         PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, voterId);
                        statement.setString(2, selectedCandidate);
                        statement.executeUpdate();
                    }

                    // 2. Update voter log
                    voterLogArea.append("Voter ID: " + voterId + " voted for " + selectedCandidate + "\n");

                    // 3. Update results
                    updateResults();

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error voting.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void loadCandidates() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT candidate_name FROM candidates")) {
            while (resultSet.next()) {
                String candidateName = resultSet.getString("candidate_name");
                candidateComboBox.addItem(candidateName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading candidates.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateResults() {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT candidate_name, COUNT(*) as votes " +
                     "FROM votes " +
                     "GROUP BY candidate_name " +
                     "ORDER BY votes DESC")) {
            resultArea.setText("Election Results:\n\n");
            while (resultSet.next()) {
                String candidateName = resultSet.getString("candidate_name");
                int votes = resultSet.getInt("votes");
                resultArea.append(candidateName + ": " + votes + " votes\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating results.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ElectronicVotingSystem();
        });
    }
}

