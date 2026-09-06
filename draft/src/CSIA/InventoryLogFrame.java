package CSIA;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.Font;

public class InventoryLogFrame extends JFrame {
    private JTable table;
    private JButton btnBack;
    private JButton btnRemoveStock;
    private JButton btnAddStock;

    public InventoryLogFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Inventory Log");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        getContentPane().setLayout(null);

        btnAddStock = new JButton("Add Stock");
        btnAddStock.setBounds(844, 40, 130, 30); 
        getContentPane().add(btnAddStock);

        btnAddStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddStockFrame addStockFrame = new AddStockFrame();
                addStockFrame.setVisible(true);
                dispose(); // Close InventoryLogFrame
            }
        });
        
        btnRemoveStock = new JButton("Remove Stock");
        btnRemoveStock.setBounds(844, 100, 130, 30); 
        getContentPane().add(btnRemoveStock);

        btnRemoveStock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                RemoveStockFrame removeStockFrame = new RemoveStockFrame();
                removeStockFrame.setVisible(true);
                dispose(); // Close InventoryLogFrame
            }
        });
        
        btnBack = new JButton("Back");
        btnBack.setBounds(844, 510, 130, 30); 
        getContentPane().add(btnBack);

        btnBack.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToMainPage();
            }
        });

        // Label for Inventory Log
        JLabel lblInventoryLog = new JLabel("Inventory Log");
        lblInventoryLog.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblInventoryLog.setBounds(10, 0, 200, 35);
        getContentPane().add(lblInventoryLog);

        // Table setup
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 40, 820, 500);
        getContentPane().add(scrollPane);

        // Load data
        loadData();
    }

 // Method to load data from the database and display it in a JTable
    private void loadData() {
        // Create a table model and set column identifiers for the table
    	DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {"Inventory ID", "Item Name", "Unit Price (RM)", "Amount", "Sales DateTime", "Decrease Type"});

        String url = "jdbc:mysql://localhost:3306/csiadraft";
        String user = "root";
        String password = "Mswchadnb05714@";
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT l.InventoryID, l.ItemName, i.UnitPrice, l.Amount, l.SalesDateTime, l.DecreaseType " +
                                              "FROM log l INNER JOIN item i ON l.InventoryID = i.InventoryID")) {

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getObject("InventoryID"),
                    rs.getObject("ItemName"),
                    rs.getObject("UnitPrice"),
                    rs.getObject("Amount"),
                    rs.getObject("SalesDateTime"),
                    rs.getObject("DecreaseType")
                });
            }

            table.setModel(model);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void goBackToMainPage() {
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            MainPageFrame mainPage = new MainPageFrame(true);
            mainPage.setVisible(true);
        });
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                InventoryLogFrame frame = new InventoryLogFrame();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
