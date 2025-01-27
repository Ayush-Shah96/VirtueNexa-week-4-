import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class TimetableGenerator extends JFrame implements ActionListener {

    private JComboBox<String> teacherComboBox;
    private JComboBox<String> subjectComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> periodComboBox;
    private JButton addConstraintButton;
    private JButton generateTimetableButton;
    private JTextArea timetableArea;

    private List<Teacher> teachers;
    private List<Subject> subjects;
    private List<Constraint> constraints;

    public TimetableGenerator() {
        super("Timetable Generator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Initialize data (replace with actual data)
        teachers = new ArrayList<>();
        teachers.add(new Teacher("Teacher1"));
        teachers.add(new Teacher("Teacher2"));
        teachers.add(new Teacher("Teacher3"));

        subjects = new ArrayList<>();
        subjects.add(new Subject("Math"));
        subjects.add(new Subject("Science"));
        subjects.add(new Subject("English"));
        subjects.add(new Subject("History"));

        constraints = new ArrayList<>();

        // Create GUI components
        JPanel northPanel = new JPanel(new GridLayout(4, 2));
        northPanel.add(new JLabel("Teacher:"));
        teacherComboBox = new JComboBox<>(getTeacherNames());
        northPanel.add(teacherComboBox);
        northPanel.add(new JLabel("Subject:"));
        subjectComboBox = new JComboBox<>(getSubjectNames());
        northPanel.add(subjectComboBox);
        northPanel.add(new JLabel("Day:"));
        dayComboBox = new JComboBox<>(new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"});
        northPanel.add(dayComboBox);
        northPanel.add(new JLabel("Period:"));
        periodComboBox = new JComboBox<>(new String[]{"Period 1", "Period 2", "Period 3", "Period 4", "Period 5"});
        northPanel.add(periodComboBox);
        northPanel.add(addConstraintButton = new JButton("Add Constraint"));
        northPanel.add(generateTimetableButton = new JButton("Generate Timetable"));

        timetableArea = new JTextArea(20, 50);
        timetableArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(timetableArea);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add listeners
        addConstraintButton.addActionListener(this);
        generateTimetableButton.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addConstraintButton) {
            addConstraint();
        } else if (e.getSource() == generateTimetableButton) {
            generateTimetable();
        }
    }

    private void addConstraint() {
        String teacherName = (String) teacherComboBox.getSelectedItem();
        String subjectName = (String) subjectComboBox.getSelectedItem();
        String day = (String) dayComboBox.getSelectedItem();
        String period = (String) periodComboBox.getSelectedItem();

        Teacher teacher = getTeacherByName(teacherName);
        Subject subject = getSubjectByName(subjectName);

        if (teacher != null && subject != null) {
            constraints.add(new Constraint(teacher, subject, day, period));
        } else {
            showError("Invalid teacher or subject.");
        }
    }

    private void generateTimetable() {
        // 1. Create a timetable data structure (e.g., 2D array)
        String[][] timetable = new String[5][5]; // 5 days, 5 periods

        // 2. Assign subjects to slots based on constraints and teacher availability
        // (Implement your timetable generation algorithm here)

        // 3. Display the generated timetable in the JTextArea
        timetableArea.setText("");
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                timetableArea.append(days[i] + ", Period " + (j + 1) + ": " + timetable[i][j] + "\n");
            }
            timetableArea.append("\n");
        }
    }

    private String[] getTeacherNames() {
        String[] names = new String[teachers.size()];
        for (int i = 0; i < teachers.size(); i++) {
            names[i] = teachers.get(i).getName();
        }
        return names;
    }

    private String[] getSubjectNames() {
        String[] names = new String[subjects.size()];
        for (int i = 0; i < subjects.size(); i++) {
            names[i] = subjects.get(i).getName();
        }
        return names;
    }

    private Teacher getTeacherByName(String name) {
        for (Teacher teacher : teachers) {
            if (teacher.getName().equals(name)) {
                return teacher;
            }
        }
        return null;
    }

    private Subject getSubjectByName(String name) {
        for (Subject subject : subjects) {
            if (subject.getName().equals(name)) {
                return subject;
            }
        }
        return null;
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TimetableGenerator();
        });
    }
}

// Teacher class
class Teacher {
    private String name;

    public Teacher(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// Subject class
class Subject {
    private String name;

    public Subject(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

// Constraint class
class Constraint {
    private Teacher teacher;
    private Subject subject;
    private String day;
    private String period;

    public Constraint(Teacher teacher, Subject subject, String day, String period) {
        this.teacher = teacher;
        this.subject = subject;
        this.day = day;
        this.period = period;
    }

    // Getters for teacher, subject, day, and period
}

