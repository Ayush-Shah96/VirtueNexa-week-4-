import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ParkingManagementSystem extends JFrame implements ActionListener {

    private JComboBox<String> vehicleTypeComboBox;
    private JTextField vehicleNumberField;
    private JButton entryButton;
    private JButton exitButton;
    private JTextArea parkingLogArea;
    private JLabel vehicleTypeLabel;
    private JLabel vehicleNumberLabel;
    private JLabel parkingFeeLabel;
    private JLabel parkingFeeValueLabel;

    private List<ParkingSlot> parkingSlots;
    private List<ParkingRecord> parkingRecords;

    public ParkingManagementSystem() {
        super("Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize data
        parkingSlots = new ArrayList<>();
        for (int i = 1; i <= 10; i++) { // Example: 10 parking slots
            parkingSlots.add(new ParkingSlot(i, "Empty"));
        }
        parkingRecords = new ArrayList<>();

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(3, 2));
        vehicleTypeLabel = new JLabel("Vehicle Type:");
        vehicleTypeComboBox = new JComboBox<>(new String[]{"Car", "Bike", "Truck"});
        vehicleNumberLabel = new JLabel("Vehicle Number:");
        vehicleNumberField = new JTextField(20);
        entryButton = new JButton("Entry");
        exitButton = new JButton("Exit");

        northPanel.add(vehicleTypeLabel);
        northPanel.add(vehicleTypeComboBox);
        northPanel.add(vehicleNumberLabel);
        northPanel.add(vehicleNumberField);
        northPanel.add(entryButton);
        northPanel.add(exitButton);

        parkingLogArea = new JTextArea(20, 50);
        parkingLogArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(parkingLogArea);

        parkingFeeLabel = new JLabel("Parking Fee:");
        parkingFeeValueLabel = new JLabel("0.00");

        JPanel southPanel = new JPanel();
        southPanel.add(parkingFeeLabel);
        southPanel.add(parkingFeeValueLabel);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        entryButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Update parking log initially
        updateParkingLog();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == entryButton) {
            vehicleEntry();
        } else if (e.getSource() == exitButton) {
            vehicleExit();
        }
    }

    private void vehicleEntry() {
        String vehicleType = (String) vehicleTypeComboBox.getSelectedItem();
        String vehicleNumber = vehicleNumberField.getText();

        if (vehicleType.isEmpty() || vehicleNumber.isEmpty()) {
            showError("Please enter vehicle type and number.");
            return;
        }

        ParkingSlot availableSlot = findAvailableSlot();
        if (availableSlot != null) {
            availableSlot.setVehicleType(vehicleType);
            availableSlot.setVehicleNumber(vehicleNumber);
            availableSlot.setEntryTime(new Date());

            ParkingRecord record = new ParkingRecord(vehicleType, vehicleNumber, availableSlot.getSlotNumber(), availableSlot.getEntryTime());
            parkingRecords.add(record);

            updateParkingLog();
            vehicleNumberField.setText("");
        } else {
            showError("No parking slots available.");
        }
    }

    private void vehicleExit() {
        String vehicleNumber = vehicleNumberField.getText();

        if (vehicleNumber.isEmpty()) {
            showError("Please enter vehicle number.");
            return;
        }

        ParkingSlot parkedSlot = findParkedSlot(vehicleNumber);
        if (parkedSlot != null) {
            parkedSlot.setVehicleType("Empty");
            parkedSlot.setVehicleNumber("");
            parkedSlot.setExitTime(new Date());

            // Calculate parking fee
            double fee = calculateParkingFee(parkedSlot.getEntryTime(), parkedSlot.getExitTime(), parkedSlot.getVehicleType());
            parkingFeeValueLabel.setText(String.format("%.2f", fee));

            // Update parking record with exit time and fee
            for (ParkingRecord record : parkingRecords) {
                if (record.getVehicleNumber().equals(vehicleNumber)) {
                    record.setExitTime(parkedSlot.getExitTime());
                    record.setFee(fee);
                    break;
                }
            }

            updateParkingLog();
            vehicleNumberField.setText("");
        } else {
            showError("Vehicle not found in parking.");
        }
    }

    private ParkingSlot findAvailableSlot() {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getVehicleType().equals("Empty")) {
                return slot;
            }
        }
        return null;
    }

    private ParkingSlot findParkedSlot(String vehicleNumber) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getVehicleNumber().equals(vehicleNumber)) {
                return slot;
            }
        }
        return null;
    }

    private double calculateParkingFee(Date entryTime, Date exitTime, String vehicleType) {
        // Implement your own parking fee calculation logic here
        // based on vehicle type and parking duration
        // This is a simplified example
        long durationInMillis = exitTime.getTime() - entryTime.getTime();
        long durationInHours = durationInMillis / (60 * 60 * 1000);
        double feePerHour = 0.0;
        if (vehicleType.equals("Car")) {
            feePerHour = 10.0;
        } else if (vehicleType.equals("Bike")) {
            feePerHour = 5.0;
        } else if (vehicleType.equals("Truck")) {
            feePerHour = 20.0;
        }
        return durationInHours * feePerHour;
    }

    private void updateParkingLog() {
        StringBuilder sb = new StringBuilder();
        for (ParkingSlot slot : parkingSlots) {
            sb.append("Slot ").append(slot.getSlotNumber()).append(": ");
            sb.append(slot.getVehicleType()).append(" - ");
            sb.append(slot.getVehicleNumber()).append("\n");
        }
        sb.append("\nParking Records:\n");
        for (ParkingRecord record : parkingRecords) {
            sb.append(record.toString()).append("\n");
        }
        parkingLogArea.setText(sb.toString());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ParkingManagementSystem();
        });
    }
}

// ParkingSlot class
class ParkingSlot {
    private int slotNumber;
    private String vehicleType;
    private String vehicleNumber;
    private Date entryTime;
    private Date exitTime;

    public ParkingSlot(int slotNumber, String vehicleType) {
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
}

// ParkingRecord class
class ParkingRecord {
    private String vehicleType;
    private String vehicleNumber;
    private int slotNumber;
    private Date entryTime;
    private Date exitTime;
    private double fee;

    public ParkingRecord(String vehicleType, String vehicleNumber, int slotNumber, Date entryTime) {
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.slotNumber =
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;

public class ParkingManagementSystem extends JFrame implements ActionListener {

    private JComboBox<String> vehicleTypeComboBox;
    private JTextField vehicleNumberField;
    private JButton entryButton;
    private JButton exitButton;
    private JTextArea parkingLogArea;
    private JLabel vehicleTypeLabel;
    private JLabel vehicleNumberLabel;
    private JLabel parkingFeeLabel;
    private JLabel parkingFeeValueLabel;

    private List<ParkingSlot> parkingSlots;
    private List<ParkingRecord> parkingRecords;

    public ParkingManagementSystem() {
        super("Parking Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize data
        parkingSlots = new ArrayList<>();
        for (int i = 1; i <= 10; i++) { // Example: 10 parking slots
            parkingSlots.add(new ParkingSlot(i, "Empty"));
        }
        parkingRecords = new ArrayList<>();

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(3, 2));
        vehicleTypeLabel = new JLabel("Vehicle Type:");
        vehicleTypeComboBox = new JComboBox<>(new String[]{"Car", "Bike", "Truck"});
        vehicleNumberLabel = new JLabel("Vehicle Number:");
        vehicleNumberField = new JTextField(20);
        entryButton = new JButton("Entry");
        exitButton = new JButton("Exit");

        northPanel.add(vehicleTypeLabel);
        northPanel.add(vehicleTypeComboBox);
        northPanel.add(vehicleNumberLabel);
        northPanel.add(vehicleNumberField);
        northPanel.add(entryButton);
        northPanel.add(exitButton);

        parkingLogArea = new JTextArea(20, 50);
        parkingLogArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(parkingLogArea);

        parkingFeeLabel = new JLabel("Parking Fee:");
        parkingFeeValueLabel = new JLabel("0.00");

        JPanel southPanel = new JPanel();
        southPanel.add(parkingFeeLabel);
        southPanel.add(parkingFeeValueLabel);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        // Add listeners
        entryButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Update parking log initially
        updateParkingLog();

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == entryButton) {
            vehicleEntry();
        } else if (e.getSource() == exitButton) {
            vehicleExit();
        }
    }

    private void vehicleEntry() {
        String vehicleType = (String) vehicleTypeComboBox.getSelectedItem();
        String vehicleNumber = vehicleNumberField.getText();

        if (vehicleType.isEmpty() || vehicleNumber.isEmpty()) {
            showError("Please enter vehicle type and number.");
            return;
        }

        ParkingSlot availableSlot = findAvailableSlot();
        if (availableSlot != null) {
            availableSlot.setVehicleType(vehicleType);
            availableSlot.setVehicleNumber(vehicleNumber);
            availableSlot.setEntryTime(new Date());

            ParkingRecord record = new ParkingRecord(vehicleType, vehicleNumber, availableSlot.getSlotNumber(), availableSlot.getEntryTime());
            parkingRecords.add(record);

            updateParkingLog();
            vehicleNumberField.setText("");
        } else {
            showError("No parking slots available.");
        }
    }

    private void vehicleExit() {
        String vehicleNumber = vehicleNumberField.getText();

        if (vehicleNumber.isEmpty()) {
            showError("Please enter vehicle number.");
            return;
        }

        ParkingSlot parkedSlot = findParkedSlot(vehicleNumber);
        if (parkedSlot != null) {
            parkedSlot.setVehicleType("Empty");
            parkedSlot.setVehicleNumber("");
            parkedSlot.setExitTime(new Date());

            // Calculate parking fee
            double fee = calculateParkingFee(parkedSlot.getEntryTime(), parkedSlot.getExitTime(), parkedSlot.getVehicleType());
            parkingFeeValueLabel.setText(String.format("%.2f", fee));

            // Update parking record with exit time and fee
            for (ParkingRecord record : parkingRecords) {
                if (record.getVehicleNumber().equals(vehicleNumber)) {
                    record.setExitTime(parkedSlot.getExitTime());
                    record.setFee(fee);
                    break;
                }
            }

            updateParkingLog();
            vehicleNumberField.setText("");
        } else {
            showError("Vehicle not found in parking.");
        }
    }

    private ParkingSlot findAvailableSlot() {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getVehicleType().equals("Empty")) {
                return slot;
            }
        }
        return null;
    }

    private ParkingSlot findParkedSlot(String vehicleNumber) {
        for (ParkingSlot slot : parkingSlots) {
            if (slot.getVehicleNumber().equals(vehicleNumber)) {
                return slot;
            }
        }
        return null;
    }

    private double calculateParkingFee(Date entryTime, Date exitTime, String vehicleType) {
        // Implement your own parking fee calculation logic here
        // based on vehicle type and parking duration
        // This is a simplified example
        long durationInMillis = exitTime.getTime() - entryTime.getTime();
        long durationInHours = durationInMillis / (60 * 60 * 1000);
        double feePerHour = 0.0;
        if (vehicleType.equals("Car")) {
            feePerHour = 10.0;
        } else if (vehicleType.equals("Bike")) {
            feePerHour = 5.0;
        } else if (vehicleType.equals("Truck")) {
            feePerHour = 20.0;
        }
        return durationInHours * feePerHour;
    }

    private void updateParkingLog() {
        StringBuilder sb = new StringBuilder();
        for (ParkingSlot slot : parkingSlots) {
            sb.append("Slot ").append(slot.getSlotNumber()).append(": ");
            sb.append(slot.getVehicleType()).append(" - ");
            sb.append(slot.getVehicleNumber()).append("\n");
        }
        sb.append("\nParking Records:\n");
        for (ParkingRecord record : parkingRecords) {
            sb.append(record.toString()).append("\n");
        }
        parkingLogArea.setText(sb.toString());
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ParkingManagementSystem();
        });
    }
}

// ParkingSlot class
class ParkingSlot {
    private int slotNumber;
    private String vehicleType;
    private String vehicleNumber;
    private Date entryTime;
    private Date exitTime;

    public ParkingSlot(int slotNumber, String vehicleType) {
        this.slotNumber = slotNumber;
        this.vehicleType = vehicleType;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public Date getEntryTime() {
        return entryTime;
    }

    public Date getExitTime() {
        return exitTime;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public void setEntryTime(Date entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(Date exitTime) {
        this.exitTime = exitTime;
    }
}

// ParkingRecord class
class ParkingRecord {
    private String vehicleType;
    private String vehicleNumber;
    private int slotNumber;
    private Date entryTime;
    private Date exitTime;
    private double fee;

    public ParkingRecord(String vehicleType, String vehicleNumber, int slotNumber, Date entryTime) {
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
        this.slotNumber =

