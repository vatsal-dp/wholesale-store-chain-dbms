import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * This class populates a retail management database with initial data using MariaDB.
 * It inserts sample records into various tables including stores, staff, members,
 * transactions, products, and suppliers.
 */
public class InsertData {
    // Database connection parameters
    private static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/dshah24";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String user = <enter unity id>;    // Database username
    private static final String password = <enter id number or password>; // Database password

    /**
     * Main method to execute the data insertion process.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        try {
            // Load the MariaDB JDBC driver
            Class.forName("org.mariadb.jdbc.Driver");

            Connection conn = null;
            Statement statement = null;
            ResultSet result = null;

            try {
                // Establish database connection
                conn = DriverManager.getConnection(jdbcURL, user, password);
                statement = conn.createStatement();
                
                // Insert data into all tables
                insertStoreData(conn);
                insertStaffData(conn);
                insertMemberData(conn);
                insertMemberSignUpData(conn);
                insertSupplierData(conn);
                insertDiscountData(conn);
                insertProductData(conn);
                insertSupplierProductData(conn);
                insertStoreInventoryData(conn);
                insertTransactionData(conn);
                insertTransactionItemData(conn);
                insertPurchaseOrdersData(conn);
                insertPurchaseItemData(conn);

            } finally {
                // Clean up database resources
                close(result);
                close(statement);
                close(conn);
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
            try { connection.close(); } catch(Throwable whatever) {}
        }
    }

    /**
     * Safely closes a database Statement.
     * @param statement The Statement object to close
     */
    static void close(Statement statement) {
        if(statement != null) {
            try { statement.close(); } catch(Throwable whatever) {}
        }
    }

    /**
     * Safely closes a database ResultSet.
     * @param result The ResultSet object to close
     */
    static void close(ResultSet result) {
        if(result != null) {
            try { result.close(); } catch(Throwable whatever) {}
        }
    }

    /**
     * Inserts sample store data into the Store table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertStoreData(Connection conn) throws SQLException {
        String sql = "INSERT INTO Store (store_id, address, phone) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Store 1 - Raleigh Main Campus
            pstmt.setInt(1, 1001);
            pstmt.setString(2, "1021 Main Campus Dr, Raleigh, NC, 27606");
            pstmt.setString(3, "9194789124");
            pstmt.executeUpdate();
            
            // Store 2 - Raleigh Partners Way
            pstmt.setInt(1, 1002);
            pstmt.setString(2, "851 Partners Way, Raleigh, NC, 27606");
            pstmt.setString(3, "9195929621");
            pstmt.executeUpdate();
        }
        System.out.println("Store data inserted successfully!");
    }

    /**
     * Inserts sample staff data into the Staff table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertStaffData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO Staff (staff_id, first_name, last_name, birth_date, employment_date, role_name, description, address, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Staff 1 - Alice Johnson (Manager)
            pstmt.setInt(1, 201);
            pstmt.setString(2, "Alice");
            pstmt.setString(3, "Johnson");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("06-15-1990").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2020").getTime()));
            pstmt.setString(6, "Manager");
            pstmt.setString(7, null); // No description
            pstmt.setString(8, "111 Wolf Street, Raleigh, NC 27606");
            pstmt.setInt(9, 34);
            pstmt.executeUpdate();

            // Staff 2 - Bob Smith (Assistant Manager)
            pstmt.setInt(1, 202);
            pstmt.setString(2, "Bob");
            pstmt.setString(3, "Smith");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("09-22-1995").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2022").getTime()));
            pstmt.setString(6, "Assistant Manager");
            pstmt.setString(7, null);
            pstmt.setString(8, "222 Fox Ave, Durham, NC 27701");
            pstmt.setInt(9, 29);
            pstmt.executeUpdate();

            // Staff 3 - Charlie Davis (Cashier)
            pstmt.setInt(1, 203);
            pstmt.setString(2, "Charlie");
            pstmt.setString(3, "Davis");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-30-1984").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2018").getTime()));
            pstmt.setString(6, "Cashier");
            pstmt.setString(7, null);
            pstmt.setString(8, "333 Bear Rd, Greensboro, NC 27282");
            pstmt.setInt(9, 40);
            pstmt.executeUpdate();

            // Staff 4 - David Lee (Warehouse Checker)
            pstmt.setInt(1, 204);
            pstmt.setString(2, "David");
            pstmt.setString(3, "Lee");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("12-10-1979").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2015").getTime()));
            pstmt.setString(6, "Warehouse Checker");
            pstmt.setString(7, null);
            pstmt.setString(8, "444 Eagle Dr, Raleigh, NC 27606");
            pstmt.setInt(9, 45);
            pstmt.executeUpdate();

            // Staff 5 - Emma White (Billing Staff)
            pstmt.setInt(1, 205);
            pstmt.setString(2, "Emma");
            pstmt.setString(3, "White");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("03-25-1995").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2021").getTime()));
            pstmt.setString(6, "Billing Staff");
            pstmt.setString(7, null);
            pstmt.setString(8, "555 Deer Ln, Durham, NC 27560");
            pstmt.setInt(9, 30);
            pstmt.executeUpdate();

            // Staff 6 - Frank Harris (Billing Staff)
            pstmt.setInt(1, 206);
            pstmt.setString(2, "Frank");
            pstmt.setString(3, "Harris");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("07-08-1986").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2019").getTime()));
            pstmt.setString(6, "Billing Staff");
            pstmt.setString(7, null);
            pstmt.setString(8, "666 Owl Ct, Raleigh, NC 27610");
            pstmt.setInt(9, 38);
            pstmt.executeUpdate();

            // Staff 7 - Isla Scott (Warehouse Checker)
            pstmt.setInt(1, 207);
            pstmt.setString(2, "Isla");
            pstmt.setString(3, "Scott");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("03-15-1992").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2023").getTime()));
            pstmt.setString(6, "Warehouse Checker");
            pstmt.setString(7, null);
            pstmt.setString(8, "777 Lynx Rd, Raleigh, NC 27612");
            pstmt.setInt(9, 33);
            pstmt.executeUpdate();

            // Staff 8 - Jack Lewis (Cashier)
            pstmt.setInt(1, 208);
            pstmt.setString(2, "Jack");
            pstmt.setString(3, "Lewis");
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-30-1983").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-01-2022").getTime()));
            pstmt.setString(6, "Cashier");
            pstmt.setString(7, null);
            pstmt.setString(8, "888 Falcon St, Greensboro, NC 27377");
            pstmt.setInt(9, 41);
            pstmt.executeUpdate();
        }
        System.out.println("Staff data inserted successfully!");
    }

    /**
     * Inserts sample member data into the Member table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertMemberData(Connection conn) throws SQLException {
        String sql = "INSERT INTO Member (member_id, first_name, last_name, membership_level, email, phone, home_address, active_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Member 1 - John Doe (Gold)
            pstmt.setInt(1, 501);
            pstmt.setString(2, "John");
            pstmt.setString(3, "Doe");
            pstmt.setString(4, "Gold");
            pstmt.setString(5, "john.doe@gmail.com");
            pstmt.setString(6, "9194285314");
            pstmt.setString(7, "12 Elm St, Raleigh, NC 27607");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();

            // Member 2 - Emily Smith (Silver)
            pstmt.setInt(1, 502);
            pstmt.setString(2, "Emily");
            pstmt.setString(3, "Smith");
            pstmt.setString(4, "Silver");
            pstmt.setString(5, "emily.smith@gmail.com");
            pstmt.setString(6, "9844235314");
            pstmt.setString(7, "34 Oak Ave, Raleigh, NC 27606");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();

            // Member 3 - Michael Brown (Platinum)
            pstmt.setInt(1, 503);
            pstmt.setString(2, "Michael");
            pstmt.setString(3, "Brown");
            pstmt.setString(4, "Platinum");
            pstmt.setString(5, "michael.brown@gmail.com");
            pstmt.setString(6, "9194820931");
            pstmt.setString(7, "56 Pine Rd, Raleigh, NC 27607");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();

            // Member 4 - Sarah Johnson (Gold)
            pstmt.setInt(1, 504);
            pstmt.setString(2, "Sarah");
            pstmt.setString(3, "Johnson");
            pstmt.setString(4, "Gold");
            pstmt.setString(5, "sarah.johnson@gmail.com");
            pstmt.setString(6, "9841298435");
            pstmt.setString(7, "78 Maple Dr, Raleigh, NC 27607");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();

            // Member 5 - David Williams (Silver)
            pstmt.setInt(1, 505);
            pstmt.setString(2, "David");
            pstmt.setString(3, "Williams");
            pstmt.setString(4, "Silver");
            pstmt.setString(5, "david.williams@gmail.com");
            pstmt.setString(6, "9194829424");
            pstmt.setString(7, "90 Birch Ln, Raleigh, NC 27607");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();

            // Member 6 - Anna Miller (Platinum)
            pstmt.setInt(1, 506);
            pstmt.setString(2, "Anna");
            pstmt.setString(3, "Miller");
            pstmt.setString(4, "Platinum");
            pstmt.setString(5, "anna.miller@gmail.com");
            pstmt.setString(6, "9848519427");
            pstmt.setString(7, "101 Oak Ct, Raleigh, NC 27607");
            pstmt.setString(8, "Active");
            pstmt.executeUpdate();
        }
        System.out.println("Member data inserted successfully!");
    }

    /**
     * Inserts sample member signup data into the MemberSignUp table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertMemberSignUpData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO MemberSignUp (signup_id, store_id, staff_id, member_id, signup_date) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Sign-up 1 - John Doe
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 1001);
            pstmt.setInt(3, 201);
            pstmt.setInt(4, 501);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("01-31-2024").getTime()));
            pstmt.executeUpdate();

            // Sign-up 2 - Emily Smith
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 1001);
            pstmt.setInt(3, 201);
            pstmt.setInt(4, 502);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("02-28-2022").getTime()));
            pstmt.executeUpdate();

            // Sign-up 3 - Michael Brown
            pstmt.setInt(1, 3);
            pstmt.setInt(2, 1002);
            pstmt.setInt(3, 202);
            pstmt.setInt(4, 503);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("03-22-2020").getTime()));
            pstmt.executeUpdate();

            // Sign-up 4 - Sarah Johnson
            pstmt.setInt(1, 4);
            pstmt.setInt(2, 1002);
            pstmt.setInt(3, 202);
            pstmt.setInt(4, 504);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("03-15-2023").getTime()));
            pstmt.executeUpdate();

            // Sign-up 5 - David Williams
            pstmt.setInt(1, 5);
            pstmt.setInt(2, 1002);
            pstmt.setInt(3, 202);
            pstmt.setInt(4, 505);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("08-23-2024").getTime()));
            pstmt.executeUpdate();

            // Sign-up 6 - Anna Miller
            pstmt.setInt(1, 6);
            pstmt.setInt(2, 1002);
            pstmt.setInt(3, 202);
            pstmt.setInt(4, 506);
            pstmt.setTimestamp(5, new Timestamp(dateFormat.parse("02-10-2025").getTime()));
            pstmt.executeUpdate();
        }
        System.out.println("MemberSignUp data inserted successfully!");
    }

    /**
     * Inserts sample supplier data into the Supplier table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertSupplierData(Connection conn) throws SQLException {
        String sql = "INSERT INTO Supplier (supplier_id, supplier_name, supplier_contact, location, email) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Supplier 1 - Fresh Farms Ltd.
            pstmt.setInt(1, 401);
            pstmt.setString(2, "Fresh Farms Ltd.");
            pstmt.setString(3, "9194248251");
            pstmt.setString(4, "123 Greenway Blvd, Raleigh, NC 27615");
            pstmt.setString(5, "contact@freshfarms.com");
            pstmt.executeUpdate();

            // Supplier 2 - Organic Good Inc.
            pstmt.setInt(1, 402);
            pstmt.setString(2, "Organic Good Inc.");
            pstmt.setString(3, "9841384298");
            pstmt.setString(4, "456 Healthy Rd, Raleigh, NC 27606");
            pstmt.setString(5, "info@organicgoods.com");
            pstmt.executeUpdate();
        }
        System.out.println("Supplier data inserted successfully!");
    }

    /**
     * Inserts sample discount data into the Discount table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertDiscountData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO Discount (discount_id, discount_percentage, max_discount_amount, valid_from, valid_to) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Discount 1 - 10% Off
            pstmt.setInt(1, 1);
            pstmt.setDouble(2, 10.0);
            pstmt.setNull(3, java.sql.Types.DECIMAL); // No max discount amount
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-10-2024").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("05-10-2024").getTime()));
            pstmt.executeUpdate();

            // Discount 2 - 20% Off
            pstmt.setInt(1, 2);
            pstmt.setDouble(2, 20.0);
            pstmt.setNull(3, java.sql.Types.DECIMAL);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("02-12-2023").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("02-19-2023").getTime()));
            pstmt.executeUpdate();
        }
        System.out.println("Discount data inserted successfully!");
    }

    /**
     * Inserts sample product data into the Product table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertProductData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO Product (product_id, product_name, market_price, production_date, expiration_date, discount_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Product 1 - Organic Apples
            pstmt.setInt(1, 301);
            pstmt.setString(2, "Organic Apples");
            pstmt.setDouble(3, 2.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-12-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-20-2025").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();

            // Product 2 - Whole Grain Bread
            pstmt.setInt(1, 302);
            pstmt.setString(2, "Whole Grain Bread");
            pstmt.setDouble(3, 3.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-10-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-15-2025").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();

            // Product 3 - Almond Milk (with Discount 2)
            pstmt.setInt(1, 303);
            pstmt.setString(2, "Almond Milk");
            pstmt.setDouble(3, 4.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-15-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-30-2025").getTime()));
            pstmt.setInt(6, 2);
            pstmt.executeUpdate();

            // Product 4 - Brown Rice
            pstmt.setInt(1, 304);
            pstmt.setString(2, "Brown Rice");
            pstmt.setDouble(3, 3.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-12-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-20-2026").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();

            // Product 5 - Olive Oil
            pstmt.setInt(1, 305);
            pstmt.setString(2, "Olive Oil");
            pstmt.setDouble(3, 7.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-04-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("04-20-2027").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();

            // Product 6 - Whole Chicken (with Discount 1)
            pstmt.setInt(1, 306);
            pstmt.setString(2, "Whole Chicken");
            pstmt.setDouble(3, 13.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-12-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("05-12-2025").getTime()));
            pstmt.setInt(6, 1);
            pstmt.executeUpdate();

            // Product 7 - Cheddar Cheese
            pstmt.setInt(1, 307);
            pstmt.setString(2, "Cheddar Cheese");
            pstmt.setDouble(3, 4.2);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-12-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("10-12-2025").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();

            // Product 8 - Dark Chocolate
            pstmt.setInt(1, 308);
            pstmt.setString(2, "Dark Chocolate");
            pstmt.setDouble(3, 3.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-12-2025").getTime()));
            pstmt.setDate(5, new java.sql.Date(dateFormat.parse("06-20-2026").getTime()));
            pstmt.setNull(6, java.sql.Types.INTEGER);
            pstmt.executeUpdate();
        }
        System.out.println("Product data inserted successfully!");
    }

    /**
     * Inserts sample supplier-product relationships into the SupplierProduct table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertSupplierProductData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO SupplierProduct (supplier_id, product_id, buy_price, price_effective_date, is_current) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Supplier 401 - Fresh Farms Ltd.
            pstmt.setInt(1, 401);
            pstmt.setInt(2, 301); // Organic Apples
            pstmt.setDouble(3, 1.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 401);
            pstmt.setInt(2, 302); // Whole Grain Bread
            pstmt.setDouble(3, 2.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 401);
            pstmt.setInt(2, 303); // Almond Milk
            pstmt.setDouble(3, 3.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            // Supplier 402 - Organic Good Inc.
            pstmt.setInt(1, 402);
            pstmt.setInt(2, 304); // Brown Rice
            pstmt.setDouble(3, 2.8);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 402);
            pstmt.setInt(2, 305); // Olive Oil
            pstmt.setDouble(3, 5.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 402);
            pstmt.setInt(2, 306); // Whole Chicken
            pstmt.setDouble(3, 10.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 402);
            pstmt.setInt(2, 307); // Cheddar Cheese
            pstmt.setDouble(3, 3.0);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();

            pstmt.setInt(1, 402);
            pstmt.setInt(2, 308); // Dark Chocolate
            pstmt.setDouble(3, 2.5);
            pstmt.setDate(4, new java.sql.Date(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, true);
            pstmt.executeUpdate();
        }
        System.out.println("SupplierProduct data inserted successfully!");
    }

    /**
     * Inserts sample store inventory data into the StoreInventory table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertStoreInventoryData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO StoreInventory (store_id, product_id, quantity, last_updated) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Store 1002 Inventory
            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 301); // Organic Apples
            pstmt.setInt(3, 120);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-12-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 302); // Whole Grain Bread
            pstmt.setInt(3, 80);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-10-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 303); // Almond Milk
            pstmt.setInt(3, 150);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-15-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 304); // Brown Rice
            pstmt.setInt(3, 200);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-12-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 305); // Olive Oil
            pstmt.setInt(3, 90);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-04-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 306); // Whole Chicken
            pstmt.setInt(3, 75);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-12-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 307); // Cheddar Cheese
            pstmt.setInt(3, 60);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-12-2025").getTime()));
            pstmt.executeUpdate();

            pstmt.setInt(1, 1002);
            pstmt.setInt(2, 308); // Dark Chocolate
            pstmt.setInt(3, 50);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-12-2025").getTime()));
            pstmt.executeUpdate();
        }
        System.out.println("StoreInventory data inserted successfully!");
    }

    /**
     * Inserts sample transaction data into the Transaction table, checking member status.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertTransactionData(Connection conn) throws SQLException, ParseException {
        String checkStatusSql = "SELECT active_status FROM Member WHERE member_id = ?";
        String insertSql = "INSERT INTO Transaction (transaction_id, store_id, member_id, cashier_id, purchase_date, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement checkStmt = conn.prepareStatement(checkStatusSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
            
            // Transaction 1 - Emily Smith
            checkStmt.setInt(1, 502);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && "Active".equalsIgnoreCase(rs.getString("active_status"))) {
                insertStmt.setInt(1, 701);
                insertStmt.setInt(2, 1002);
                insertStmt.setInt(3, 502);
                insertStmt.setInt(4, 203); // Cashier Charlie Davis
                insertStmt.setTimestamp(5, new Timestamp(dateFormat.parse("02-10-2024").getTime()));
                insertStmt.setDouble(6, 45.0);
                insertStmt.executeUpdate();
                System.out.println("Transaction 701 inserted successfully!");
            } else {
                System.out.println("Transaction 701 skipped: Member 502 is not active.");
            }
            rs.close();

            // Transaction 2 - Emily Smith
            checkStmt.setInt(1, 502);
            rs = checkStmt.executeQuery();
            if (rs.next() && "Active".equalsIgnoreCase(rs.getString("active_status"))) {
                insertStmt.setInt(1, 702);
                insertStmt.setInt(2, 1002);
                insertStmt.setInt(3, 502);
                insertStmt.setInt(4, 208); // Cashier Jack Lewis
                insertStmt.setTimestamp(5, new Timestamp(dateFormat.parse("09-12-2024").getTime()));
                insertStmt.setDouble(6, 60.75);
                insertStmt.executeUpdate();
                System.out.println("Transaction 702 inserted successfully!");
            } else {
                System.out.println("Transaction 702 skipped: Member 502 is not active.");
            }
            rs.close();

            // Transaction 3 - Emily Smith
            checkStmt.setInt(1, 502);
            rs = checkStmt.executeQuery();
            if (rs.next() && "Active".equalsIgnoreCase(rs.getString("active_status"))) {
                insertStmt.setInt(1, 703);
                insertStmt.setInt(2, 1002);
                insertStmt.setInt(3, 502);
                insertStmt.setInt(4, 208); // Cashier Jack Lewis
                insertStmt.setTimestamp(5, new Timestamp(dateFormat.parse("09-23-2024").getTime()));
                insertStmt.setDouble(6, 78.9);
                insertStmt.executeUpdate();
                System.out.println("Transaction 703 inserted successfully!");
            } else {
                System.out.println("Transaction 703 skipped: Member 502 is not active.");
            }
            rs.close();

            // Transaction 4 - Sarah Johnson
            checkStmt.setInt(1, 504);
            rs = checkStmt.executeQuery();
            if (rs.next() && "Active".equalsIgnoreCase(rs.getString("active_status"))) {
                insertStmt.setInt(1, 704);
                insertStmt.setInt(2, 1002);
                insertStmt.setInt(3, 504);
                insertStmt.setInt(4, 208); // Cashier Jack Lewis
                insertStmt.setTimestamp(5, new Timestamp(dateFormat.parse("07-23-2024").getTime()));
                insertStmt.setDouble(6, 32.5);
                insertStmt.executeUpdate();
                System.out.println("Transaction 704 inserted successfully!");
            } else {
                System.out.println("Transaction 704 skipped: Member 504 is not active.");
            }
            rs.close();

            System.out.println("Transaction data insertion process completed!");
        } catch (SQLException e) {
            System.out.println("Error inserting transaction data: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Inserts sample transaction item data into the TransactionItem table, calculating discounts.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertTransactionItemData(Connection conn) throws SQLException {
        String sql = "INSERT INTO TransactionItem (transaction_id, product_id, quantity, unit_price, discount_amount) VALUES (?, ?, ?, ?, ?)";
        String productQuery = "SELECT product_id, market_price, discount_id FROM Product WHERE product_name = ?";
        String discountQuery = "SELECT discount_percentage, max_discount_amount, valid_from, valid_to FROM Discount WHERE discount_id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             PreparedStatement productStmt = conn.prepareStatement(productQuery);
             PreparedStatement discountStmt = conn.prepareStatement(discountQuery)) {

            // Transaction details stored in a Map for easy access
            Map<Integer, TransactionDetails> transactions = new HashMap<>();
            transactions.put(701, new TransactionDetails("Organic Apples, Whole Grain Bread", 45.0, "02-10-2024"));
            transactions.put(702, new TransactionDetails("Almond Milk, Brown Rice, Olive Oil", 60.75, "09-12-2024"));
            transactions.put(703, new TransactionDetails("Dark Chocolate, Olive Oil, Almond Milk", 78.9, "09-23-2024"));
            transactions.put(704, new TransactionDetails("Whole Chicken", 32.5, "07-23-2024"));

            for (Map.Entry<Integer, TransactionDetails> entry : transactions.entrySet()) {
                int transactionId = entry.getKey();
                TransactionDetails details = entry.getValue();
                String[] products = details.productList.split(",\\s*");
                double totalPrice = details.totalPrice;
                Timestamp purchaseDate;
                try {
                    purchaseDate = new Timestamp(dateFormat.parse(details.purchaseDate).getTime());
                } catch (ParseException e) {
                    System.out.println("Error parsing date for transaction " + transactionId + ": " + e.getMessage());
                    continue;
                }
                
                // Calculate total cost without discount to distribute quantities
                double totalCostNoDiscount = 0;
                Map<String, Double> productPrices = new HashMap<>();
                Map<String, Double> productDiscounts = new HashMap<>();
                Map<String, Integer> productIds = new HashMap<>();

                for (String productName : products) {
                    productStmt.setString(1, productName.trim());
                    ResultSet productRs = productStmt.executeQuery();
                    if (productRs.next()) {
                        int productId = productRs.getInt("product_id");
                        double marketPrice = productRs.getDouble("market_price");
                        int discountId = productRs.getInt("discount_id");
                        double discountAmount = 0;

                        if (discountId != 0) {
                            discountStmt.setInt(1, discountId);
                            ResultSet discountRs = discountStmt.executeQuery();
                            if (discountRs.next()) {
                                double discountPercentage = discountRs.getDouble("discount_percentage");
                                Double maxDiscountAmount = discountRs.getObject("max_discount_amount") != null ? 
                                    discountRs.getDouble("max_discount_amount") : null;
                                Timestamp validFrom = discountRs.getTimestamp("valid_from");
                                Timestamp validTo = discountRs.getTimestamp("valid_to");

                                if (purchaseDate.after(validFrom) && purchaseDate.before(validTo)) {
                                    double calculatedDiscount = marketPrice * (discountPercentage / 100);
                                    if (maxDiscountAmount != null && calculatedDiscount > maxDiscountAmount) {
                                        discountAmount = maxDiscountAmount;
                                    } else {
                                        discountAmount = calculatedDiscount;
                                    }
                                }
                            }
                            discountRs.close();
                        }

                        productIds.put(productName, productId);
                        productPrices.put(productName, marketPrice);
                        productDiscounts.put(productName, discountAmount);
                        totalCostNoDiscount += marketPrice;
                    }
                    productRs.close();
                }

                // Distribute quantities proportionally to match total_price
                double remainingTotal = totalPrice;
                int remainingProducts = products.length;

                for (String productName : products) {
                    int productId = productIds.get(productName);
                    double unitPrice = productPrices.get(productName);
                    double discountPerUnit = productDiscounts.get(productName);

                    // Estimate quantity based on proportion of total cost
                    double itemCostNoDiscount = unitPrice / totalCostNoDiscount * totalPrice;
                    int quantity = (int) Math.round((itemCostNoDiscount - discountPerUnit) / (unitPrice - discountPerUnit));
                    if (remainingProducts == 1) {
                        // Adjust last item to match exact total
                        double calculatedTotalSoFar = totalPrice - remainingTotal;
                        quantity = (int) Math.round((remainingTotal - calculatedTotalSoFar) / (unitPrice - discountPerUnit));
                    }

                    if (quantity <= 0) quantity = 1; // Ensure at least 1 unit
                    double itemTotal = quantity * (unitPrice - discountPerUnit);
                    remainingTotal -= itemTotal;
                    remainingProducts--;

                    pstmt.setInt(1, transactionId);
                    pstmt.setInt(2, productId);
                    pstmt.setInt(3, quantity);
                    pstmt.setDouble(4, unitPrice);
                    pstmt.setDouble(5, discountPerUnit * quantity);
                    pstmt.executeUpdate();
                }
            }
            System.out.println("TransactionItem data inserted successfully!");
        }
    }

    /**
     * Inner class to hold transaction details for easier processing.
     */
    private static class TransactionDetails {
        String productList;  // Comma-separated list of product names
        double totalPrice;   // Total transaction amount
        String purchaseDate; // Date of purchase in MM-dd-yyyy format

        /**
         * Constructor for TransactionDetails.
         * @param productList List of products in the transaction
         * @param totalPrice Total cost of the transaction
         * @param purchaseDate Date of the transaction
         */
        TransactionDetails(String productList, double totalPrice, String purchaseDate) {
            this.productList = productList;
            this.totalPrice = totalPrice;
            this.purchaseDate = purchaseDate;
        }
    }

    /**
     * Inserts sample purchase order data into the PurchaseOrders table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     * @throws ParseException if date parsing fails
     */
    private static void insertPurchaseOrdersData(Connection conn) throws SQLException, ParseException {
        String sql = "INSERT INTO PurchaseOrders (order_id, supplier_id, store_id, order_date, paid) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Purchase Order 1 - From Fresh Farms Ltd.
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 401);
            pstmt.setInt(3, 1002);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();

            // Purchase Order 2 - From Organic Good Inc.
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 402);
            pstmt.setInt(3, 1002);
            pstmt.setTimestamp(4, new Timestamp(dateFormat.parse("04-01-2025").getTime()));
            pstmt.setBoolean(5, false);
            pstmt.executeUpdate();
        }
        System.out.println("PurchaseOrders data inserted successfully!");
    }

    /**
     * Inserts sample purchase item data into the PurchaseItem table.
     * @param conn Database connection
     * @throws SQLException if a database error occurs
     */
    private static void insertPurchaseItemData(Connection conn) throws SQLException {
        String sql = "INSERT INTO PurchaseItem (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Purchase Order 1 items (from Supplier 401)
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 301); // Organic Apples
            pstmt.setInt(3, 120);
            pstmt.setDouble(4, 1.5);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 302); // Whole Grain Bread
            pstmt.setInt(3, 80);
            pstmt.setDouble(4, 2.0);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 1);
            pstmt.setInt(2, 303); // Almond Milk
            pstmt.setInt(3, 150);
            pstmt.setDouble(4, 3.5);
            pstmt.executeUpdate();

            // Purchase Order 2 items (from Supplier 402)
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 304); // Brown Rice
            pstmt.setInt(3, 200);
            pstmt.setDouble(4, 2.8);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 305); // Olive Oil
            pstmt.setInt(3, 90);
            pstmt.setDouble(4, 5.0);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 306); // Whole Chicken
            pstmt.setInt(3, 75);
            pstmt.setDouble(4, 10.0);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 307); // Cheddar Cheese
            pstmt.setInt(3, 60);
            pstmt.setDouble(4, 3.0);
            pstmt.executeUpdate();
            
            pstmt.setInt(1, 2);
            pstmt.setInt(2, 308); // Dark Chocolate
            pstmt.setInt(3, 50);
            pstmt.setDouble(4, 2.5);
            pstmt.executeUpdate();
        }
        System.out.println("PurchaseItem data inserted successfully!");
    }
}