package CSIA;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.Color;

public class AddItemFrame extends JFrame {
    private JTextField txtInventoryID, txtItemName, txtDescription, txtUnitPrice, txtQuantityInStock, txtInventoryValue, txtReorderLevel, txtReorderTimeInDays;
    private JButton btnSubmit;
    private JButton btnBack;
    
    public AddItemFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Add New Item");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 480, 360);
        getContentPane().setLayout(null);

     // Back Button
        btnBack = new JButton("Back");
        btnBack.setBounds(365, 10, 80, 30); // Position this button beside the InventoryID field
        getContentPane().add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isAnyTextFieldFilled()) {
                    showConfirmationDialog();
                } else {
                    goBackToMainPage();
                }
            }
        });
        
        JLabel lblInventoryID = new JLabel("Inventory ID:");
        lblInventoryID.setBounds(10, 10, 100, 25);
        getContentPane().add(lblInventoryID);

        txtInventoryID = new JTextField();
        txtInventoryID.setBounds(145, 10, 200, 25);
        getContentPane().add(txtInventoryID);

        JLabel lblItemName = new JLabel("Item Name:");
        lblItemName.setBounds(10, 40, 100, 25);
        getContentPane().add(lblItemName);

        txtItemName = new JTextField();
        txtItemName.setBounds(145, 40, 200, 25);
        getContentPane().add(txtItemName);
        
        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setBounds(10, 70, 100, 25);
        getContentPane().add(lblDescription);

        txtDescription = new JTextField();
        txtDescription.setBounds(145, 70, 200, 55);
        getContentPane().add(txtDescription);
        
        JLabel lblUnitPrice = new JLabel("Unit Price (RM):");
        lblUnitPrice.setBounds(10, 130, 100, 25);
        getContentPane().add(lblUnitPrice);

        txtUnitPrice = new JTextField();
        txtUnitPrice.setBounds(145, 130, 200, 25);
        getContentPane().add(txtUnitPrice);
        
        JLabel lblQuantityInStock = new JLabel("Quantity in Stock:");
        lblQuantityInStock.setBounds(10, 160, 136, 25);
        getContentPane().add(lblQuantityInStock);

        txtQuantityInStock = new JTextField();
        txtQuantityInStock.setBounds(145, 160, 200, 25);
        getContentPane().add(txtQuantityInStock);
        
        JLabel lblInventoryValue = new JLabel("Inventory Value (RM):");
        lblInventoryValue.setBounds(10, 190, 165, 25);
        getContentPane().add(lblInventoryValue);

        txtInventoryValue = new JTextField();
        txtInventoryValue.setBounds(145, 190, 200, 25);
        txtInventoryValue.setEditable(false); // Make this field read-only
        getContentPane().add(txtInventoryValue);
        
        JLabel lblReorderLevel = new JLabel("Reorder Level:");
        lblReorderLevel.setBounds(10, 220, 177, 25);
        getContentPane().add(lblReorderLevel);

        txtReorderLevel = new JTextField();
        txtReorderLevel.setBounds(145, 220, 200, 25);
        getContentPane().add(txtReorderLevel);
        
        JLabel lblReorderTimeInDays = new JLabel("Reorder Time in Days:");
        lblReorderTimeInDays.setBounds(10, 250, 136, 25);
        getContentPane().add(lblReorderTimeInDays);

        txtReorderTimeInDays = new JTextField();
        txtReorderTimeInDays.setBounds(145, 250, 200, 25);
        getContentPane().add(txtReorderTimeInDays);

        // Submit Button
        btnSubmit = new JButton("Add Item");
        btnSubmit.setBounds(145, 285, 100, 30);
        getContentPane().add(btnSubmit);

        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItemToDatabase();
            }
        });
        
        DocumentListener documentListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateInventoryValue(); }
            public void removeUpdate(DocumentEvent e) { updateInventoryValue(); }
            public void changedUpdate(DocumentEvent e) { updateInventoryValue(); }
        };

        txtUnitPrice.getDocument().addDocumentListener(documentListener);
        txtQuantityInStock.getDocument().addDocumentListener(documentListener);
    }

    private boolean isAnyTextFieldFilled() {
        return !txtInventoryID.getText().trim().isEmpty() ||
               !txtItemName.getText().trim().isEmpty() ||
               !txtDescription.getText().trim().isEmpty() ||
               !txtUnitPrice.getText().trim().isEmpty() ||
               !txtQuantityInStock.getText().trim().isEmpty() ||
               !txtReorderLevel.getText().trim().isEmpty() ||
               !txtReorderTimeInDays.getText().trim().isEmpty(); // Check other text fields similarly
    }
    
    private void showConfirmationDialog() {
        int result = JOptionPane.showConfirmDialog(this,
                "The information will not be saved. Are you sure you want to return to the Main Page?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            goBackToMainPage();
        }
    }
    
    private void goBackToMainPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
    
    private void updateInventoryValue() {
        try {
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());
            int quantity = Integer.parseInt(txtQuantityInStock.getText());
            double inventoryValue = unitPrice * quantity;
            txtInventoryValue.setText(String.valueOf(inventoryValue));
        } catch (NumberFormatException e) {
            // This catch block handles cases where one of the fields is empty or non-numeric
            txtInventoryValue.setText("");
        }
    }
    
    private void addItemToDatabase() {
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        String inventoryID = txtInventoryID.getText().trim();
        
     // Establishes a connection to the database to ensure the uniqueness of the InventoryID before insertion.
        try (
            Connection conn = DriverManager.getConnection(url, user, password);
            PreparedStatement checkExistStmt = conn.prepareStatement("SELECT COUNT(*) FROM item WHERE InventoryID = ?");
            // Prepares a statement for inserting a new item into the 'item' table if the InventoryID is unique.
            PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO item (InventoryID, ItemName, "
            		+ "Description, UnitPrice, QuantityInStock, InventoryValue, ReorderLevel, "
            		+ "ReorderTimeInDays) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
        ) {
            // Sets the InventoryID to the checkExistStmt to query the database.
            checkExistStmt.setString(1, inventoryID);
            ResultSet rs = checkExistStmt.executeQuery();

            // Checks the result set if the InventoryID already exists.
            if (rs.next() && rs.getInt(1) > 0) {
                // If InventoryID exists, displays an error message and stops further execution.
                JOptionPane.showMessageDialog(this, "This Inventory ID already exists, use a different one.", 
                		"Error", JOptionPane.ERROR_MESSAGE);
                return;
            } 

               // Insert the new item
               insertStmt.setString(1, inventoryID);
               insertStmt.setString(2, txtItemName.getText());
               insertStmt.setString(3, txtDescription.getText());
               insertStmt.setDouble(4, Double.parseDouble(txtUnitPrice.getText()));
               insertStmt.setInt(5, Integer.parseInt(txtQuantityInStock.getText()));
               insertStmt.setDouble(6, Double.parseDouble(txtInventoryValue.getText()));
               insertStmt.setInt(7, Integer.parseInt(txtReorderLevel.getText()));
               insertStmt.setInt(8, Integer.parseInt(txtReorderTimeInDays.getText()));

               int affectedRows = insertStmt.executeUpdate();
               if (affectedRows > 0) {
                   JOptionPane.showMessageDialog(this, "Item added successfully!");
                   // Close this frame
                   this.dispose();
                   // Open MainPageFrame
                   EventQueue.invokeLater(() -> {
                       MainPageFrame mainPage = new MainPageFrame(true);
                       mainPage.setVisible(true);
                   });
               } else {
                   JOptionPane.showMessageDialog(this, "Item could not be added.", "Error", JOptionPane.ERROR_MESSAGE);
               }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error connecting to database.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
