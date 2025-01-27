import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.Timer;
import java.util.TimerTask;

public class OnlineExam extends JFrame implements ActionListener {

    private JLabel questionLabel;
    private JRadioButton options;
    private ButtonGroup optionGroup;
    private JLabel timerLabel;
    private JButton nextButton;
    private JButton submitButton;
    private JTextArea resultArea;

    private List<Question> questions;
    private int currentQuestionIndex;
    private int score;
    private Timer timer;
    private int timeRemaining;

    public OnlineExam(List<Question> questions, int examDuration) {
        super("Online Exam");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        this.questions = questions;
        this.timeRemaining = examDuration * 60; // Convert minutes to seconds

        // Create GUI components
        JPanel northPanel = new JPanel(new FlowLayout());
        questionLabel = new JLabel("Question: ");
        northPanel.add(questionLabel);

        JPanel centerPanel = new JPanel(new GridLayout(4, 1));
        options = new JRadioButton[4];
        optionGroup = new ButtonGroup();
        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            optionGroup.add(options[i]);
            centerPanel.add(options[i]);
        }

        JPanel southPanel = new JPanel(new FlowLayout());
        timerLabel = new JLabel("Time Remaining: " + timeRemaining / 60 + ":" + String.format("%02d", timeRemaining % 60));
        nextButton = new JButton("Next");
        submitButton = new JButton("Submit");
        southPanel.add(timerLabel);
        southPanel.add(nextButton);
        southPanel.add(submitButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
        add(scrollPane, BorderLayout.EAST); // Display results on the right side

        // Add listeners
        nextButton.addActionListener(this);
        submitButton.addActionListener(this);

        // Start the timer
        startTimer();

        // Load the first question
        loadQuestion();

        setVisible(true);
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeRemaining--;
                timerLabel.setText("Time Remaining: " + timeRemaining / 60 + ":" + String.format("%02d", timeRemaining % 60));
                if (timeRemaining <= 0) {
                    timer.cancel();
                    timer.purge();
                    JOptionPane.showMessageDialog(null, "Time's up!", "Exam Finished", JOptionPane.INFORMATION_MESSAGE);
                    showResults();
                }
            }
        }, 1000, 1000);
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question currentQuestion = questions.get(currentQuestionIndex);
            questionLabel.setText((currentQuestionIndex + 1) + ". " + currentQuestion.getQuestion());
            for (int i = 0; i < 4; i++) {
                options[i].setText(currentQuestion.getOptions()[i]);
                options[i].setSelected(false);
            }
        } else {
            // All questions answered
            showResults();
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            loadQuestion();
        }
    }

    private void submitExam() {
        timer.cancel();
        timer.purge();
        showResults();
    }

    private void showResults() {
        resultArea.setText("Exam Results:\n\n");
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            String selectedOption = "";
            for (int j = 0; j < 4; j++) {
                if (options[j].isSelected()) {
                    selectedOption = options[j].getText();
                    break;
                }
            }
            resultArea.append("Question " + (i + 1) + ": " + question.getQuestion() + "\n");
            resultArea.append("Your Answer: " + selectedOption + "\n");
            resultArea.append("Correct Answer: " + question.getCorrectAnswer() + "\n\n");
            if (selectedOption.equals(question.getCorrectAnswer())) {
                score++;
            }
        }
        resultArea.append("Your Score: " + score + "/" + questions.size() + "\n");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            nextQuestion();
        } else if (e.getSource() == submitButton) {
            submitExam();
        }
    }

    public static void main(String args) {
        // Create a list of sample questions (replace with your actual data)
        List<Question> questions = new ArrayList<>();
        questions.add(new Question("What is the capital of France?", new String{"London", "Berlin", "Paris", "Madrid"}, "Paris"));
        questions.add(new Question("Who painted the Mona Lisa?", new String{"Michelangelo", "Leonardo da Vinci", "Raphael", "Donatello"}, "Leonardo da Vinci"));
        questions.add(new Question("What is the largest planet in our solar system?", new String{"Earth", "Mars", "Jupiter", "Saturn"}, "Jupiter"));

        SwingUtilities.invokeLater(() -> {
            new OnlineExam(questions, 30); // 30 minutes exam duration
        });
    }
}

// Question class
class Question {
    private String question;
    private String options;
    private String correctAnswer;

    public Question(String question, String options, String correctAnswer) {
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptions() {
        return options;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }
}

