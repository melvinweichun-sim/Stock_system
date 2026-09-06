package CSIA;

import javax.swing.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Color;

public class EditItemFrame extends JFrame {
    // Components declaration
    private JTextField txtInventoryID, txtItemName, txtDescription, txtUnitPrice, 
    				   txtReorderLevel, txtReorderTimeInDays;
    private JLabel lblInventoryID, lblInventoryValueLabel, lblInventoryValue, 
    			   lblItemName, lblDescription, lblUnitPrice, lblReorderLevel, 
    			   lblReorderTimeInDays, lblQuantityInStock, lblQuantityInStockLabel;
    private JButton btnSave;
    private JButton btnBack;
    private JButton btnOk;

    public EditItemFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Edit Item");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        getContentPane().setLayout(null);
        
        txtInventoryID = new JTextField();
        txtInventoryID.setBounds(145, 10, 200, 25);
        getContentPane().add(txtInventoryID);
        
        // Item Name TextField
        txtItemName = new JTextField();
        txtItemName.setBounds(145, 40, 200, 25);
        getContentPane().add(txtItemName);
        
        // Description TextField
        txtDescription = new JTextField();
        txtDescription.setBounds(145, 70, 200, 25);
        getContentPane().add(txtDescription);

        // Unit Price TextField
        txtUnitPrice = new JTextField();
        txtUnitPrice.setBounds(145, 100, 200, 25);
        getContentPane().add(txtUnitPrice);
        txtUnitPrice.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                calculateAndDisplayInventoryValue();
            }
            public void removeUpdate(DocumentEvent e) {
                calculateAndDisplayInventoryValue();
            }
            public void insertUpdate(DocumentEvent e) {
                calculateAndDisplayInventoryValue();
            }
        });

        // Reorder Level TextField
        txtReorderLevel = new JTextField();
        txtReorderLevel.setBounds(145, 190, 200, 25);
        getContentPane().add(txtReorderLevel);

        // Reorder Time TextField
        txtReorderTimeInDays = new JTextField();
        txtReorderTimeInDays.setBounds(145, 220, 200, 25);
        getContentPane().add(txtReorderTimeInDays);

        lblInventoryID = new JLabel("Inventory ID:");
        lblInventoryID.setBounds(10, 10, 200, 25);
        getContentPane().add(lblInventoryID);
        
        lblItemName = new JLabel("Item Name:");
        lblItemName.setBounds(10, 40, 200, 25);
        getContentPane().add(lblItemName);
        
        lblInventoryValueLabel = new JLabel("Inventory Value (RM):");
        lblInventoryValueLabel.setBounds(10, 160, 200, 25);
        getContentPane().add(lblInventoryValueLabel);

        lblInventoryValue = new JLabel("");
        lblInventoryValue.setBounds(145, 160, 200, 25);
        getContentPane().add(lblInventoryValue);
        
        lblDescription = new JLabel("Description:");
        lblDescription.setBounds(10, 70, 200, 25);
        getContentPane().add(lblDescription);
        
        lblUnitPrice = new JLabel("Unit Price (RM):");
        lblUnitPrice.setBounds(10, 100, 200, 25);
        getContentPane().add(lblUnitPrice);
        
        lblReorderLevel = new JLabel("Reorder Level:");
        lblReorderLevel.setBounds(10, 190, 200, 25);
        getContentPane().add(lblReorderLevel);
        
        lblReorderTimeInDays = new JLabel("Reorder Time In Days:");
        lblReorderTimeInDays.setBounds(10, 220, 200, 25);
        getContentPane().add(lblReorderTimeInDays);
        
        lblQuantityInStockLabel = new JLabel("Quantity in Stock:");
        lblQuantityInStockLabel.setBounds(10, 130, 200, 25); 
        getContentPane().add(lblQuantityInStockLabel);

        lblQuantityInStock = new JLabel("");
        lblQuantityInStock.setBounds(145, 130, 200, 25);
        getContentPane().add(lblQuantityInStock);
        
        btnOk = new JButton("OK");
        btnOk.setBounds(350, 40, 70, 25);
        getContentPane().add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchAndDisplayItemDetails(txtItemName.getText().trim());
            }
        });
        
        // Save Button
        btnSave = new JButton("Save");
        btnSave.setBounds(10, 250, 110, 30);
        getContentPane().add(btnSave);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveItemDetails();
            }
        });

        btnBack = new JButton("Back");
        btnBack.setBounds(10, 290, 110, 30); 
        getContentPane().add(btnBack);
        
        btnBack.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainPage();
            }
        });
        
    }

 // Method to fetch and display the details of an item based on its name.
    private void fetchAndDisplayItemDetails(String itemName) {
        // Database credentials and URL
        String url = "jdbc:mysql://localhost:3306/csiadraft";
        String user = "root";
        String password = "Mswchadnb05714@";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("SELECT InventoryID, ItemName, Description, UnitPrice, QuantityInStock, ReorderLevel, ReorderTimeInDays FROM item WHERE LOWER(ItemName) = LOWER(?)")) {

            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	txtInventoryID.setText(rs.getString("InventoryID")); 
                txtItemName.setText(rs.getString("ItemName"));
                txtDescription.setText(rs.getString("Description"));
                txtUnitPrice.setText(String.valueOf(rs.getDouble("UnitPrice")));
                txtReorderLevel.setText(String.valueOf(rs.getInt("ReorderLevel")));
                txtReorderTimeInDays.setText(String.valueOf(rs.getInt("ReorderTimeInDays")));
                lblQuantityInStock.setText(String.valueOf(rs.getInt("QuantityInStock")));
            } else {
                JOptionPane.showMessageDialog(this, "Item not found", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving item details", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 // Method to calculate and display the inventory value based on unit price and quantity in stock.
    private void calculateAndDisplayInventoryValue() {
        // Retrieve the inventory ID from the UI component
        String selectedInventoryID = (String) txtInventoryID.getText();
        if (selectedInventoryID == null) {
            lblInventoryValue.setText("");
            return;
        }

        try {
            double unitPrice = Double.parseDouble(txtUnitPrice.getText());
            int quantityInStock = getQuantityInStock(selectedInventoryID);
            double inventoryValue = unitPrice * quantityInStock;
            lblInventoryValue.setText(String.format("%.2f", inventoryValue));
        } catch (NumberFormatException e) {
            lblInventoryValue.setText("Invalid unit price");
        }
    }

    private void saveItemDetails() {
        // Retrieve values from text fields
        String inventoryID = txtInventoryID.getText().toString();
        String itemName = txtItemName.getText();
        String description = txtDescription.getText();
        String unitPriceStr = txtUnitPrice.getText();
        String reorderLevelStr = txtReorderLevel.getText();
        String reorderTimeInDaysStr = txtReorderTimeInDays.getText();
        
        String url = "jdbc:mysql://localhost:3306/csiadraft"; 
        String user = "root"; 
        String password = "Mswchadnb05714@"; 

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("UPDATE item SET InventoryID = ?, ItemName = ?, Description = ?, UnitPrice = ?, ReorderLevel = ?, ReorderTimeInDays = ? WHERE ItemName = LOWER(?)")) {
            
            // Set parameters for the query
            pstmt.setString(1, inventoryID);
            pstmt.setString(2, itemName);
            pstmt.setString(3, description);
            pstmt.setDouble(4, Double.parseDouble(unitPriceStr));
            pstmt.setInt(5, Integer.parseInt(reorderLevelStr));
            pstmt.setInt(6, Integer.parseInt(reorderTimeInDaysStr));
            pstmt.setString(7, txtItemName.getText());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                JOptionPane.showMessageDialog(this, "Item details updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                // Close this frame
                this.dispose();

                // Open MainPageFrame
                EventQueue.invokeLater(() -> {
                    MainPageFrame mainPage = new MainPageFrame(true);
                    mainPage.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this, "No item found or update failed", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating item details", "Database Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
 // Method to retrieve the quantity in stock for a specific inventory ID from the database.
    private int getQuantityInStock(String inventoryID) {
        // Database credentials and URL
        String url = "jdbc:mysql://localhost:3306/csiadraft"; 
        String user = "root"; 
        String password = "Mswchadnb05714@"; 
        int quantityInStock = 0;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("SELECT QuantityInStock FROM item WHERE InventoryID = ?")) {

            pstmt.setString(1, inventoryID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                quantityInStock = rs.getInt("QuantityInStock");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving quantity in stock", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return quantityInStock;
    }

    private void goBackToMainPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
}
