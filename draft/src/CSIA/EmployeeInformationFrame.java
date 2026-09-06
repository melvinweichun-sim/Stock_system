package CSIA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeInformationFrame extends JFrame {
    private JTable table;
    private JPanel panelButtons;
    private JButton btnAdd, btnDelete, btnEdit, btnBack;
    

    public EmployeeInformationFrame() {
    	getContentPane().setBackground(new Color(202, 252, 238));
        setTitle("Employee Information");
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        table = new JTable();
        scrollPane.setViewportView(table);
        
     // Add button initialization in the constructor
        panelButtons = new JPanel();
        getContentPane().add(panelButtons, BorderLayout.SOUTH);

        btnAdd = new JButton("Add");
        panelButtons.add(btnAdd);
        btnAdd.addActionListener(e -> addEmployee());

        btnDelete = new JButton("Delete");
        panelButtons.add(btnDelete);
        btnDelete.addActionListener(e -> deleteEmployee());

        btnEdit = new JButton("Edit");
        panelButtons.add(btnEdit);
        btnEdit.addActionListener(e -> editEmployee());
        
        btnBack = new JButton("Back");
        btnBack.setBounds(200, 700, 110, 30); 
        panelButtons.add(btnBack);

        btnBack.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainPage();
            }
        });

        loadEmployeeData();
    }

    private void goBackToMainPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
    
    private void loadEmployeeData() {
        DefaultTableModel model = new DefaultTableModel(new String[]{"Employee ID", "Name", "Salary (RM)", "Phone Number (+60)"}, 0);
        table.setModel(model);

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {

            while (rs.next()) {
                int employeeId = rs.getInt("employee_id");
                String Name = rs.getString("name");
                double salary = rs.getDouble("salary");
                String phoneNumber = rs.getString("phone_number");

                model.addRow(new Object[]{employeeId, Name, salary, phoneNumber});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading employee data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                EmployeeInformationFrame frame = new EmployeeInformationFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void addEmployee() {
        JTextField nameField = new JTextField();
        JTextField salaryField = new JTextField();
        JTextField phoneField = new JTextField();
        Object[] message = {
            "Name:", nameField,
            "Salary (RM):", salaryField,
            "Phone Number (+60):", phoneField
        };

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        
        int option = JOptionPane.showConfirmDialog(this, message, "Add New Employee", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double salary = Double.parseDouble(salaryField.getText());
            String phoneNumber = phoneField.getText();

            String insertSql = "INSERT INTO employees (name, salary, phone_number) VALUES (?, ?, ?)";

            try (Connection conn = DriverManager.getConnection(url, user, password);
                 PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

                pstmt.setString(1, name);
                pstmt.setDouble(2, salary);
                pstmt.setString(3, phoneNumber);
                pstmt.executeUpdate();
                
                loadEmployeeData(); // Refresh table data
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteEmployee() {
        int selectedRow = table.getSelectedRow();

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        
        if (selectedRow >= 0) {
            int employeeId = (Integer) table.getValueAt(selectedRow, 0);
            int confirmation = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this employee?", "Delete Employee", JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                String deleteSql = "DELETE FROM employees WHERE employee_id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {

                    pstmt.setInt(1, employeeId);
                    pstmt.executeUpdate();
                    
                    loadEmployeeData(); // Refresh table data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error deleting employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void editEmployee() {
        int selectedRow = table.getSelectedRow();

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        
        if (selectedRow >= 0) {
            int employeeId = (Integer) table.getValueAt(selectedRow, 0);

            String currentName = (String) table.getValueAt(selectedRow, 1);
            String currentSalary = table.getValueAt(selectedRow, 2).toString();
            String currentPhone = (String) table.getValueAt(selectedRow, 3);

            JTextField nameField = new JTextField(currentName);
            JTextField salaryField = new JTextField(currentSalary);
            JTextField phoneField = new JTextField(currentPhone);
            Object[] message = {
                "Name:", nameField,
                "Salary (RM):", salaryField,
                "Phone Number (+60):", phoneField
            };

            int option = JOptionPane.showConfirmDialog(this, message, "Edit Employee", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameField.getText();
                double salary = Double.parseDouble(salaryField.getText());
                String phoneNumber = phoneField.getText();

                String updateSql = "UPDATE employees SET name = ?, salary = ?, phone_number = ? WHERE employee_id = ?";

                try (Connection conn = DriverManager.getConnection(url, user, password);
                     PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

                    pstmt.setString(1, name);
                    pstmt.setDouble(2, salary);
                    pstmt.setString(3, phoneNumber);
                    pstmt.setInt(4, employeeId);
                    pstmt.executeUpdate();
                    
                    loadEmployeeData(); // Refresh table data
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Error updating employee: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
}
