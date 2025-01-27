import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class FinancialTracker extends JFrame implements ActionListener {

    private JTextField goalAmountField, expenseAmountField, expenseDescriptionField;
    private JButton addGoalButton, addExpenseButton, generateReportButton;
    private JTextArea reportArea;
    private DefaultListModel<String> goalListModel;
    private JList<String> goalList;
    private DefaultListModel<String> expenseListModel;
    private JList<String> expenseList;

    private List<Goal> goals;
    private List<Expense> expenses;

    public FinancialTracker() {
        super("Financial Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize data
        goals = new ArrayList<>();
        expenses = new ArrayList<>();

        // Initialize GUI components
        JPanel northPanel = new JPanel(new GridLayout(2, 2));
        northPanel.add(new JLabel("Goal Amount:"));
        goalAmountField = new JTextField(10);
        northPanel.add(goalAmountField);
        northPanel.add(new JLabel("Expense Amount:"));
        expenseAmountField = new JTextField(10);
        northPanel.add(expenseAmountField);
        northPanel.add(new JLabel("Expense Description:"));
        expenseDescriptionField = new JTextField(20);
        northPanel.add(expenseDescriptionField);
        northPanel.add(addGoalButton = new JButton("Add Goal"));
        northPanel.add(addExpenseButton = new JButton("Add Expense"));

        goalListModel = new DefaultListModel<>();
        goalList = new JList<>(goalListModel);
        JScrollPane goalScrollPane = new JScrollPane(goalList);

        expenseListModel = new DefaultListModel<>();
        expenseList = new JList<>(expenseListModel);
        JScrollPane expenseScrollPane = new JScrollPane(expenseList);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, goalScrollPane, expenseScrollPane);
        splitPane.setDividerLocation(400);

        reportArea = new JTextArea(10, 50);
        reportArea.setEditable(false);
        JScrollPane reportScrollPane = new JScrollPane(reportArea);

        JPanel southPanel = new JPanel();
        southPanel.add(generateReportButton = new JButton("Generate Report"));

        add(northPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
        add(reportScrollPane, BorderLayout.SOUTH);

        // Add listeners
        addGoalButton.addActionListener(this);
        addExpenseButton.addActionListener(this);
        generateReportButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addGoalButton) {
            addGoal();
        } else if (e.getSource() == addExpenseButton) {
            addExpense();
        } else if (e.getSource() == generateReportButton) {
            generateReport();
        }
    }

    private void addGoal() {
        try {
            double goalAmount = Double.parseDouble(goalAmountField.getText());
            goals.add(new Goal(goalAmount));
            goalListModel.addElement("Goal: $" + goalAmount);
            goalAmountField.setText("");
        } catch (NumberFormatException ex) {
            showError("Invalid goal amount.");
        }
    }

    private void addExpense() {
        try {
            double expenseAmount = Double.parseDouble(expenseAmountField.getText());
            String description = expenseDescriptionField.getText();
            expenses.add(new Expense(expenseAmount, description));
            expenseListModel.addElement(description + ": $" + expenseAmount);
            expenseAmountField.setText("");
            expenseDescriptionField.setText("");
        } catch (NumberFormatException ex) {
            showError("Invalid expense amount.");
        }
    }

    private void generateReport() {
        reportArea.setText("Financial Report\n\n");

        // Calculate total expenses
        double totalExpenses = 0.0;
        for (Expense expense : expenses) {
            totalExpenses += expense.getAmount();
        }

        // Calculate savings progress for each goal
        for (Goal goal : goals) {
            double savingsProgress = 0.0; // Calculate savings progress based on expenses and income (if applicable)
            reportArea.append("Goal: $" + goal.getAmount() + " - Savings Progress: " + savingsProgress + "%\n");
        }

        reportArea.append("\nTotal Expenses: $" + totalExpenses + "\n");
        // Add more report details as needed (e.g., income, net worth)
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new FinancialTracker();
        });
    }
}

// Goal class
class Goal {
    private double amount;

    public Goal(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }
}

// Expense class
class Expense {
    private double amount;
    private String description;

    public Expense(double amount, String description) {
        this.amount = amount;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}

