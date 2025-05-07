import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("")
public class StudentManagementApp implements ActionListener {
    private JButton addButton, deleteButton, viewButton, updateButton;
    private JTextField idField, nameField, ageField, gradeField;
    private static final String URL = "jdbc:postgresql://localhost:5432/test";
    private static final String USER = "polanr";
    private static final String PASSWORD = "Mylove$76";
    private Connection conn;
    private JFrame frame;

    // Color and font constants for consistent styling
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);  // Steel Blue
    private static final Color SECONDARY_COLOR = new Color(220, 220, 220);  // Light Gray
    private static final Color TEXT_COLOR = new Color(40, 40, 40);  // Dark Gray
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font TEXT_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 12);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentManagementApp().start());
    }

    public void start() {
        try {
            // Establish the database connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
            
            // Initialize the GUI
            initializeGUI();

        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Failed to connect to the database: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initializeGUI() {
        // Create the main frame
        frame = new JFrame("Student Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 400);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(SECONDARY_COLOR);

        // Add window listener to close the database connection on exit
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeConnection();
            }
        });

        // Create the input fields panel
        JPanel inputPanel = createInputPanel();
        JPanel buttonPanel = createButtonPanel();

        // Add panels to the frame
        frame.add(inputPanel, BorderLayout.CENTER);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(SECONDARY_COLOR);

        // Initialize text fields and labels
        idField = new JTextField();
        nameField = new JTextField();
        ageField = new JTextField();
        gradeField = new JTextField();

        // Style text fields
        styleTextField(idField);
        styleTextField(nameField);
        styleTextField(ageField);
        styleTextField(gradeField);

        // Add fields to the panel
        panel.add(createFieldPanel(new JLabel("Student ID:"), idField));
        panel.add(createFieldPanel(new JLabel("Student Name:"), nameField));
        panel.add(createFieldPanel(new JLabel("Student Age:"), ageField));
        panel.add(createFieldPanel(new JLabel("Student Grade:"), gradeField));

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        buttonPanel.setBackground(new Color(200, 200, 200));  // Slightly darker gray

        // Initialize buttons
        addButton = new JButton("Add Student");
        viewButton = new JButton("View Students");
        deleteButton = new JButton("Delete Student");
        updateButton = new JButton("Update Student");

        // Style buttons
        styleButton(addButton);
        styleButton(viewButton);
        styleButton(deleteButton);
        styleButton(updateButton);

        // Add action listeners
        addButton.addActionListener(this);
        viewButton.addActionListener(this);
        deleteButton.addActionListener(this);
        updateButton.addActionListener(this);

        // Add buttons to the panel
        buttonPanel.add(addButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);

        return buttonPanel;
    }

    private static JPanel createFieldPanel(JLabel label, JTextField textField) {
        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        fieldPanel.setBackground(new Color(240, 240, 250));  // Light lavender background
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_COLOR);
        fieldPanel.add(label);
        fieldPanel.add(textField);
        return fieldPanel;
    }

    private void styleTextField(JTextField textField) {
        textField.setPreferredSize(new Dimension(150, 30));
        textField.setFont(TEXT_FONT);
        textField.setForeground(TEXT_COLOR);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_COLOR, 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        textField.setBackground(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(PRIMARY_COLOR);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            addStudent();
        } else if (e.getSource() == viewButton) {
            viewStudents();
        } else if (e.getSource() == updateButton) {
            updateStudent();
        } else if (e.getSource() == deleteButton) {
            deleteStudent();
        }
    }

    private void addStudent() {
        String sql = "INSERT INTO students (id, name, age, grade) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idField.getText().trim());
            stmt.setString(2, nameField.getText().trim());
            stmt.setInt(3, Integer.parseInt(ageField.getText().trim()));
            stmt.setString(4, gradeField.getText().trim());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Student added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(frame, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Invalid input for age. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error adding student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        String sql = "DELETE FROM students WHERE id = ?";
        try {
            String id = JOptionPane.showInputDialog(frame, "Enter the Student ID to delete:");
            if (id == null || id.trim().isEmpty()) return;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id.trim());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Student successfully deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error deleting student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewStudents() {
        String sql = "SELECT * FROM students";
        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            StringBuilder students = new StringBuilder("ID\tName\tAge\tGrade\n");
            boolean hasStudents = false;
            while (rs.next()) {
                hasStudents = true;
                students.append(rs.getString("id")).append("\t")
                        .append(rs.getString("name")).append("\t")
                        .append(rs.getInt("age")).append("\t")
                        .append(rs.getString("grade")).append("\n");
            }
            if (!hasStudents) {
                JOptionPane.showMessageDialog(frame, "No students found.", "Students", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JTextArea textArea = new JTextArea(students.toString());
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                textArea.setEditable(false);
                JOptionPane.showMessageDialog(frame, new JScrollPane(textArea), "Students", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error retrieving students: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        String sql = "UPDATE students SET name = ?, age = ?, grade = ? WHERE id = ?";
        try {
            String id = JOptionPane.showInputDialog(frame, "Enter the Student ID to update:");
            if (id == null || id.trim().isEmpty()) return;

            String name = JOptionPane.showInputDialog(frame, "Enter new name:");
            String ageStr = JOptionPane.showInputDialog(frame, "Enter new age:");
            String grade = JOptionPane.showInputDialog(frame, "Enter new grade:");

            if (name == null || ageStr == null || grade == null || name.trim().isEmpty() || ageStr.trim().isEmpty() || grade.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "All fields are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int age = Integer.parseInt(ageStr.trim());

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name.trim());
                stmt.setInt(2, age);
                stmt.setString(3, grade.trim());
                stmt.setString(4, id.trim());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Student updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Student not found!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(frame, "Invalid input for age. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Error updating student: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        gradeField.setText("");
    }

    private void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}