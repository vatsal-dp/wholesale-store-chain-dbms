import java.sql.*;

/**
 * This class handles the deletion of database tables for a retail management system
 * using MariaDB. It safely drops all tables while managing foreign key constraints.
 */
public class DeleteTables {
    // Database connection parameters
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/dshah24"; // Using SERVICE_NAME
    private static final String user = <enter unity id>;    // Database username
    private static final String password = <enter id number or password>; // Database password

    /**
     * Main method to execute the table deletion process.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Load the MariaDB JDBC driver to enable database connectivity
            Class.forName("org.mariadb.jdbc.Driver");

            Connection connection = null;
            Statement statement = null;
            ResultSet result = null;

            try {
                // Establish connection to the database using provided credentials
                connection = DriverManager.getConnection(jdbcURL, user, password);

                // Create a statement object for executing SQL commands
                statement = connection.createStatement();
                
                // Disable foreign key checks to allow dropping tables with dependencies
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 0;");
                
                // Drop all tables if they exist, using CASCADE where applicable
                statement.executeUpdate("DROP TABLE if exists Store CASCADE");        // Store information
                statement.executeUpdate("DROP TABLE if exists Staff");               // Staff records
                statement.executeUpdate("DROP TABLE if exists Member");              // Member records
                statement.executeUpdate("DROP TABLE if exists MemberSignUp");        // Membership signup records
                statement.executeUpdate("DROP TABLE if exists Transaction");         // Purchase transactions
                statement.executeUpdate("DROP TABLE if exists Product");            // Product catalog
                statement.executeUpdate("DROP TABLE if exists TransactionItem");     // Transaction line items
                statement.executeUpdate("DROP TABLE if exists Supplier");           // Supplier information
                statement.executeUpdate("DROP TABLE if exists SupplierProduct");     // Supplier-product pricing
                statement.executeUpdate("DROP TABLE if exists StoreInventory");      // Store stock levels
                statement.executeUpdate("DROP TABLE if exists Discount");           // Discount offers
                statement.executeUpdate("DROP TABLE if exists PurchaseOrders");      // Purchase orders
                statement.executeUpdate("DROP TABLE if exists PurchaseItem");        // Purchase order items
                
                // Re-enable foreign key checks after deletion
                statement.executeUpdate("SET FOREIGN_KEY_CHECKS = 1;");

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