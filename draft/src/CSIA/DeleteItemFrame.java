package CSIA;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Color;
import java.awt.EventQueue;

//The DeleteItemFrame class for creating a frame to delete items from inventory.
public class DeleteItemFrame extends JFrame {
 // Declaration of Swing components used in the frame.
    private JLabel lblInventoryID, lblItemName, lblDescription, lblUnitPrice, lblQuantityInStock, lblInventoryValue;
    private JLabel lblInventoryIDLabel, lblDescriptionLabel, lblUnitPriceLabel, lblQuantityInStockLabel, lblInventoryValueLabel;
    private JButton btnDelete;
    private JButton btnBack;
    private JTextField txtItemName;
    private JButton btnOk;

 // Constructor to initialize and set up the DeleteItemFrame UI.
    public DeleteItemFrame() {
        // Frame setup: background color, title, close operation, size, and layout.
    	getContentPane().setBackground(new Color(202, 252, 238));
        setTitle("Delete Item");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        getContentPane().setLayout(null);

        // Labels for displaying item details
        lblInventoryID = new JLabel("Inventory ID:");
        lblInventoryID.setBounds(10, 10, 580, 25);
        getContentPane().add(lblInventoryID);
        
        lblItemName = new JLabel("Item Name:");
        lblItemName.setBounds(10, 40, 580, 25);
        getContentPane().add(lblItemName);

        lblDescription = new JLabel("Description:");
        lblDescription.setBounds(10, 70, 580, 25);
        getContentPane().add(lblDescription);

        lblUnitPrice = new JLabel("Unit Price (RM):");
        lblUnitPrice.setBounds(10, 100, 580, 25);
        getContentPane().add(lblUnitPrice);

        lblQuantityInStock = new JLabel("Quantity in Stock:");
        lblQuantityInStock.setBounds(10, 130, 580, 25);
        getContentPane().add(lblQuantityInStock);

        lblInventoryValue = new JLabel("Inventory Value (RM):");
        lblInventoryValue.setBounds(10, 160, 580, 25);
        getContentPane().add(lblInventoryValue);
        
        lblInventoryIDLabel = new JLabel("");
        lblInventoryIDLabel.setBounds(145, 10, 200, 25); 
        getContentPane().add(lblInventoryIDLabel);
        
        lblDescriptionLabel = new JLabel("");
        lblDescriptionLabel.setBounds(145, 70, 200, 25); 
        getContentPane().add(lblDescriptionLabel);
        
        lblUnitPriceLabel = new JLabel("");
        lblUnitPriceLabel.setBounds(145, 100, 200, 25); 
        getContentPane().add(lblUnitPriceLabel);
        
        lblQuantityInStockLabel = new JLabel("");
        lblQuantityInStockLabel.setBounds(145, 130, 200, 25); 
        getContentPane().add(lblQuantityInStockLabel);
        
        lblInventoryValueLabel = new JLabel("");
        lblInventoryValueLabel.setBounds(145, 160, 200, 25); 
        getContentPane().add(lblInventoryValueLabel);
        
        txtItemName = new JTextField();
        txtItemName.setBounds(145, 40, 160, 25);
        getContentPane().add(txtItemName);
  
        btnOk = new JButton("OK");
        btnOk.setBounds(315, 40, 70, 25);
        getContentPane().add(btnOk);
        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fetchAndDisplayItemDetails(txtItemName.getText().trim());
            }
        });
        
        btnDelete = new JButton("Delete");
        btnDelete.setBounds(10, 200, 150, 30);
        getContentPane().add(btnDelete);

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmAndDeleteItem();
            }
        });
        
        btnBack = new JButton("Back");
        btnBack.setBounds(10, 240, 150, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainPage();
            }
        });
    }

    private void goBackToMainPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
    
 // Method to confirm the deletion of an item and execute the deletion if confirmed.
    private void confirmAndDeleteItem() {
        // Confirmation dialog to ensure the user wants to delete the specified item.
        // Execution of item deletion from the database if confirmed.
        String itemName = txtItemName.getText().trim();
        if (itemName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No item name entered", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the item: " + itemName + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            deleteSelectedItem(itemName);
         // Close this frame
            this.dispose();

            // Open MainPageFrame
            EventQueue.invokeLater(() -> {
                MainPageFrame mainPage = new MainPageFrame(true);
                mainPage.setVisible(true);
            });
        }
    }
    
    // Method to execute the deletion of the specified item from the database.
    private void deleteSelectedItem(String itemName) {
        // Connection to database and execution of SQL statement to delete the item.
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password);
        	     PreparedStatement pstmt = conn.prepareStatement("DELETE FROM item WHERE LOWER(ItemName) = LOWER(?)")) {

        	    // Sets the item name in the prepared statement.
        	    pstmt.setString(1, itemName);

        	    // Executes the delete operation and returns the number of affected rows.
        	    int affectedRows = pstmt.executeUpdate();

        	    // Checks if any rows (items) were deleted.
        	    if (affectedRows > 0) {
        	        JOptionPane.showMessageDialog(this, "Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        	    } else {
        	        JOptionPane.showMessageDialog(this, "Item not found or could not be deleted", "Error", JOptionPane.ERROR_MESSAGE);
        	    }
        	} catch (SQLException ex) {
        	    // Handles any SQL exceptions that occur during the operation.
        	    ex.printStackTrace();
        	    JOptionPane.showMessageDialog(this, "Error deleting item from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        	}
    }
    
    // Method to fetch and display the details of the item to be deleted.
    private void fetchAndDisplayItemDetails(String itemName) {
        // Establish connection to the database using provided credentials
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             // Prepare SQL statement to select item details where item name matches the input, case-insensitively
             PreparedStatement stmt = conn.prepareStatement("SELECT InventoryID, Description, UnitPrice, " +
                      "QuantityInStock, InventoryValue FROM item WHERE LOWER(ItemName) = LOWER(?)")) {
            stmt.setString(1, itemName); // Set the itemName parameter in the SQL query

            // Execute the query and store the result in ResultSet
            try (ResultSet rs = stmt.executeQuery()) {
                // Check if there is at least one result
                if (rs.next()) {
                    // If item is found, display its details by setting text of JLabels
                    lblInventoryIDLabel.setText(rs.getString("InventoryID"));
                    lblDescriptionLabel.setText(rs.getString("Description"));
                    lblUnitPriceLabel.setText(String.valueOf(rs.getDouble("UnitPrice")));
                    lblQuantityInStockLabel.setText(String.valueOf(rs.getInt("QuantityInStock")));
                    lblInventoryValueLabel.setText(String.valueOf(rs.getDouble("InventoryValue")));
                } else {
                    // If no item is found, show a dialog message indicating item not found
                    JOptionPane.showMessageDialog(this, "Item not found", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            // Catch and print any SQL exceptions that occur during operation
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
}
