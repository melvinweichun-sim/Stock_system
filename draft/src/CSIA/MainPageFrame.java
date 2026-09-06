package CSIA;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.awt.*;

public class MainPageFrame extends JFrame {

    private JTable table; 
    private JButton btnInventoryLog;
    private JButton btnDeleteItem;
    private JButton btnLogout;
    private JButton btnSalesTrack;
    private JButton btnInventoryLevel;
    private JButton btnEditItem;
    private JComboBox<String> cmbSortOrder;
    private JButton btnEmployeeInfo;
    
    public MainPageFrame(boolean isBoss) {
        getContentPane().setBackground(new Color(202, 253, 238));
        // Frame Title
        setTitle("Main Page - XXXX Mini_Mart Stock Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 200, 1160, 600);
        getContentPane().setLayout(null);

        // Label for Inventory List
        JLabel lblInventoryList = new JLabel("S-Mart Inventory List");
        lblInventoryList.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblInventoryList.setBounds(10, 0, 590, 44);
        getContentPane().add(lblInventoryList);        

        table = new JTable(); // Initialize the table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 40, 960, 500);
        getContentPane().add(scrollPane);
        
        btnInventoryLog = new JButton("Inventory Log");
        btnInventoryLog.setBounds(992, 220, 130, 30);
        getContentPane().add(btnInventoryLog);

        btnInventoryLog.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InventoryLogFrame inventoryLogFrame = new InventoryLogFrame();
                inventoryLogFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        JButton btnAddNewItem = new JButton("Add New Item");
        btnAddNewItem.setBounds(992, 40, 130, 30);
        getContentPane().add(btnAddNewItem);

        btnAddNewItem.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddItemFrame addItemFrame = new AddItemFrame();
                addItemFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        btnDeleteItem = new JButton("Delete Item");
        btnDeleteItem.setBounds(992, 100, 130, 30); 
        getContentPane().add(btnDeleteItem);

        btnDeleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DeleteItemFrame deleteItemFrame = new DeleteItemFrame();
                deleteItemFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        btnEditItem = new JButton("Edit Item");
        btnEditItem.setBounds(992, 160, 130, 30); 
        getContentPane().add(btnEditItem);

        btnEditItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                EditItemFrame editItemFrame = new EditItemFrame();
                editItemFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        btnLogout = new JButton("Logout");
        btnLogout.setBounds(992, 510, 130, 30); 
        getContentPane().add(btnLogout);

        btnLogout.addActionListener((ActionListener) new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                goBackToLoginPage();
            }
        });
        
        btnSalesTrack = new JButton("Sales Track");
        btnSalesTrack.setBounds(992, 340, 130, 30); 
        getContentPane().add(btnSalesTrack);

        btnSalesTrack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SalesTrackFrame salesTrackFrame = new SalesTrackFrame();
                salesTrackFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        btnInventoryLevel = new JButton("Inventory Level");
        btnInventoryLevel.setBounds(992, 280, 130, 30);
        getContentPane().add(btnInventoryLevel);

        btnInventoryLevel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                InventoryLevelFrame inventoryLevelFrame = new InventoryLevelFrame();
                inventoryLevelFrame.setVisible(true);
                dispose(); // Close MainPageFrame
            }
        });
        
        cmbSortOrder = new JComboBox<>(new String[] {
                "Inventory ID: A-Z", "Inventory ID: Z-A", 
                "Item Name: A-Z", "Item Name: Z-A", 
                "Unit Price: Lowest-Highest","Unit Price: Highest-Lowest",
                "Quantity In Stock: Lowest-Highest", "Quantity In Stock: Highest-Lowest",
                "Inventory Value: Lowest-Highest", "Inventory Value: Highest-Lowest"
            });
            cmbSortOrder.setBounds(735, 10, 235, 25); 
            getContentPane().add(cmbSortOrder);

            cmbSortOrder.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateInventoryList();
                }
            });
            
         if (isBoss) {
            // Add "Employee Info" button
            btnEmployeeInfo = new JButton("Employee Info");
            btnEmployeeInfo.setBounds(992, 400, 130, 30);
            getContentPane().add(btnEmployeeInfo);
            btnEmployeeInfo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    EmployeeInformationFrame employeeInfoFrame = new EmployeeInformationFrame();
                    employeeInfoFrame.setVisible(true);
                    dispose(); // Close MainPageFrame
                }
            });
        }
        
        // Load data
        loadData();
    }
    
 // Go back to the login page
    private void goBackToLoginPage() {
        // Dispose the current frame and open the login frame
        this.dispose(); // Close the current frame

        EventQueue.invokeLater(() -> {
            LoginFrame firstGUI = new LoginFrame();
            firstGUI.setVisible(true);
        });
    }
    
    // Load data into the table model from the database
    private void loadData() {
        // Fetch inventory data from the database and populate the table model
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[] {"Inventory ID", "Item Name", 
        		"Description", "Unit Price (RM)", "Quantity in Stock", 
        		"Inventory Value (RM)", "Reorder Level", "Reorder Time in Days"});

        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();
        
        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM item")) {

            while (rs.next()) {
                model.addRow(new Object[] {
                    rs.getObject("InventoryID"),
                    rs.getObject("ItemName"),
                    rs.getObject("Description"),
                    rs.getObject("UnitPrice"),
                    rs.getObject("QuantityInStock"),
                    rs.getObject("InventoryValue"),
                    rs.getObject("ReorderLevel"),
                    rs.getObject("ReorderTimeInDays"),
                });
            }

            table.setModel(model); // Set the model to the table

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading data from database", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

 // Main method to run the application
    public static void main(String[] args) {
        // Launch the main page frame
        EventQueue.invokeLater(() -> {
            try {
                MainPageFrame frame = new MainPageFrame(true);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
 // Update the inventory list based on the selected sort order
    private void updateInventoryList() {
        // Sort the inventory list based on the selected option and update the table
        String selectedSortOrder = (String) cmbSortOrder.getSelectedItem();
        List<Item> inventoryList = fetchAllInventory();

        // Apply Quick Sort
        quickSort(inventoryList, 0, inventoryList.size() - 1, selectedSortOrder);

        // Update JTable model with sorted inventoryList
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data

     // Iterate through each item in the sorted inventory list
        for (Item item : inventoryList) {
            // Add each item's details to a new row in the table model
            model.addRow(new Object[] {
                item.getInventoryID(), // Inventory ID of the item
                item.getItemName(), // Name... 
                item.getDescription(), // Description... 
                item.getUnitPrice(), // Unit price...
                item.getQuantityInStock(), // Quantity in stock.. 
                item.getInventoryValue(), // Inventory value...
                item.getReorderLevel(), // Reorder level...   
                item.getReorderTimeInDays() // Reorder time in days... 
            });
        }
    }

 // Determine if two items should be swapped based on the sort order
    private boolean shouldSwap(Item item1, Item item2, String sortOrder) {
        // Compare two items based on the selected sort order and return true if they should be swapped
        switch (sortOrder) {
        
        case "Inventory ID: A-Z":
            return item1.getInventoryID().compareToIgnoreCase(item2.getInventoryID()) < 0;
        case "Inventory ID: Z-A":
            return item1.getInventoryID().compareToIgnoreCase(item2.getInventoryID()) > 0;
        case "Item Name: A-Z":
            return item1.getItemName().compareToIgnoreCase(item2.getItemName()) < 0;
        case "Item Name: Z-A":
            return item1.getItemName().compareToIgnoreCase(item2.getItemName()) > 0;
            case "Unit Price: Lowest-Highest":
                return item1.getUnitPrice() < item2.getUnitPrice();
            case "Unit Price: Highest-Lowest":
                return item1.getUnitPrice() > item2.getUnitPrice();
            case "Quantity In Stock: Lowest-Highest":
                return item1.getQuantityInStock() < item2.getQuantityInStock();
            case "Quantity In Stock: Highest-Lowest":
                return item1.getQuantityInStock() > item2.getQuantityInStock();
            case "Inventory Value: Lowest-Highest":
                return item1.getInventoryValue() < item2.getInventoryValue();
            case "Inventory Value: Highest-Lowest":
                return item1.getInventoryValue() > item2.getInventoryValue();
            default:
                return false;
        }
    }
    
 // Fetch all inventory items from the database
    private List<Item> fetchAllInventory() {
        // Retrieve all inventory items from the database and return them as a list
    	List<Item> inventoryList = new ArrayList<>();
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM item")) {

            while (rs.next()) {
                try {
                    inventoryList.add(new Item(
                        rs.getString("InventoryID"),
                        rs.getString("ItemName"),
                        rs.getString("Description"),
                        rs.getDouble("UnitPrice"),
                        rs.getInt("QuantityInStock"),
                        rs.getDouble("InventoryValue"),
                        rs.getInt("ReorderLevel"),
                        rs.getInt("ReorderTimeInDays")
                    ));
                } catch (SQLException e) {
                    System.err.println("Error parsing inventory item: " + e.getMessage());
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return inventoryList;
    }


    class Item {
        private String inventoryID;
        private String itemName;
        private String description;
        private double unitPrice;
        private int quantityInStock;
        private double inventoryValue;
        private int reorderLevel;
        private int reorderTimeInDays;

        // Constructor
        public Item(String inventoryID, String itemName, String description, 
        		    double unitPrice, int quantityInStock, double inventoryValue,  
        		    int reorderLevel, int reorderTimeInDays) 
        {
            this.inventoryID = inventoryID;
            this.itemName = itemName;
            this.description = description;
            this.unitPrice = unitPrice;
            this.quantityInStock = quantityInStock;
            this.inventoryValue = inventoryValue;
            this.reorderLevel = reorderLevel;
            this.reorderTimeInDays = reorderTimeInDays;
        }

        // Getters
        public String getInventoryID() { return inventoryID; }
        public String getItemName() { return itemName; }
        public String getDescription() { return description; }
        public double getUnitPrice() { return unitPrice; }
        public int getQuantityInStock() { return quantityInStock; }
        public double getInventoryValue() { return inventoryValue; }
        public int getReorderLevel() { return reorderLevel;}
        public int getReorderTimeInDays() { return reorderTimeInDays;}
    }
    
 // Sort the inventory list using the Quick Sort algorithm
    private void quickSort(List<Item> items, int low, int high, String sortOrder) {
        // Implementation of the Quick Sort algorithm
        if (low < high) {
            int pi = partition(items, low, high, sortOrder);

            quickSort(items, low, pi - 1, sortOrder);
            quickSort(items, pi + 1, high, sortOrder);
        }
    }

 // Partition the list for the Quick Sort algorithm
    private int partition(List<Item> items, int low, int high, String sortOrder) {
        // Partition the list into two parts based on the pivot item
        Item pivot = items.get(high);
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (shouldSwap(items.get(j), pivot, sortOrder)) {
                i++;

                // swap items[i] and items[j]
                Item temp = items.get(i);
                items.set(i, items.get(j));
                items.set(j, temp);
            }
        }

        // swap items[i+1] and items[high] (or pivot)
        Item temp = items.get(i + 1);
        items.set(i + 1, items.get(high));
        items.set(high, temp);

        return i + 1;
    }
    
 // Database connector class to initialize and create necessary tables
    public class Connector {
        public static void main(String[] args) {
            // Initialize the database and create tables
            String url = DBConfig.getUrl();
            String user = DBConfig.getUser();
            String password = DBConfig.getPassword();
            
            DatabaseInitializer dbInitializer = new DatabaseInitializer(url, user, password);
            dbInitializer.createTables();
        }
    }

}
