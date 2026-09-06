package CSIA;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class RemoveStockFrame extends JFrame {
    private JComboBox<String> cmbDecreaseType;
	private JLabel lblInventoryIDLabel, lblInventoryID, lblItemNameLabel, lblAmount, lblDecreaseType;
    private JLabel lblUnitPriceLabel, lblUnitPrice;
    private JTextField txtAmount, txtItemName;
    private JButton btnSave;
    private JButton btnBack;
    private JButton btnOk;
    
    public RemoveStockFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Add Record");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        getContentPane().setLayout(null);

        // Label for InventoryID
        lblInventoryIDLabel = new JLabel("Inventory ID:");
        lblInventoryIDLabel.setBounds(10, 10, 100, 25);
        getContentPane().add(lblInventoryIDLabel);

        lblInventoryID = new JLabel("");
        lblInventoryID.setBounds(120, 10, 200, 25);
        getContentPane().add(lblInventoryID);
        
        // Label for Item Name
        lblItemNameLabel = new JLabel("Item Name:");
        lblItemNameLabel.setBounds(10, 45, 100, 25);
        getContentPane().add(lblItemNameLabel);

        // Label for Amount
        lblAmount = new JLabel("Amount:");
        lblAmount.setBounds(10, 115, 100, 25);
        getContentPane().add(lblAmount);

        // Amount TextField
        txtAmount = new JTextField();
        txtAmount.setBounds(120, 115, 160, 25);
        getContentPane().add(txtAmount);
        
        // Label for Decrease Type 
        lblDecreaseType = new JLabel("Decrease Type:");
        lblDecreaseType.setBounds(10, 150, 100, 25);
        getContentPane().add(lblDecreaseType);

        lblUnitPriceLabel = new JLabel("Unit Price (RM):");
        lblUnitPriceLabel.setBounds(10, 80, 100, 25); 
        getContentPane().add(lblUnitPriceLabel);

        lblUnitPrice = new JLabel("");
        lblUnitPrice.setBounds(120, 80, 200, 25);
        getContentPane().add(lblUnitPrice);
        
        // Item Name TextField
        txtItemName = new JTextField();
        txtItemName.setBounds(120, 45, 160, 25);
        getContentPane().add(txtItemName);
        
        btnOk = new JButton("OK");
        btnOk.setBounds(290, 45, 70, 25);
        getContentPane().add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchAndDisplayItemDetails(txtItemName.getText().trim());
            }
        });
        
        // Save Button
        btnSave = new JButton("Save");
        btnSave.setBounds(10, 185, 150, 30);
        getContentPane().add(btnSave);
        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveRecordAndUpdateStock();
            }
        });
        
        cmbDecreaseType = new JComboBox<>(new String[] {"Inventory Reduction", "Sales"});
        cmbDecreaseType.setBounds(120, 150, 160, 25);
        getContentPane().add(cmbDecreaseType);

        btnBack = new JButton("Back");
        btnBack.setBounds(10, 220, 150, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToInventoryLogPage();
            }
        });
        
    }

    private void goBackToInventoryLogPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            InventoryLogFrame InventoryLogPage = new InventoryLogFrame();
            InventoryLogPage.setVisible(true);
        });
    }
    

    private void fetchAndDisplayItemDetails(String itemName) {
        String url = "jdbc:mysql://localhost:3306/csiadraft";
        String user = "root";
        String password = "Mswchadnb05714@";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("SELECT InventoryID, ItemName, UnitPrice FROM item WHERE LOWER(ItemName) = LOWER(?)")) {

            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	lblInventoryID.setText(rs.getString("InventoryID")); 
                txtItemName.setText(rs.getString("ItemName"));
                lblUnitPrice.setText(String.valueOf(rs.getDouble("UnitPrice")));
            } else {
                JOptionPane.showMessageDialog(this, "Item not found", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving item details", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRecordAndUpdateStock() {
    	String selectedInventoryID = lblInventoryID.getText();
        String amountStr = txtAmount.getText();
        String decreaseType = (String) cmbDecreaseType.getSelectedItem();

        if (selectedInventoryID.isEmpty() || amountStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive value for stock reduction", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            amount = -amount; // Convert to negative for stock reduction
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
            String sqlLog = "INSERT INTO log (InventoryID, ItemName, Amount, SalesDateTime, DecreaseType) VALUES (?, ?, ?, NOW(), ?)";
            pstmtLog = conn.prepareStatement(sqlLog);
            pstmtLog.setString(1, selectedInventoryID);
            pstmtLog.setString(2, txtItemName.getText());
            pstmtLog.setInt(3, amount);
            pstmtLog.setString(4, decreaseType);
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
                return currentQuantity + amountChange; // Adjust quantity
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