package CSIA;

import javax.swing.*;	


import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;

import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InventoryLevelFrame extends JFrame {
	
    private JButton btnBack;
	
    public InventoryLevelFrame() {
    	getContentPane().setBackground(new Color(202, 253, 238));
        setTitle("Inventory Level");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 850, 650);
        getContentPane().setLayout(null);

        // Create dataset
        DefaultCategoryDataset dataset = createDataset();

        // Create chart
        JFreeChart barChart = ChartFactory.createBarChart(
                "Inventory Levels", 
                "Item Name", "Stock Level", 
                dataset, PlotOrientation.VERTICAL, 
                true, true, false);

        // Get the plot from the chart
        CategoryPlot plot = barChart.getCategoryPlot();

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(202, 204, 255)); // Set the color of the bars.
        
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
        
        // Add chart to a chart panel
        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setBounds(10, 10, 780, 550);
        getContentPane().add(chartPanel);
        
        btnBack = new JButton("Back");
        btnBack.setBounds(680, 572, 110, 30);
        getContentPane().add(btnBack);

        btnBack.addActionListener((ActionListener) new ActionListener() {
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
    
    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String url = DBConfig.getUrl();
        String user = DBConfig.getUser();
        String password = DBConfig.getPassword();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ItemName, QuantityInStock FROM item")) {

            while (rs.next()) {
                dataset.addValue(rs.getDouble("QuantityInStock"), "Stock Level", rs.getString("ItemName"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading inventory data", "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return dataset;
    }
}
