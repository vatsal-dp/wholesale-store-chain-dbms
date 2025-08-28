import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Query_helper {
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/dshah24";
    private static final String USER = <enter unity id>;
    private static final String PASSWORD = <enter id number or password>;

    /**
     * Executes various database queries based on user selection.
     * 
     * @param choice The user's menu selection (1-8) determining which query to execute
     * @param connection Active database connection
     * @param scanner Scanner object for user input
     */
    public static void executeQuery(int choice, Connection connection, Scanner scanner) {
        String query = "";
        PreparedStatement statement = null;
        
        try {
            switch (choice) {
                case 1: // Supplier Unpaid Orders
                    System.out.print("Enter Supplier ID: ");
                    int supplierId = Integer.parseInt(scanner.nextLine());
                    // Query to retrieve unpaid orders for a specific supplier
                    query = "SELECT s.supplier_id, s.supplier_name, s.supplier_contact, po.order_id, po.order_date, " +
                            "SUM(pi.quantity * pi.price) AS total_amount, " +
                            "CASE WHEN po.paid = 1 THEN 'Paid' ELSE 'Unpaid' END AS payment_status " +
                            "FROM Supplier s JOIN PurchaseOrders po ON s.supplier_id = po.supplier_id " +
                            "JOIN PurchaseItem pi ON po.order_id = pi.order_id " +
                            "WHERE s.supplier_id = ? AND po.paid = 0 " +
                            "GROUP BY s.supplier_id, s.supplier_name, s.supplier_contact, po.order_id, po.order_date, po.paid " +
                            "ORDER BY po.order_date;";
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, supplierId);
                    break;
                    
                case 2: // Platinum Reward Checks
                    System.out.print("Enter year for transactions (e.g. 2025): ");
                    int year = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter member_id (optional, press Enter to skip): ");
                    String memberIdInput = scanner.nextLine();
                    
                    if (memberIdInput.isEmpty()) {
                        // Query for cashback amounts for all platinum members in a given year
                        query = "SELECT member_id, (final_transaction_total * 0.02) AS cashback_amount " +
                                "FROM (SELECT m.member_id, " +
                                "SUM(t.total_amount) AS final_transaction_total " +
                                "FROM Transaction t " +
                                "JOIN Member m ON t.member_id = m.member_id " +
                                "WHERE m.membership_level = 'Platinum' AND m.active_status = 'Active' " +
                                "AND YEAR(t.purchase_date) = ? " +
                                "GROUP BY m.member_id) AS TransactionTotals " +
                                "WHERE final_transaction_total > 0 " +
                                "ORDER BY member_id;";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, year);
                    } else {
                        int memberId = Integer.parseInt(memberIdInput);
                        // Query for cashback amount for a specific platinum member in a given year
                        query = "SELECT member_id, (final_transaction_total * 0.02) AS cashback_amount " +
                                "FROM (SELECT m.member_id, " +
                                "SUM(t.total_amount) AS final_transaction_total " +
                                "FROM Transaction t " +
                                "JOIN Member m ON t.member_id = m.member_id " +
                                "WHERE m.membership_level = 'Platinum' AND m.active_status = 'Active' " +
                                "AND YEAR(t.purchase_date) = ? " +
                                "AND m.member_id = ? " +
                                "GROUP BY m.member_id) AS TransactionTotals " +
                                "WHERE final_transaction_total > 0;";
                        statement = connection.prepareStatement(query);
                        statement.setInt(1, year);
                        statement.setInt(2, memberId);
                    }
                    break;
                    
                case 3: // Enter New Transaction
                    System.out.println("=== Enter New Transaction ===");
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    
                    try {
                        // Start transaction to ensure data consistency
                        connection.setAutoCommit(false);
                        
                        // Get basic transaction details from user
                        System.out.print("Enter store ID: ");
                        int storeId = Integer.parseInt(scanner.nextLine());
                        
                        System.out.print("Enter cashier ID: ");
                        int cashierId = Integer.parseInt(scanner.nextLine());
                        
                        System.out.print("Enter member ID (or leave blank if none): ");
                        String memberIdin = scanner.nextLine();
                        Integer memberId = memberIdin.isEmpty() ? null : Integer.parseInt(memberIdin);
                        
                        // Insert new transaction record
                        String insertTransaction = "INSERT INTO Transaction (transaction_id, store_id, member_id, cashier_id, purchase_date, total_amount) " +
                                                "VALUES (?, ?, ?, ?, NOW(), 0)";
                        
                        // Generate new transaction ID
                        pstmt = connection.prepareStatement("SELECT MAX(transaction_id) + 1 FROM Transaction");
                        rs = pstmt.executeQuery();
                        int transactionId = rs.next() ? rs.getInt(1) : 1;
                        
                        pstmt = connection.prepareStatement(insertTransaction);
                        pstmt.setInt(1, transactionId);
                        pstmt.setInt(2, storeId);
                        if (memberId == null) {
                            pstmt.setNull(3, Types.INTEGER);
                        } else {
                            pstmt.setInt(3, memberId);
                        }
                        pstmt.setInt(4, cashierId);
                        pstmt.executeUpdate();
                        
                        // Process transaction items
                        double totalAmount = 0.0;
                        boolean addMoreItems = true;
                        
                        while (addMoreItems) {
                            System.out.print("Enter product ID: ");
                            int productId = Integer.parseInt(scanner.nextLine());
                            
                            System.out.print("Enter quantity: ");
                            int quantity = Integer.parseInt(scanner.nextLine());
                            
                            // Retrieve product price and discount information
                            String productQuery = "SELECT p.market_price, " +
                                                "d.discount_percentage, " +
                                                "d.valid_from, " +
                                                "d.valid_to " +
                                                "FROM Product p " +
                                                "LEFT JOIN Discount d ON p.discount_id = d.discount_id " +
                                                "WHERE p.product_id = ?";
                            
                            pstmt = connection.prepareStatement(productQuery);
                            pstmt.setInt(1, productId);
                            rs = pstmt.executeQuery();
                            
                            if (!rs.next()) {
                                System.out.println("Product not found!");
                                continue;
                            }
                            
                            double unitPrice = rs.getDouble("market_price");
                            double discountPercentage = 0.0;
                            
                            // Check discount validity
                            if (rs.getObject("discount_percentage") != null) {
                                Date validFrom = rs.getDate("valid_from");
                                Date validTo = rs.getDate("valid_to");
                                Date currentDate = new Date(System.currentTimeMillis());
                                
                                if (validFrom != null && validTo != null && 
                                    currentDate.compareTo(validFrom) >= 0 && 
                                    currentDate.compareTo(validTo) <= 0) {
                                    discountPercentage = rs.getDouble("discount_percentage");
                                }
                            }
                            
                            // Calculate item totals with discounts
                            double subtotal = unitPrice * quantity;
                            double discountAmount = (subtotal * discountPercentage) / 100;
                            double itemTotal = subtotal - discountAmount;
                            totalAmount += itemTotal;
                            
                            // Insert transaction item
                            String insertItem = "INSERT INTO TransactionItem (transaction_id, product_id, quantity, unit_price, discount_amount) " +
                                             "VALUES (?, ?, ?, ?, ?)";
                            pstmt = connection.prepareStatement(insertItem);
                            pstmt.setInt(1, transactionId);
                            pstmt.setInt(2, productId);
                            pstmt.setInt(3, quantity);
                            pstmt.setDouble(4, unitPrice);
                            pstmt.setDouble(5, discountAmount);
                            pstmt.executeUpdate();
                            
                            // Update store inventory
                            String updateInventory = "UPDATE StoreInventory SET quantity = quantity - ?, last_updated = NOW() " +
                                                  "WHERE store_id = ? AND product_id = ?";
                            pstmt = connection.prepareStatement(updateInventory);
                            pstmt.setInt(1, quantity);
                            pstmt.setInt(2, storeId);
                            pstmt.setInt(3, productId);
                            pstmt.executeUpdate();
                            
                            System.out.print("Add another item? (yes/no): ");
                            addMoreItems = scanner.nextLine().trim().toLowerCase().startsWith("y");
                        }
                        
                        // Update transaction total amount
                        String updateTotal = "UPDATE Transaction SET total_amount = ? WHERE transaction_id = ?";
                        pstmt = connection.prepareStatement(updateTotal);
                        pstmt.setDouble(1, totalAmount);
                        pstmt.setInt(2, transactionId);
                        pstmt.executeUpdate();
                        
                        // Print transaction bill
                        System.out.println("\n=== Transaction Bill ===");
                        System.out.println("Transaction ID: " + transactionId);
                        System.out.println("Store ID: " + storeId);
                        System.out.println("Cashier ID: " + cashierId);
                        if (memberId != null) {
                            System.out.println("Member ID: " + memberId);
                        }
                        System.out.println("Date: " + java.time.LocalDateTime.now());
                        
                        String itemsQuery = "SELECT p.product_name, ti.quantity, ti.unit_price, ti.discount_amount " +
                                         "FROM TransactionItem ti JOIN Product p ON ti.product_id = p.product_id " +
                                         "WHERE ti.transaction_id = ?";
                        pstmt = connection.prepareStatement(itemsQuery);
                        pstmt.setInt(1, transactionId);
                        rs = pstmt.executeQuery();
                        
                        System.out.println("\nItems:");
                        System.out.println("Name\t\tQty\tUnit Price\tDiscount\tTotal");
                        while (rs.next()) {
                            String name = rs.getString("product_name");
                            int qty = rs.getInt("quantity");
                            double price = rs.getDouble("unit_price");
                            double discount = rs.getDouble("discount_amount");
                            double lineTotal = (price * qty) - discount;
                            
                            System.out.printf("%-15s %d\t%.2f\t\t%.2f\t\t%.2f%n", 
                                name, qty, price, discount, lineTotal);
                        }
                        
                        System.out.printf("\nTotal Amount: $%.2f%n", totalAmount);
                        
                        connection.commit();
                        
                    } catch (SQLException e) {
                        System.out.println("Error processing transaction: " + e.getMessage());
                        connection.rollback();
                    } finally {
                        connection.setAutoCommit(true);
                        if (rs != null) try { rs.close(); } catch (SQLException e) {}
                        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
                    }
                    break;
                    
                case 4: // Total Sales Report (by day, month, or year)
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    String startDate = getValidDateInput(scanner);
                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    String endDate = getValidDateInput(scanner);
                    System.out.print("Enter report type (1 for daily, 2 for monthly, 3 for yearly): ");
                    int reportType = Integer.parseInt(scanner.nextLine());

                    switch (reportType) {
                        case 1: // Daily sales report
                            query = "SELECT DATE(purchase_date) AS sale_date, SUM(total_amount) AS total_sales " +
                                    "FROM Transaction WHERE purchase_date BETWEEN ? AND ? " +
                                    "GROUP BY DATE(purchase_date) ORDER BY sale_date;";
                            break;
                        case 2: // Monthly sales report
                            query = "SELECT DATE_FORMAT(purchase_date, '%Y-%m') AS sale_month, SUM(total_amount) AS total_sales " +
                                    "FROM Transaction WHERE purchase_date BETWEEN ? AND ? " +
                                    "GROUP BY DATE_FORMAT(purchase_date, '%Y-%m') ORDER BY sale_month;";
                            break;
                        case 3: // Yearly sales report
                            query = "SELECT YEAR(purchase_date) AS sale_year, SUM(total_amount) AS total_sales " +
                                    "FROM Transaction WHERE purchase_date BETWEEN ? AND ? " +
                                    "GROUP BY YEAR(purchase_date) ORDER BY sale_year;";
                            break;
                    }
                    statement = connection.prepareStatement(query);
                    statement.setString(1, startDate);
                    statement.setString(2, endDate);
                    break;

                case 5: // Sales Growth Report for Specific Store
                    System.out.print("Enter store ID: ");
                    int storeId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    String growthStart = getValidDateInput(scanner);
                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    String growthEnd = getValidDateInput(scanner);
                    // Monthly sales report for a specific store
                    query = "SELECT DATE_FORMAT(purchase_date, '%Y-%m') AS sale_month, SUM(total_amount) AS monthly_sales " +
                            "FROM Transaction WHERE store_id = ? AND purchase_date BETWEEN ? AND ? " +
                            "GROUP BY DATE_FORMAT(purchase_date, '%Y-%m') ORDER BY sale_month;";
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, storeId);
                    statement.setString(2, growthStart);
                    statement.setString(3, growthEnd);
                    break;

                case 6: // Merchandise Stock Report
                    System.out.print("Enter report type (1 for all stores, 2 for specific store, 3 for specific product): ");
                    int stockType = Integer.parseInt(scanner.nextLine());
                    
                    switch (stockType) {
                        case 1: // Stock report across all stores
                            query = "SELECT s.store_id, s.address, p.product_id, p.product_name, SUM(si.quantity) AS stock_level " +
                                    "FROM Store s JOIN StoreInventory si ON s.store_id = si.store_id " +
                                    "JOIN Product p ON si.product_id = p.product_id " +
                                    "GROUP BY s.store_id, s.address, p.product_id, p.product_name " +
                                    "ORDER BY s.store_id, p.product_id;";
                            statement = connection.prepareStatement(query);
                            break;
                        case 2: // Stock report for specific store
                            System.out.print("Enter store ID: ");
                            int specificStoreId = Integer.parseInt(scanner.nextLine());
                            query = "SELECT p.product_id, p.product_name, si.quantity AS stock_level " +
                                    "FROM StoreInventory si JOIN Product p ON si.product_id = p.product_id " +
                                    "WHERE si.store_id = ? ORDER BY p.product_id;";
                            statement = connection.prepareStatement(query);
                            statement.setInt(1, specificStoreId);
                            break;
                        case 3: // Stock report for specific product across stores
                            System.out.print("Enter product ID: ");
                            int productId = Integer.parseInt(scanner.nextLine());
                            query = "SELECT s.store_id, s.address, si.quantity AS stock_level " +
                                    "FROM StoreInventory si JOIN Store s ON si.store_id = s.store_id " +
                                    "WHERE si.product_id = ? ORDER BY s.store_id;";
                            statement = connection.prepareStatement(query);
                            statement.setInt(1, productId);
                            break;
                    }
                    break;

                case 7: // Customer Growth Report (by month or year)
                    System.out.print("Enter start year (YYYY): ");
                    int startYear = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter end year (YYYY): ");
                    int endYear = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter report type (1 for monthly, 2 for yearly): ");
                    int growthType = Integer.parseInt(scanner.nextLine());
                    
                    if (growthType == 1) {
                        // Monthly customer growth report
                        query = "SELECT DATE_FORMAT(ms.signup_date, '%Y-%m') AS signup_month, COUNT(*) AS new_customers " +
                                "FROM MemberSignUp ms WHERE YEAR(ms.signup_date) BETWEEN ? AND ? " +
                                "GROUP BY DATE_FORMAT(ms.signup_date, '%Y-%m') ORDER BY signup_month;";
                    } else {
                        // Yearly customer growth report
                        query = "SELECT YEAR(ms.signup_date) AS signup_year, COUNT(*) AS new_customers " +
                                "FROM MemberSignUp ms WHERE YEAR(ms.signup_date) BETWEEN ? AND ? " +
                                "GROUP BY YEAR(ms.signup_date) ORDER BY signup_year;";
                    }
                    statement = connection.prepareStatement(query);
                    statement.setInt(1, startYear);
                    statement.setInt(2, endYear);
                    break;

                case 8: // Customer Activity Report
                    System.out.print("Enter start date (YYYY-MM-DD): ");
                    String activityStart = getValidDateInput(scanner);
                    System.out.print("Enter end date (YYYY-MM-DD): ");
                    String activityEnd = getValidDateInput(scanner);
                    System.out.print("Enter minimum purchase amount (optional, press Enter to skip): ");
                    String minAmount = scanner.nextLine();
                    
                    if (minAmount.isEmpty()) {
                        // Customer activity report without minimum purchase filter
                        query = "SELECT m.member_id, m.first_name, m.last_name, COALESCE(SUM(t.total_amount), 0) AS total_purchase_amount " +
                                "FROM Member m LEFT JOIN Transaction t ON m.member_id = t.member_id " +
                                "WHERE (t.purchase_date BETWEEN ? AND ? OR t.purchase_date IS NULL) " +
                                "GROUP BY m.member_id, m.first_name, m.last_name ORDER BY total_purchase_amount DESC;";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, activityStart);
                        statement.setString(2, activityEnd);
                    } else {
                        double minPurchaseAmount = Double.parseDouble(minAmount);
                        // Customer activity report with minimum purchase filter
                        query = "SELECT m.member_id, m.first_name, m.last_name, COALESCE(SUM(t.total_amount), 0) AS total_purchase_amount " +
                                "FROM Member m LEFT JOIN Transaction t ON m.member_id = t.member_id " +
                                "WHERE (t.purchase_date BETWEEN ? AND ? OR t.purchase_date IS NULL) " +
                                "GROUP BY m.member_id, m.first_name, m.last_name " +
                                "HAVING total_purchase_amount >= ? ORDER BY total_purchase_amount DESC;";
                        statement = connection.prepareStatement(query);
                        statement.setString(1, activityStart);
                        statement.setString(2, activityEnd);
                        statement.setDouble(3, minPurchaseAmount);
                    }
                    break;
            }
            
            if (statement != null) {
                executeAndPrint(statement);
                statement.close();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please try again.");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Manages CRUD operations (Create, Update, Delete) for specified entities.
     * 
     * @param connection Active database connection
     * @param scanner Scanner object for user input
     * @param entityName Name of the entity being managed
     * @param insertQuery SQL query for inserting new records
     * @param updateQuery SQL query for updating existing records
     * @param deleteQuery SQL query for deleting records
     * @param fields Array of field names for the entity
     * @throws SQLException if database operations fail
     */
    public static void manageEntity(Connection connection, Scanner scanner, String entityName, 
                                   String insertQuery, String updateQuery, String deleteQuery, 
                                   String[] fields) throws SQLException {
        System.out.println("\nManage " + entityName + " Information:");
        System.out.println("1. Add new " + entityName);
        System.out.println("2. Update existing " + entityName);
        System.out.println("3. Delete " + entityName);
        System.out.print("Choose action: ");
        
        int action = Integer.parseInt(scanner.nextLine());
        
        PreparedStatement stmt = null;
        switch (action) {
            case 1: // Insert new entity record
                stmt = connection.prepareStatement(insertQuery);
                for (int i = 0; i < fields.length; i++) {
                    System.out.print("Enter " + fields[i] + ": ");
                    String value = scanner.nextLine();
                    if (fields[i].contains("id") || fields[i].equals("age")) {
                        stmt.setInt(i + 1, Integer.parseInt(value));
                    } else if (fields[i].contains("date")) {
                        stmt.setDate(i + 1, java.sql.Date.valueOf(value));
                    } else {
                        stmt.setString(i + 1, value);
                    }
                }
                break;
                
            case 2: // Update existing entity record
                // First, fetch the current values
                System.out.print("Enter " + fields[0] + " to update: ");
                String idValue = scanner.nextLine();
                
                String selectQuery = "SELECT " + String.join(", ", fields) + " FROM " + entityName + " WHERE " + fields[0] + " = ?";
                PreparedStatement selectStmt = connection.prepareStatement(selectQuery);
                selectStmt.setInt(1, Integer.parseInt(idValue));
                ResultSet rs = selectStmt.executeQuery();
                
                if (!rs.next()) {
                    System.out.println("No record found with " + fields[0] + " = " + idValue);
                    rs.close();
                    selectStmt.close();
                    return;
                }
                
                // Store current values for comparison
                String[] currentValues = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    currentValues[i] = rs.getString(i + 1);
                }
                rs.close();
                selectStmt.close();
                
                stmt = connection.prepareStatement(updateQuery);
                System.out.println("Enter new values (press Enter to keep current value):");
                for (int i = 1; i < fields.length; i++) {
                    System.out.print("Enter new " + fields[i] + " (current: " + currentValues[i] + "): ");
                    String value = scanner.nextLine();
                    if (value.isEmpty()) {
                        value = currentValues[i];
                    }
                    if (fields[i].contains("id") || fields[i].equals("age")) {
                        stmt.setInt(i, Integer.parseInt(value));
                    } else if (fields[i].contains("date")) {
                        stmt.setDate(i, java.sql.Date.valueOf(value));
                    } else {
                        stmt.setString(i, value);
                    }
                }
                stmt.setInt(fields.length, Integer.parseInt(idValue));
                break;
                
            case 3: // Delete entity record
                stmt = connection.prepareStatement(deleteQuery);
                System.out.print("Enter " + fields[0] + " to delete: ");
                stmt.setInt(1, Integer.parseInt(scanner.nextLine()));
                break;
                
            default:
                System.out.println("Invalid action");
                return;
        }
        
        int rowsAffected = stmt.executeUpdate();
        System.out.println(rowsAffected + " row(s) affected");
        stmt.close();
    }

    /**
     * Manages inventory operations including adding new products, processing returns,
     * and transferring products between stores.
     * 
     * @param connection Active database connection
     * @param scanner Scanner object for user input
     * @throws SQLException if database operations fail
     */
    public static void manageInventory(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nManage Inventory:");
        System.out.println("1. Create inventory for new products");
        System.out.println("2. Update inventory with returns");
        System.out.println("3. Transfer products between stores");
        System.out.print("Choose action: ");
        
        int action = Integer.parseInt(scanner.nextLine());
        PreparedStatement stmt = null;
        
        switch (action) {
            case 1: // Create inventory for new products
                connection.setAutoCommit(false);
                try {
                    System.out.print("Enter store_id: ");
                    int storeId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter product_id: ");
                    int productId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter quantity received: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter supplier_id: ");
                    int supplierId = Integer.parseInt(scanner.nextLine());

                    // Get current supplier price
                    PreparedStatement priceStmt = connection.prepareStatement(
                        "SELECT buy_price FROM SupplierProduct " +
                        "WHERE supplier_id = ? AND product_id = ? AND is_current = 1");
                    priceStmt.setInt(1, supplierId);
                    priceStmt.setInt(2, productId);
                    ResultSet priceRs = priceStmt.executeQuery();
                    
                    double unitPrice;
                    if (priceRs.next()) {
                        unitPrice = priceRs.getDouble("buy_price");
                    } else {
                        throw new SQLException("No current price found for supplier_id " + supplierId + " and product_id " + productId);
                    }
                    priceRs.close();
                    priceStmt.close();

                    // Create purchase order
                    PreparedStatement poStmt = connection.prepareStatement(
                        "INSERT INTO PurchaseOrders (supplier_id, store_id, order_date, paid) " +
                        "VALUES (?, ?, NOW(), 0)",
                        Statement.RETURN_GENERATED_KEYS);
                    poStmt.setInt(1, supplierId);
                    poStmt.setInt(2, storeId);
                    poStmt.executeUpdate();

                    ResultSet rs = poStmt.getGeneratedKeys();
                    int orderId = rs.next() ? rs.getInt(1) : -1;
                    rs.close();
                    poStmt.close();

                    // Add purchase items
                    PreparedStatement piStmt = connection.prepareStatement(
                        "INSERT INTO PurchaseItem (order_id, product_id, quantity, price) " +
                        "VALUES (?, ?, ?, ?)");
                    piStmt.setInt(1, orderId);
                    piStmt.setInt(2, productId);
                    piStmt.setInt(3, quantity);
                    piStmt.setDouble(4, unitPrice);
                    piStmt.executeUpdate();
                    piStmt.close();

                    // Update or insert inventory
                    stmt = connection.prepareStatement(
                        "INSERT INTO StoreInventory (store_id, product_id, quantity, last_updated) " +
                        "VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity), last_updated = NOW()");
                    stmt.setInt(1, storeId);
                    stmt.setInt(2, productId);
                    stmt.setInt(3, quantity);
                    int rowsAffected = stmt.executeUpdate();

                    connection.commit();
                    System.out.println(rowsAffected + " row(s) affected");
                    System.out.println("Inventory created and purchase order recorded successfully");
                } catch (SQLException e) {
                    connection.rollback();
                    System.out.println("Error during inventory creation: " + e.getMessage());
                    throw e;
                } finally {
                    connection.setAutoCommit(true);
                    if (stmt != null) stmt.close();
                }
                break;
                
            case 2: // Update inventory with returns
                PreparedStatement inventoryStmt = connection.prepareStatement(
                    "UPDATE StoreInventory SET quantity = quantity + ?, last_updated = NOW() " +
                    "WHERE store_id = ? AND product_id = ?");
                
                PreparedStatement transactionItemStmt = connection.prepareStatement(
                    "UPDATE TransactionItem SET quantity = quantity - ?, " +
                    "discount_amount = discount_amount - (? * " +
                    "(SELECT market_price FROM Product WHERE product_id = ?) * 0) " +
                    "WHERE transaction_id = ? AND product_id = ?");
                
                PreparedStatement transactionStmt = connection.prepareStatement(
                    "UPDATE Transaction SET total_amount = total_amount - (? * " +
                    "(SELECT market_price FROM Product WHERE product_id = ?)), " +
                    "purchase_date = NOW() WHERE transaction_id = ?");

                System.out.print("Enter transaction_id: ");
                int transactionId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter store_id: ");
                int storeId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter product_id: ");
                int productId = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter quantity returned: ");
                int quantityReturned = Integer.parseInt(scanner.nextLine());

                try {
                    connection.setAutoCommit(false);

                    inventoryStmt.setInt(1, quantityReturned);
                    inventoryStmt.setInt(2, storeId);
                    inventoryStmt.setInt(3, productId);
                    int inventoryRows = inventoryStmt.executeUpdate();

                    transactionItemStmt.setInt(1, quantityReturned);
                    transactionItemStmt.setInt(2, quantityReturned);
                    transactionItemStmt.setInt(3, productId);
                    transactionItemStmt.setInt(4, transactionId);
                    transactionItemStmt.setInt(5, productId);
                    int itemRows = transactionItemStmt.executeUpdate();

                    transactionStmt.setInt(1, quantityReturned);
                    transactionStmt.setInt(2, productId);
                    transactionStmt.setInt(3, transactionId);
                    int transactionRows = transactionStmt.executeUpdate();

                    if (inventoryRows > 0 && itemRows > 0 && transactionRows > 0) {
                        connection.commit();
                        System.out.println("Return processed successfully: " + 
                            quantityReturned + " items returned to store " + storeId);
                    } else {
                        connection.rollback();
                        System.out.println("Failed to process return: No matching records found");
                    }
                } catch (SQLException e) {
                    try {
                        connection.rollback();
                        System.out.println("Error processing return: " + e.getMessage());
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    try {
                        connection.setAutoCommit(true);
                        inventoryStmt.close();
                        transactionItemStmt.close();
                        transactionStmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
                
            case 3: // Transfer products between stores
                connection.setAutoCommit(false);
                try {
                    stmt = connection.prepareStatement(
                        "UPDATE StoreInventory SET quantity = quantity - ?, last_updated = NOW() " +
                        "WHERE store_id = ? AND product_id = ? AND quantity >= ?");
                    System.out.print("Enter source store_id: ");
                    int sourceStore = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter destination store_id: ");
                    int destStore = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter product_id: ");
                    int transferProduct = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter quantity to transfer: ");
                    int transferQty = Integer.parseInt(scanner.nextLine());
                    
                    stmt.setInt(1, transferQty);
                    stmt.setInt(2, sourceStore);
                    stmt.setInt(3, transferProduct);
                    stmt.setInt(4, transferQty);
                    int rowsReduced = stmt.executeUpdate();
                    
                    if (rowsReduced == 0) {
                        throw new SQLException("Insufficient quantity in source store or store/product not found");
                    }
                    
                    stmt = connection.prepareStatement(
                        "INSERT INTO StoreInventory (store_id, product_id, quantity, last_updated) " +
                        "VALUES (?, ?, ?, NOW()) " +
                        "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity), last_updated = NOW()");
                    stmt.setInt(1, destStore);
                    stmt.setInt(2, transferProduct);
                    stmt.setInt(3, transferQty);
                    int rowsAdded = stmt.executeUpdate();
                    
                    connection.commit();
                    System.out.println(rowsReduced + " row(s) reduced, " + rowsAdded + " row(s) added");
                    System.out.println("Transfer completed successfully");
                } catch (SQLException e) {
                    connection.rollback();
                    System.out.println("Error during transfer: " + e.getMessage());
                    throw e;
                } finally {
                    connection.setAutoCommit(true);
                    if (stmt != null) stmt.close();
                }
                break;
                
            default:
                System.out.println("Invalid action");
                return;
        }
    }

    /**
     * Manages product promotions including adding discounts and assigning/removing them from products.
     * 
     * @param connection Active database connection
     * @param scanner Scanner object for user input
     * @throws SQLException if database operations fail
     */
    public static void managePromotions(Connection connection, Scanner scanner) throws SQLException {
        System.out.println("\nManage Product Promotions:");
        System.out.println("1. Add new Discount");
        System.out.println("2. Assign Discount to Product");
        System.out.println("3. Remove Discount from Product");
        System.out.print("Choose action: ");
        
        int action = Integer.parseInt(scanner.nextLine());
        PreparedStatement stmt = null;
        
        switch (action) {
            case 1: // Add new discount
                stmt = connection.prepareStatement(
                    "INSERT INTO Discount (discount_id, discount_percentage, max_discount_amount, valid_from, valid_to) VALUES (?, ?, ?, ?, ?)");
                System.out.print("Enter discount_id: ");
                stmt.setInt(1, Integer.parseInt(scanner.nextLine()));
                System.out.print("Enter discount_percentage: ");
                stmt.setDouble(2, Double.parseDouble(scanner.nextLine()));
                System.out.print("Enter max_discount_amount (or leave blank): ");
                String maxAmount = scanner.nextLine();
                stmt.setObject(3, maxAmount.isEmpty() ? null : Double.parseDouble(maxAmount));
                System.out.print("Enter valid_from (YYYY-MM-DD): ");
                stmt.setDate(4, java.sql.Date.valueOf(scanner.nextLine()));
                System.out.print("Enter valid_to (YYYY-MM-DD): ");
                stmt.setDate(5, java.sql.Date.valueOf(scanner.nextLine()));
                break;
                
            case 2: // Assign discount to product
                stmt = connection.prepareStatement(
                    "UPDATE Product SET discount_id = ? WHERE product_id = ?");
                System.out.print("Enter discount_id: ");
                stmt.setInt(1, Integer.parseInt(scanner.nextLine()));
                System.out.print("Enter product_id: ");
                stmt.setInt(2, Integer.parseInt(scanner.nextLine()));
                break;
                
            case 3: // Remove discount from product
                stmt = connection.prepareStatement(
                    "UPDATE Product SET discount_id = NULL WHERE product_id = ?");
                System.out.print("Enter product_id: ");
                stmt.setInt(1, Integer.parseInt(scanner.nextLine()));
                break;
                
            default:
                System.out.println("Invalid action");
                return;
        }
        
        int rowsAffected = stmt.executeUpdate();
        System.out.println(rowsAffected + " row(s) affected");
        stmt.close();
    }

    /**
     * Validates and returns a date string in YYYY-MM-DD format from user input.
     * 
     * @param scanner Scanner object for user input
     * @return Valid date string in YYYY-MM-DD format
     */
    public static String getValidDateInput(Scanner scanner) {
        while (true) {
            String dateStr = scanner.nextLine();
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate.parse(dateStr, formatter);
                return dateStr;
            } catch (DateTimeParseException e) {
                System.out.print("Invalid date format. Please enter date in YYYY-MM-DD format: ");
            }
        }
    }

    /**
     * Executes a prepared statement and prints the results in a formatted table.
     * 
     * @param statement PreparedStatement to execute
     * @throws SQLException if query execution fails
     */
    public static void executeAndPrint(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columns = metaData.getColumnCount();
            
            // Print column headers
            for (int i = 1; i <= columns; i++) {
                System.out.print(metaData.getColumnName(i) + " \t ");
            }
            System.out.println("\n" + "-".repeat(columns * 15));
            
            boolean hasResults = false;
            while (resultSet.next()) {
                hasResults = true;
                for (int i = 1; i <= columns; i++) {
                    System.out.print(resultSet.getString(i) + " \t ");
                }
                System.out.println();
            }
            
            if (!hasResults) {
                System.out.println("No results found for your query.");
            }
        }
    }
}