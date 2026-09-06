package CSIA;

import javax.swing.*;

import javax.swing.table.DefaultTableModel;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Color;

public class SalesTrackFrame extends JFrame {
    private JComboBox<String> cmbDate;
    private JTable table;
    private JLabel lblGrandTotal;
    private JButton btnBack;

    public SalesTrackFrame() {
    	getContentPane().setBackground(new Color(202, 252, 238));
        setTitle("Sales Track");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        getContentPane().setLayout(null);

        // Date ComboBox
        cmbDate = new JComboBox<>();
        cmbDate.setBounds(10, 10, 160, 25);
        getContentPane().add(cmbDate);
        cmbDate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateSalesDisplay();
            }
        });
        
        btnBack = new JButton("Back");
        btnBack.setBounds(660, 460, 110, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainPage();
            }
        });
        
        // Sales Table
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 45, 760, 400);
        getContentPane().add(scrollPane);

        // Grand Total Label
        lblGrandTotal = new JLabel("Grand Total: ");
        lblGrandTotal.setBounds(10, 460, 200, 25);
        getContentPane().add(lblGrandTotal);

        // Load Dates
        loadDates();
    }

    private void loadDates() {
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT DATE(SalesDateTime) AS SalesDate FROM log ORDER BY SalesDate")) {

            while (rs.next()) {
                cmbDate.addItem(rs.getString("SalesDate"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dates from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goBackToMainPage() {
        this.dispose(); 

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
    
    private void updateSalesDisplay() {
        String selectedDate = (String) cmbDate.getSelectedItem();
        if (selectedDate == null || selectedDate.isEmpty()) {
            return;
        }

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        DefaultTableModel model = new DefaultTableModel(new String[]{"Inventory ID", "Item Name", "Unit Price (RM)", "Amount", "Total Price (RM)"}, 0);

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT l.InventoryID, i.ItemName, i.UnitPrice, SUM(l.Amount) AS TotalAmount " +
                 "FROM log l JOIN item i ON l.InventoryID = i.InventoryID " +
                 "WHERE DATE(l.SalesDateTime) = ? AND l.Amount < 0 AND l.DecreaseType = 'sales' " + // Filter for sales decrease type
                 "GROUP BY l.InventoryID, i.ItemName, i.UnitPrice")) {

            pstmt.setString(1, selectedDate);
            ResultSet rs = pstmt.executeQuery();
            float grandTotal = 0;

            while (rs.next()) {
                String inventoryID = rs.getString("InventoryID");
                String itemName = rs.getString("ItemName");
                double unitPrice = rs.getDouble("UnitPrice");
                int amount = rs.getInt("TotalAmount");
                double totalPrice = unitPrice * -amount; // Negate to get positive value

                model.addRow(new Object[]{inventoryID, itemName, unitPrice, -amount, totalPrice});
                grandTotal += totalPrice;
            }

            table.setModel(model);
            lblGrandTotal.setText("Grand Total: RM" + grandTotal);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving sales data from database", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
