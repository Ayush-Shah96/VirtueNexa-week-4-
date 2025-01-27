import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class HotelReservationSystem extends JFrame implements ActionListener {

    private JComboBox<String> roomTypeComboBox;
    private JDateChooser checkInDateChooser, checkOutDateChooser;
    private JButton checkAvailabilityButton, bookButton;
    private JTextArea reservationArea;

    private List<Room> rooms;

    public HotelReservationSystem() {
        super("Hotel Reservation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Initialize rooms (replace with actual data)
        rooms = new ArrayList<>();
        rooms.add(new Room("Single", 50.0));
        rooms.add(new Room("Double", 80.0));
        rooms.add(new Room("Suite", 150.0));

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(3, 2));
        northPanel.add(new JLabel("Room Type:"));
        roomTypeComboBox = new JComboBox<>(getRoomTypeNames());
        northPanel.add(roomTypeComboBox);
        northPanel.add(new JLabel("Check-in Date:"));
        checkInDateChooser = new JDateChooser();
        northPanel.add(checkInDateChooser);
        northPanel.add(new JLabel("Check-out Date:"));
        checkOutDateChooser = new JDateChooser();
        northPanel.add(checkOutDateChooser);
        northPanel.add(checkAvailabilityButton = new JButton("Check Availability"));
        northPanel.add(bookButton = new JButton("Book Now"));

        reservationArea = new JTextArea(10, 50);
        reservationArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reservationArea);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add listeners
        checkAvailabilityButton.addActionListener(this);
        bookButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkAvailabilityButton) {
            checkAvailability();
        } else if (e.getSource() == bookButton) {
            bookRoom();
        }
    }

    private void checkAvailability() {
        String roomType = (String) roomTypeComboBox.getSelectedItem();
        Date checkInDate = checkInDateChooser.getDate();
        Date checkOutDate = checkOutDateChooser.getDate();

        if (checkInDate == null || checkOutDate == null) {
            showError("Please select check-in and check-out dates.");
            return;
        }

        boolean isAvailable = checkRoomAvailability(roomType, checkInDate, checkOutDate);

        reservationArea.setText("");
        if (isAvailable) {
            reservationArea.append("Room type: " + roomType + "\n");
            reservationArea.append("Check-in: " + checkInDate + "\n");
            reservationArea.append("Check-out: " + checkOutDate + "\n");
            reservationArea.append("Room is available.");
        } else {
            reservationArea.append("Room type: " + roomType + " is not available for the selected dates.");
        }
    }

    private boolean checkRoomAvailability(String roomType, Date checkInDate, Date checkOutDate) {
        // Implement your room availability logic here
        // (e.g., check if the room is already booked for the given dates)
        return true; // Placeholder
    }

    private void bookRoom() {
        // Implement booking logic here
        // (e.g., update room availability, store reservation details)
        reservationArea.append("\nRoom booked successfully!\n");
    }

    private String[] getRoomTypeNames() {
        String[] names = new String[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            names[i] = rooms.get(i).getType();
        }
        return names;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HotelReservationSystem();
        });
    }
}

// Room class
class Room {
    private String type;
    private double price;

    public Room(String type, double price) {
        this.type = type;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public double getPrice() {
        return price;
    }
}

