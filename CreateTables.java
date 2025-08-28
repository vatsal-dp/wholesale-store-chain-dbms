import java.sql.*;

/**
 * This class creates database tables for a retail management system using MariaDB.
 * It establishes relationships between various entities such as stores, staff, members,
 * transactions, products, and suppliers.
 */
public class CreateTables {
    // Database connection parameters
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/dshah24";
    private static final String user = <enter unity id>;
    private static final String password = <enter id number or password>;

    /**
     * Main method to execute the table creation process.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Load the MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;

            try {
                // Establish database connection
                connection = DriverManager.getConnection(jdbcURL, user, password);
                statement = connection.createStatement();
                
                // Create Store table - stores basic store information
                statement.executeUpdate("CREATE TABLE Store ("
                    + "store_id INT PRIMARY KEY, "          // Unique identifier for each store
                    + "address VARCHAR(255) NOT NULL, "    // Store's physical address
                    + "phone VARCHAR(20) NOT NULL"         // Store's contact number
                    + ")");
                
                // Create Staff table - stores employee information
                statement.executeUpdate("CREATE TABLE Staff ("
                    + "staff_id INT PRIMARY KEY,"          // Unique identifier for each staff member
                    + "first_name VARCHAR(50) NOT NULL,"   // Staff's first name
                    + "last_name VARCHAR(50) NOT NULL,"    // Staff's last name
                    + "birth_date DATE NOT NULL,"          // Staff's date of birth
                    + "employment_date DATE NOT NULL,"     // Date of hiring
                    + "role_name VARCHAR(50) NOT NULL,"    // Job title/role
                    + "description VARCHAR(255),"          // Additional role description
                    + "address VARCHAR(255),"             // Staff's home address
                    + "age INT"                           // Staff's age
                    + ");");
                
                // Create Member table - stores customer membership information
                statement.executeUpdate("CREATE TABLE Member ("
                    + "member_id INT PRIMARY KEY,"         // Unique identifier for each member
                    + "first_name VARCHAR(50) NOT NULL,"   // Member's first name
                    + "last_name VARCHAR(50) NOT NULL,"    // Member's last name
                    + "membership_level VARCHAR(20) NOT NULL," // Membership tier (e.g., Silver, Gold)
                    + "email VARCHAR(100) NOT NULL,"       // Member's email address
                    + "phone VARCHAR(20),"                 // Member's contact number
                    + "home_address VARCHAR(255),"         // Member's home address
                    + "active_status VARCHAR(20) NOT NULL DEFAULT 'Inactive'" // Membership status
                    + ");");
                
                // Create MemberSignUp table - tracks member registration details
                statement.executeUpdate("CREATE TABLE MemberSignUp ("
                    + "signup_id INT PRIMARY KEY,"         // Unique identifier for signup record
                    + "store_id INT NOT NULL,"             // Store where signup occurred
                    + "staff_id INT NOT NULL,"             // Staff who processed signup
                    + "member_id INT NOT NULL,"            // Member who signed up
                    + "signup_date DATETIME NOT NULL,"     // Date and time of signup
                    + "FOREIGN KEY (store_id) REFERENCES Store(store_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (staff_id) REFERENCES Staff(staff_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (member_id) REFERENCES Member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "UNIQUE(store_id, member_id)"        // Prevent duplicate signups per store
                    + ");");
                
                // Create Transaction table - stores purchase transaction details
                statement.executeUpdate("CREATE TABLE Transaction ("
                    + "transaction_id INT PRIMARY KEY,"    // Unique identifier for each transaction
                    + "store_id INT NOT NULL,"             // Store where transaction occurred
                    + "member_id INT,"                     // Member who made purchase (nullable)
                    + "cashier_id INT NOT NULL,"           // Staff who processed transaction
                    + "purchase_date DATETIME NOT NULL,"   // Date and time of purchase
                    + "total_amount DECIMAL(10,2) NOT NULL DEFAULT 0," // Total transaction amount
                    + "FOREIGN KEY (store_id) REFERENCES Store(store_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (member_id) REFERENCES Member(member_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (cashier_id) REFERENCES Staff(staff_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create Discount table - stores discount offer details
                statement.executeUpdate("CREATE TABLE Discount ("
                    + "discount_id INT PRIMARY KEY,"       // Unique identifier for each discount
                    + "discount_percentage DECIMAL(5, 2) NOT NULL," // Percentage of discount
                    + "max_discount_amount DECIMAL(10, 2)," // Maximum discount cap
                    + "valid_from DATE NOT NULL,"          // Start date of discount validity
                    + "valid_to DATE NOT NULL,"            // End date of discount validity
                    + "CHECK (discount_percentage > 0 AND discount_percentage <= 100)," // Validate percentage
                    + "CHECK (valid_from <= valid_to)"     // Ensure valid date range
                    + ");");
                
                // Create Product table - stores product information
                statement.executeUpdate("CREATE TABLE Product ("
                    + "product_id INT PRIMARY KEY,"        // Unique identifier for each product
                    + "product_name VARCHAR(100) NOT NULL," // Product name
                    + "market_price DECIMAL(10, 2) NOT NULL," // Regular price
                    + "production_date DATE,"              // Manufacturing date
                    + "expiration_date DATE,"              // Expiry date
                    + "discount_id INT,"                   // Associated discount (if any)
                    + "FOREIGN KEY (discount_id) REFERENCES Discount(discount_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create TransactionItem table - stores individual items in transactions
                statement.executeUpdate("CREATE TABLE TransactionItem ("
                    + "transaction_id INT,"                // Transaction reference
                    + "product_id INT,"                    // Product reference
                    + "quantity INT NOT NULL,"             // Number of items purchased
                    + "unit_price DECIMAL(10, 2) NOT NULL," // Price per unit
                    + "discount_amount DECIMAL(10, 2) NOT NULL DEFAULT 0," // Discount applied
                    + "PRIMARY KEY (transaction_id, product_id)," // Composite primary key
                    + "FOREIGN KEY (transaction_id) REFERENCES Transaction(transaction_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create Supplier table - stores supplier information
                statement.executeUpdate("CREATE TABLE Supplier ("
                    + "supplier_id INT PRIMARY KEY,"       // Unique identifier for each supplier
                    + "supplier_name VARCHAR(100) NOT NULL," // Supplier's name
                    + "supplier_contact VARCHAR(100),"     // Contact person/info
                    + "location VARCHAR(255),"             // Supplier's location
                    + "email VARCHAR(100)"                 // Supplier's email
                    + ");");
                
                // Create SupplierProduct table - tracks supplier-product pricing history
                statement.executeUpdate("CREATE TABLE SupplierProduct ("
                    + "supplier_id INT,"                   // Supplier reference
                    + "product_id INT,"                    // Product reference
                    + "buy_price DECIMAL(10, 2) NOT NULL," // Purchase price from supplier
                    + "price_effective_date DATE NOT NULL," // Date price became effective
                    + "is_current BOOLEAN NOT NULL DEFAULT TRUE," // Current price indicator
                    + "PRIMARY KEY (supplier_id, product_id, price_effective_date)," // Composite key
                    + "FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create StoreInventory table - tracks store stock levels
                statement.executeUpdate("CREATE TABLE StoreInventory ("
                    + "store_id INT,"                      // Store reference
                    + "product_id INT,"                    // Product reference
                    + "quantity INT NOT NULL DEFAULT 0,"   // Current stock quantity
                    + "last_updated DATETIME NOT NULL,"    // Last inventory update time
                    + "PRIMARY KEY (store_id, product_id)," // Composite primary key
                    + "FOREIGN KEY (store_id) REFERENCES Store(store_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create PurchaseOrders table - stores purchase order details
                statement.executeUpdate("CREATE TABLE PurchaseOrders ("
                    + "order_id INT PRIMARY KEY AUTO_INCREMENT," // Auto-incrementing order ID
                    + "supplier_id INT NOT NULL,"          // Supplier reference
                    + "store_id INT NOT NULL,"             // Store placing order
                    + "order_date DATETIME NOT NULL,"      // Date and time of order
                    + "paid bool DEFAULT FALSE,"           // Payment status
                    + "FOREIGN KEY (supplier_id) REFERENCES Supplier(supplier_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (store_id) REFERENCES Store(store_id) ON DELETE CASCADE ON UPDATE CASCADE"
                    + ");");
                
                // Create PurchaseItem table - stores items in purchase orders
                statement.executeUpdate("CREATE TABLE PurchaseItem ("
                    + "order_id INT,"                      // Purchase order reference
                    + "product_id INT,"                    // Product reference
                    + "quantity INT NOT NULL,"             // Number of items ordered
                    + "price DECIMAL(10, 2) NOT NULL,"     // Price per unit
                    + "PRIMARY KEY (order_id, product_id)," // Composite primary key
                    + "FOREIGN KEY (order_id) REFERENCES PurchaseOrders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "FOREIGN KEY (product_id) REFERENCES Product(product_id) ON DELETE CASCADE ON UPDATE CASCADE,"
                    + "CHECK (quantity > 0),"              // Ensure positive quantity
                    + "CHECK (price >= 0)"                 // Ensure non-negative price
                    + ");");

                // Note for TA: The discount_id in Product table is a foreign key referencing Discount(discount_id)

            } finally {
                // Clean up database resources
                close(result);
                close(statement);
                close(connection);
            }
        } catch(Throwable oops) {
            // Print any errors that occur during execution
            oops.printStackTrace();
        }
    }

    /**
     * Safely closes a database Connection.
     * @param connection The Connection object to close
     */
    static void close(Connection connection) {
        if(connection != null) {
            try {
                connection.close();
            } catch(Throwable whatever) {
                // Ignore any errors during closing
            }
        }
    }
    
    /**
     * Safely closes a database Statement.
     * @param statement The Statement object to close
     */
    static void close(Statement statement) {
        if(statement != null) {
            try {
                statement.close();
            } catch(Throwable whatever) {
                // Ignore any errors during closing
            }
        }
    }
    
    /**
     * Safely closes a database ResultSet.
     * @param result The ResultSet object to close
     */
    static void close(ResultSet result) {
        if(result != null) {
            try {
                result.close();
            } catch(Throwable whatever) {
                // Ignore any errors during closing
            }
        }
    }
}