import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailReminderSystem extends JFrame implements ActionListener {

    private JTextField subjectField, recipientField, messageField;
    private JButton addReminderButton, sendTestEmailButton;
    private JTable reminderTable;
    private DefaultTableModel tableModel;
    private Timer timer;

    private List<Reminder> reminders;

    public EmailReminderSystem() {
        super("Email Reminder System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Initialize components
        JPanel northPanel = new JPanel(new GridLayout(3, 2));
        northPanel.add(new JLabel("Subject:"));
        subjectField = new JTextField(20);
        northPanel.add(subjectField);
        northPanel.add(new JLabel("Recipient:"));
        recipientField = new JTextField(20);
        northPanel.add(recipientField);
        northPanel.add(new JLabel("Message:"));
        messageField = new JTextField(20);
        northPanel.add(messageField);
        northPanel.add(addReminderButton = new JButton("Add Reminder"));
        northPanel.add(sendTestEmailButton = new JButton("Send Test Email"));

        // Initialize table
        String[] columnNames = {"Subject", "Recipient", "Date/Time", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reminderTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reminderTable);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize reminders list
        reminders = new ArrayList<>();

        // Add listeners
        addReminderButton.addActionListener(this);
        sendTestEmailButton.addActionListener(this);

        // Start the timer for checking reminders
        startTimer();

        setVisible(true);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkReminders();
            }
        }, 0, 60000); // Check every minute
    }

    private void checkReminders() {
        Date currentDateTime = new Date();
        for (Reminder reminder : reminders) {
            if (reminder.getDateTime().before(currentDateTime) && !reminder.isSent()) {
                sendEmail(reminder.getRecipient(), reminder.getSubject(), reminder.getMessage());
                reminder.setSent(true);
                updateTable();
            }
        }
    }

    private void sendEmail(String recipient, String subject, String message) {
        // Replace with your actual email credentials
        String from = "your_email@example.com";
        String host = "smtp.gmail.com"; // Or your email provider's SMTP server
        String password = "your_email_password";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587"); // Or the appropriate port for your SMTP server

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            msg.setSubject(subject);
            msg.setText(message);

            Transport.send(msg);
            System.out.println("Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending email.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addReminder() {
        String subject = subjectField.getText();
        String recipient = recipientField.getText();
        String message = messageField.getText();

        // Get reminder date/time from user (e.g., using a date/time picker)
        Date reminderDateTime = new Date(); // Placeholder

        Reminder reminder = new Reminder(subject, recipient, message, reminderDateTime);
        reminders.add(reminder);
        updateTable();

        subjectField.setText("");
        recipientField.setText("");
        messageField.setText("");
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Reminder reminder : reminders) {
            Object[] row = {reminder.getSubject(), reminder.getRecipient(),
                    reminder.getDateTime(), reminder.isSent() ? "Sent" : "Pending"};
            tableModel.addRow(row);
        }
    }

    private void sendTestEmail() {
        String recipient = recipientField.getText();
        String subject = "Test Email";
        String message = "This is a test email from the Email Reminder System.";
        sendEmail(recipient, subject, message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmailReminderSystem();
        });
    }
}

// Reminder class
class Reminder {
    private String subject;
    private String recipient;
    private String message;
    private Date dateTime;
    private boolean sent;

    public Reminder(String subject, String recipient, String message, Date dateTime) {
        this.subject = subject;
        this.recipient = recipient;
        this.message = message;
        this.dateTime = dateTime;
        this.sent = false;
    }

    // Getters and setters for subject, recipient, message, dateTime, and sent
}

