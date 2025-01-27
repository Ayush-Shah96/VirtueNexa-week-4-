import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibraryBookRecommender extends JFrame implements ActionListener {

    private JTextField userIdField;
    private JButton recommendButton;
    private JTextArea recommendationArea;

    private String dbUrl = "jdbc:mysql://localhost:3306/library"; // Replace with your database URL
    private String dbUser = "your_username"; // Replace with your database username
    private String dbPassword = "your_password"; // Replace with your database password

    public LibraryBookRecommender() {
        super("Library Book Recommender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLayout(new BorderLayout());

        // Create GUI components
        JPanel northPanel = new JPanel(new FlowLayout());
        userIdField = new JTextField(10);
        recommendButton = new JButton("Recommend Books");
        northPanel.add(new JLabel("User ID:"));
        northPanel.add(userIdField);
        northPanel.add(recommendButton);

        recommendationArea = new JTextArea(15, 40);
        recommendationArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(recommendationArea);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add listener
        recommendButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == recommendButton) {
            int userId = Integer.parseInt(userIdField.getText());
            List<String> recommendations = getBookRecommendations(userId);

            recommendationArea.setText(""); // Clear previous recommendations
            for (String book : recommendations) {
                recommendationArea.append(book + "\n");
            }
        }
    }

    private List<String> getBookRecommendations(int userId) {
        List<String> recommendations = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(
                     "SELECT b.title " +
                             "FROM books b " +
                             "JOIN genres g ON b.genre_id = g.genre_id " +
                             "JOIN user_genres ug ON g.genre_id = ug.genre_id " +
                             "JOIN user_history uh ON uh.user_id = " + userId + " AND uh.book_id = b.book_id " +
                             "GROUP BY b.title " +
                             "ORDER BY COUNT(*) DESC " +
                             "LIMIT 5;")) { // Recommend top 5 books

            while (resultSet.next()) {
                String bookTitle = resultSet.getString("title");
                recommendations.add(bookTitle);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving recommendations.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return recommendations;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryBookRecommender();
        });
    }
}

