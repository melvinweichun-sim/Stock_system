package CSIA;

import javax.swing.*;
import java.sql.*;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Color;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnForgotPassword;

    public LoginFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        // Frame Title
        setTitle("Login - XXXX Mini_Mart Stock Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(null);

        // Title Label
        JLabel lblTitle = new JLabel("S-Mart Stock Management System");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setBounds(0, 11, 438, 43);
        getContentPane().add(lblTitle);

        // Username Label and TextField
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(50, 70, 80, 16);
        getContentPane().add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(140, 65, 200, 26);
        getContentPane().add(txtUsername);
        txtUsername.setColumns(10);

        // Password Label and TextField
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(50, 110, 80, 16);
        getContentPane().add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(140, 105, 200, 26);
        getContentPane().add(txtPassword);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setBounds(140, 150, 117, 29);
        getContentPane().add(btnLogin);

        // Action Listener for Login Button
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                String role = checkCredentials(username, password);

                if (role != null) {
                    boolean isBoss = "boss".equals(role);
                    MainPageFrame mainPage = new MainPageFrame(isBoss);
                    mainPage.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Credentials");
                }
            }
        });
        
     // ForgotPassword Button
        btnForgotPassword = new JButton("Forgot Password?");
        btnForgotPassword.setBounds(50, 210, 140, 29);
        getContentPane().add(btnForgotPassword);
        
        btnForgotPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Prompt user for their username
                String username = JOptionPane.showInputDialog("Enter your username:");
                if (username != null) {
                    // Retrieve and display security question
                    String question = getSecurityQuestion(username);
                    if (question != null) {
                        String answer = JOptionPane.showInputDialog(question);
                        if (verifySecurityAnswer(username, answer)) {
                            // Allow user to reset password
                            String newPassword = JOptionPane.showInputDialog("Enter your new password:");
                            if (newPassword != null) {
                                resetPassword(username, newPassword);
                                JOptionPane.showMessageDialog(null, "Password reset successfully.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Incorrect answer.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "User not found.");
                    }
                }
            }
        });

    }

    // Method to check credentials from the database
 // Modified to return the user's role or null if credentials are invalid
    private String checkCredentials(String Username, String Password) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // Database connection
            String url = DBConfig.getUrl();
            String user = DBConfig.getUser();
            String password = DBConfig.getPassword();

            conn = DriverManager.getConnection(url, user, pass);

            // SQL query to check username and password
            String query = "SELECT Role FROM user WHERE Username=? AND Password=?";
            pst = conn.prepareStatement(query);
            pst.setString(1, Username);
            pst.setString(2, Password);

            rs = pst.executeQuery();

            // If user exists, return their role
            if (rs.next()) {
                return rs.getString("Role");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        // Return null if credentials are invalid or an exception occurred
        return null;
    }

 // Method to retrieve the security question associated with a specific username
    public String getSecurityQuestion(String username) {
        // SQL query to select the security question from the 'user_security' table
        String sql = "SELECT SecurityQuestion FROM user_security WHERE Username=?";
        try (
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(DBConfig.getUrl(), DBConfig.getUser(), DBConfig.getPassword());
            // Prepare the SQL statement with the provided username
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, username); // Set the username parameter in the query
            ResultSet rs = pst.executeQuery(); // Execute the query

            if (rs.next()) { // If a row is found
                return rs.getString("SecurityQuestion"); // Return the security question
            }
        } catch (SQLException ex) { // Catch any SQL exceptions
            ex.printStackTrace(); // Print the stack trace for debugging
        }
        return null; // Return null if no question is found or an exception occurs
    }

    // Method to verify if the provided answer matches the security answer stored in the database
    public boolean verifySecurityAnswer(String username, String providedAnswer) {
        // SQL query to select the security answer for a specific username
        String sql = "SELECT SecurityAnswer FROM user_security WHERE Username=?";

        try (
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(DBConfig.getUrl(), DBConfig.getUser(), DBConfig.getPassword());
            // Prepare the SQL statement
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, username); // Set the username parameter
            ResultSet rs = pst.executeQuery(); // Execute the query

            if (rs.next()) { // If a row is found
                String actualAnswer = rs.getString("SecurityAnswer"); // Get the actual answer from the database
                return providedAnswer.equals(actualAnswer); // Return true if the answers match, false otherwise
            }
        } catch (SQLException ex) { // Catch any SQL exceptions
            ex.printStackTrace(); // Print the stack trace for debugging
        }
        return false; // Return false if no answer is found or an exception occurs
    }

    // Method to reset the user's password in the database
    public void resetPassword(String username, String newPassword) {
        // SQL statement to update the user's password
        String sql = "UPDATE user SET Password=? WHERE Username=?";
        try (
            // Establish a connection to the database
            Connection conn = DriverManager.getConnection(DBConfig.getUrl(), DBConfig.getUser(), DBConfig.getPassword());
            // Prepare the SQL statement
            PreparedStatement pst = conn.prepareStatement(sql)
        ) {
            pst.setString(1, newPassword); // Set the new password parameter
            pst.setString(2, username); // Set the username parameter
            int rowsAffected = pst.executeUpdate(); // Execute the update

            if (rowsAffected > 0) { // If the update was successful
                System.out.println("Password reset successfully."); // Print success message
            } else {
                System.out.println("Password reset failed."); // Print failure message
            }
        } catch (SQLException ex) { // Catch any SQL exceptions
            ex.printStackTrace(); // Print the stack trace for debugging
        }
    }

    // Main method to run the LoginFrame
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame(); // Create a new instance of LoginFrame
                    frame.setVisible(true); // Make the frame visible
                } catch (Exception e) {
                    e.printStackTrace(); // Print the stack trace for any exceptions
                }
            }
        });
    }

    // Class to initialize the database and create necessary tables
    public class Connector {
        public static void main(String[] args) {
            // Database connection details
            String url = DBConfig.getUrl();
            String user = DBConfig.getUser();
            String password = DBConfig.getPassword();
            
            // Create an instance of DatabaseInitializer with the connection details
            DatabaseInitializer dbInitializer = new DatabaseInitializer(url, user, password);
            dbInitializer.createTables(); // Call the method to create tables
        }
    }
}
