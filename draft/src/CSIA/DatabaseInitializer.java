package CSIA;

import java.sql.*;

public class DatabaseInitializer {
    // Database connection parameters
    private String url;
    private String user;
    private String password;

    // Constructor to initialize the database connection parameters
    public DatabaseInitializer(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    // Method to create all necessary tables in the database
    public void createTables() {
        try (
            Connection conn = DriverManager.getConnection(url, user, password); // Establish a connection to the database
            Statement stmt = conn.createStatement() // Create a statement to execute SQL queries
        ) {
            // SQL statement to create the 'log' table if it does not exist
            String createLogTable = """
                CREATE TABLE IF NOT EXISTS log (
                    InventoryID VARCHAR(45) NOT NULL,
                    ItemName VARCHAR(45) NOT NULL,
                    Amount INT NOT NULL,
                    SalesDateTime DATETIME(6),
                    DecreaseType VARCHAR(45) NOT NULL
                )""";
            stmt.execute(createLogTable); // Execute the SQL statement

            // SQL statement to create the 'user' table with security questions and answers
            String createUserTable = """
                CREATE TABLE IF NOT EXISTS user (
                    Username VARCHAR(45) NOT NULL PRIMARY KEY,
                    Password VARCHAR(45) NOT NULL,
                    Role VARCHAR(20)
                )""";
            stmt.execute(createUserTable); // Execute the SQL statement
            
            // SQL statement to create the 'user_security' table with security questions and answers
            String createUser_SecurityTable = """
                CREATE TABLE IF NOT EXISTS user_security (
                    Username VARCHAR(45) NOT NULL,
                    SecurityQuestion VARCHAR(255) NOT NULL,
                    SecurityAnswer VARCHAR(255) NOT NULL,
                    FOREIGN KEY (Username) REFERENCES user(Username) ON DELETE CASCADE
                )""";
            stmt.execute(createUser_SecurityTable); // Corrected to execute the intended createUser_SecurityTable
            
            // SQL statement to create the 'item' table
            String createItemTable = """
                CREATE TABLE IF NOT EXISTS item (
                    InventoryID VARCHAR(45) NOT NULL PRIMARY KEY,
                    ItemName VARCHAR(45) NOT NULL,
                    Description VARCHAR(100),
                    UnitPrice FLOAT NOT NULL,
                    QuantityInStock INT NOT NULL,
                    InventoryValue FLOAT NOT NULL,
                    ReorderLevel INT NOT NULL,
                    ReorderTimeInDays INT NOT NULL
                )""";
            stmt.execute(createItemTable); // Execute the SQL statement

            // SQL statement to create the 'employees' table
            String createEmployeeTable = """
                CREATE TABLE IF NOT EXISTS employees (
                    employee_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(50) NOT NULL,
                    salary DECIMAL(10, 2) NOT NULL,
                    phone_number VARCHAR(15) NOT NULL
                )""";
            stmt.execute(createEmployeeTable); // Execute the SQL statement

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}
