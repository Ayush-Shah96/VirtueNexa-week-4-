import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveManagementSystem extends JFrame implements ActionListener {

    private JComboBox<String> employeeComboBox;
    private JComboBox<String> leaveTypeComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JTextArea reasonTextArea;
    private JButton applyButton;
    private JButton approveButton;
    private JButton rejectButton;
    private JTextArea leaveRequestsArea;
    private JLabel employeeLabel;
    private JLabel leaveTypeLabel;
    private JLabel startDateLabel;
    private JLabel endDateLabel;
    private JLabel reasonLabel;
    private JLabel leaveBalanceLabel;
    private JLabel leaveBalanceValueLabel;

    private List<Employee> employees;
    private List<LeaveRequest> leaveRequests;

    public LeaveManagementSystem() {
        super("Leave Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize data
        employees = new ArrayList<>();
        employees.add(new Employee("John Doe", 20));
        employees.add(new Employee("Jane Smith", 15));
        // Add more employees as needed

        leaveRequests = new ArrayList<>();

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(5, 2));
        employeeLabel = new JLabel("Employee:");
        employeeComboBox = new JComboBox<>(getEmployeeNames());
        leaveTypeLabel = new JLabel("Leave Type:");
        leaveTypeComboBox = new JComboBox<>(new String[]{"Sick", "Casual", "Vacation"});
        startDateLabel = new JLabel("Start Date:");
        startDateField = new JTextField(10);
        endDateLabel = new JLabel("End Date:");
        endDateField = new JTextField(10);
        reasonLabel = new JLabel("Reason:");
        reasonTextArea = new JTextArea(5, 20);
        applyButton = new JButton("Apply");
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");

        northPanel.add(employeeLabel);
        northPanel.add(employeeComboBox);
        northPanel.add(leaveTypeLabel);
        northPanel.add(leaveTypeComboBox);
        northPanel.add(startDateLabel);
        northPanel.add(startDateField);
        northPanel.add(endDateLabel);
        northPanel.add(endDateField);
        northPanel.add(reasonLabel);
        northPanel.add(new JScrollPane(reasonTextArea));
        northPanel.add(applyButton);
        northPanel.add(new JPanel()); // Empty panel for spacing
        northPanel.add(approveButton);
        northPanel.add(rejectButton);

        leaveRequestsArea = new JTextArea(20, 50);
        leaveRequestsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(leaveRequestsArea);

        leaveBalanceLabel = new JLabel("Leave Balance:");
        leaveBalanceValueLabel = new JLabel("0");

        JPanel southPanel = new JPanel();
        southPanel.add(leaveBalanceLabel);
        southPanel.add(leaveBalanceValueLabel);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        applyButton.addActionListener(this);
        approveButton.addActionListener(this);
        rejectButton.addActionListener(this);
        employeeComboBox.addActionListener(this);

        // Update leave requests area initially
        updateLeaveRequestsArea();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == applyButton) {
            applyForLeave();
        } else if (e.getSource() == approveButton) {
            approveLeave();
        } else if (e.getSource() == rejectButton) {
            rejectLeave();
        } else if (e.getSource() == employeeComboBox) {
            updateLeaveBalance();
        }
    }

    private void applyForLeave() {
        String employeeName = (String) employeeComboBox.getSelectedItem();
        String leaveType = (String) leaveTypeComboBox.getSelectedItem();
        String startDate = startDateField.getText();
        String endDate = endDateField.getText();
        String reason = reasonTextArea.getText();

        Employee employee = getEmployeeByName(employeeName);
        if (employee != null) {
            LeaveRequest request = new LeaveRequest(employee, leaveType, startDate, endDate, reason);
            leaveRequests.add(request);
            updateLeaveRequestsArea();
            updateLeaveBalance();
            clearForm();
        } else {
            showError("Employee not found.");
        }
    }

    private void approveLeave() {
        if (getSelectedRequest() != null) {
            getSelectedRequest().setStatus("Approved");
            updateLeaveRequestsArea();
            updateLeaveBalance();
        } else {
            showError("Please select a leave request.");
        }
    }

    private void rejectLeave() {
        if (getSelectedRequest() != null) {
            getSelectedRequest().setStatus("Rejected");
            updateLeaveRequestsArea();
            updateLeaveBalance();
        } else {
            showError("Please select a leave request.");
        }
    }

    private void updateLeaveRequestsArea() {
        StringBuilder sb = new StringBuilder();
        for (LeaveRequest request : leaveRequests) {
            sb.append(request.toString()).append("\n");
        }
        leaveRequestsArea.setText(sb.toString());
    }

    private void updateLeaveBalance() {
        String employeeName = (String) employeeComboBox.getSelectedItem();
        Employee employee = getEmployeeByName(employeeName);
        if (employee != null) {
            int leaveBalance = employee.getLeaveBalance();
            // Calculate leave balance based on approved/rejected leaves
            for (LeaveRequest request : leaveRequests) {
                if (request.getEmployee().equals(employee)) {
                    if (request.getStatus().equals("Approved")) {
                        leaveBalance -= calculateLeaveDays(request.getStartDate(), request.getEndDate());
                    }
                }
            }
            leaveBalanceValueLabel.setText(String.valueOf(leaveBalance));
        }
    }

    private void clearForm() {
        startDateField.setText("");
        endDateField.setText("");
        reasonTextArea.setText("");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String[] getEmployeeNames() {
        String[] names = new String[employees.size()];
        for (int i = 0; i < employees.size(); i++) {
            names[i] = employees.get(i).getName();
        }
        return names;
    }

    private Employee getEmployeeByName(String name) {
        for (Employee employee : employees) {
            if (employee.getName().equals(name)) {
                return employee;
            }
        }
        return null;
    }

    private LeaveRequest getSelectedRequest() {
        String selectedText = leaveRequestsArea.getSelectedText();
        if (selectedText != null) {
            String[] parts = selectedText.split(",");
            String employeeName = parts[0].trim();
            Employee employee = getEmployeeByName(employeeName);
            if (employee != null) {
                // Create a LeaveRequest object based on the selected text
                // and return it
            }
        }
        return null;
    }

    private int calculateLeaveDays(String startDate, String endDate) {
        // Implement logic to calculate number of leave days
        // based on start and end dates
        return 0; // Placeholder
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LeaveManagementSystem();
        });
    }
}

// Employee class
class Employee {
    private String name;
    private int leaveBalance;

    public Employee(String name, int leaveBalance) {
        this.name = name;
        this.leaveBalance = leaveBalance;
    }

    public String getName() {
        return name;
    }

    public int getLeaveBalance() {
        return leaveBalance;
    }
}

// LeaveRequest class
class LeaveRequest {
    private Employee employee;
    private String leaveType;
    private String startDate;
    private String endDate;
    private String reason;
    private String status;

    public LeaveRequest(Employee employee, String leaveType, String startDate, String endDate, String reason) {
        this.employee = employee;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.reason = reason;
        this.status = "Pending";
    }

    public Employee getEmployee() {
        return employee;
    }

    public String getStatus() {
        return status;
    }

