package CSIA;

import javax.swing.*;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Color;

public class AddRecordFrame extends JFrame {
    private JComboBox<String> cmbInventoryID;
    private JLabel lblItemName, lblInventoryID, lblItemNameLabel, lblAmount;
    private JLabel lblUnitPriceLabel, lblUnitPrice;
    private JTextField txtAmount;
    private JButton btnSave;
    private JButton btnBack;

    public AddRecordFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Add Record");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        getContentPane().setLayout(null);

        // Label for InventoryID
        lblInventoryID = new JLabel("Inventory ID:");
        lblInventoryID.setBounds(10, 10, 100, 25);
        getContentPane().add(lblInventoryID);

        // InventoryID ComboBox
        cmbInventoryID = new JComboBox<>(); 
        cmbInventoryID.setBounds(120, 10, 160, 25);
        getContentPane().add(cmbInventoryID);
        cmbInventoryID.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateItemNameDisplay();
            }
        });

        // Label for Item Name
        lblItemNameLabel = new JLabel("Item Name:");
        lblItemNameLabel.setBounds(10, 45, 100, 25);
        getContentPane().add(lblItemNameLabel);

        // ItemName Label
        lblItemName = new JLabel("");
        lblItemName.setBounds(120, 45, 200, 25);
        getContentPane().add(lblItemName);

        // Label for Amount
        lblAmount = new JLabel("Amount:");
        lblAmount.setBounds(10, 115, 100, 25);
        getContentPane().add(lblAmount);

        // Amount TextField
        txtAmount = new JTextField();
        txtAmount.setBounds(120, 115, 160, 25);
        getContentPane().add(txtAmount);

        lblUnitPriceLabel = new JLabel("Unit Price (RM):");
        lblUnitPriceLabel.setBounds(10, 80, 100, 25); 
        getContentPane().add(lblUnitPriceLabel);

        lblUnitPrice = new JLabel("");
        lblUnitPrice.setBounds(120, 80, 200, 25);
        getContentPane().add(lblUnitPrice);
        
        // Save Button
        btnSave = new JButton("Save");
        btnSave.setBounds(10, 150, 150, 30);
        getContentPane().add(btnSave);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveRecordAndUpdateStock();
            }
        });

        btnBack = new JButton("Back");
        btnBack.setBounds(10, 185, 150, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToInventoryLogPage();
            }
        });
        
        
        // Load InventoryIDs
        loadInventoryIDs();
    }

    private void goBackToInventoryLogPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            InventoryLogFrame InventoryLogPage = new InventoryLogFrame();
            InventoryLogPage.setVisible(true);
        });
    }
    
    private void loadInventoryIDs() {
        String url = "jdbc:mysql://localhost:3306/csiadraft";
        String user = "root"; 
        String password = "Mswchadnb05714@"; 

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();
            String sql = "SELECT InventoryID FROM item";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String inventoryID = rs.getString("InventoryID");
                cmbInventoryID.addItem(inventoryID);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading Inventory IDs from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void updateItemNameDisplay() {
    	String selectedInventoryID = (String) cmbInventoryID.getSelectedItem();
        if (selectedInventoryID == null || selectedInventoryID.isEmpty()) {
            lblItemName.setText("");
            lblUnitPrice.setText(""); // Clear unit price label
            return;
        }

        String url = "jdbc:mysql://localhost:3306/csiadraft"; 
        String user = "root";
        String password = "Mswchadnb05714@"; 

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            String sql = "SELECT ItemName, UnitPrice FROM item WHERE InventoryID = ?"; 
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, selectedInventoryID);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                String itemName = rs.getString("ItemName");
                lblItemName.setText(itemName);
                double unitPrice = rs.getDouble("UnitPrice");
                lblUnitPrice.setText(String.format("%.2f", unitPrice)); // Set unit price text
            } else {
                lblItemName.setText("Item not found");
                lblUnitPrice.setText(""); // Clear unit price label
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving Item Name from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void saveRecordAndUpdateStock() {
        String selectedInventoryID = (String) cmbInventoryID.getSelectedItem();
        String amountStr = txtAmount.getText();

        if (selectedInventoryID == null || selectedInventoryID.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount format", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String url = "jdbc:mysql://localhost:3306/csiadraft"; 
        String user = "root"; 
        String password = "Mswchadnb05714@"; 

        Connection conn = null;
        PreparedStatement pstmtGetPrice = null;
        PreparedStatement pstmtLog = null;
        PreparedStatement pstmtUpdateStock = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url, user, password);
            conn.setAutoCommit(false); // Start transaction

            // Fetch Unit Price
            String sqlGetPrice = "SELECT UnitPrice FROM item WHERE InventoryID = ?";
            pstmtGetPrice = conn.prepareStatement(sqlGetPrice);
            pstmtGetPrice.setString(1, selectedInventoryID);
            rs = pstmtGetPrice.executeQuery();
            
            /// Check current stock level
            int currentStock = getCurrentStock(conn, selectedInventoryID);
            // If the amount is negative, it indicates stock decrease. Check if it's feasible.
            if (amount < 0 && Math.abs(amount) > currentStock) {
                JOptionPane.showMessageDialog(this, "The quantity in stock only has " + currentStock + " in stock", "Stock Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit the method without proceeding
            }

            // Calculate new QuantityInStock and InventoryValue
            int newQuantity = calculateNewQuantity(conn, selectedInventoryID, amount); // Implement this method
            if (newQuantity < 0) {
                JOptionPane.showMessageDialog(this, "Cannot reduce stock below zero", "Stock Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
               
            double unitPrice = 0;
            if (rs.next()) {
                unitPrice = rs.getDouble("UnitPrice");
            } else {
                throw new SQLException("Item not found");
            }

            // Calculate new QuantityInStock and InventoryValue
            double newInventoryValue = newQuantity * unitPrice;

            // Insert into log table
            String sqlLog = "INSERT INTO log (InventoryID, ItemName, Amount, SalesDateTime) VALUES (?, ?, ?, NOW())";
            pstmtLog = conn.prepareStatement(sqlLog);
            pstmtLog.setString(1, selectedInventoryID);
            pstmtLog.setString(2, lblItemName.getText()); // Assuming lblItemName contains the ItemName
            pstmtLog.setInt(3, amount);
            pstmtLog.executeUpdate();

            // Update QuantityInStock and InventoryValue in items table
            String sqlUpdateStock = "UPDATE item SET QuantityInStock = ?, InventoryValue = ? WHERE InventoryID = ?";
            pstmtUpdateStock = conn.prepareStatement(sqlUpdateStock);
            pstmtUpdateStock.setInt(1, newQuantity);
            pstmtUpdateStock.setDouble(2, newInventoryValue);
            pstmtUpdateStock.setString(3, selectedInventoryID);
            pstmtUpdateStock.executeUpdate();

            try {
                amount = Integer.parseInt(amountStr);
             
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount format", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            conn.commit(); // Commit transaction

            JOptionPane.showMessageDialog(this, "Record saved successfully");

            // Close this frame and open InventoryLogFrame
            this.dispose();
            EventQueue.invokeLater(() -> {
                InventoryLogFrame inventoryLogFrame = new InventoryLogFrame();
                inventoryLogFrame.setVisible(true);
            });

        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in case of an error
                } catch (SQLException exRollback) {
                    exRollback.printStackTrace();
                }
            }
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving record to database", "Database Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close all resources
            try {
                if (rs != null) rs.close();
                if (pstmtGetPrice != null) pstmtGetPrice.close();
                if (pstmtLog != null) pstmtLog.close();
                if (pstmtUpdateStock != null) pstmtUpdateStock.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

 // Method to calculate the new quantity in stock for a specific inventory item after a change
    private int calculateNewQuantity(Connection conn, String inventoryID, int amountChange) throws SQLException {
        // SQL query to get the current quantity in stock for the specified inventory ID
        String sql = "SELECT QuantityInStock FROM item WHERE InventoryID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventoryID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int currentQuantity = rs.getInt("QuantityInStock");
                return currentQuantity + amountChange; 
            } else {
                throw new SQLException("Item not found");
            }
        }
    }
    
 // Method to retrieve the current stock level for a specific inventory item
    private int getCurrentStock(Connection conn, String inventoryID) throws SQLException {
        // SQL query to get the current quantity in stock for the specified inventory ID
        String sql = "SELECT QuantityInStock FROM item WHERE InventoryID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, inventoryID);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("QuantityInStock");
            } else {
                throw new SQLException("Item not found");
            }
        }
    }
}
