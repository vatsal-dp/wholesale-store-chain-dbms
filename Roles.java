import java.sql.*;
import java.util.Scanner;

public class Roles {
    static final String jdbcURL = "jdbc:mariadb://classdb2.csc.ncsu.edu:3306/dshah24";
    private static final String USER = <enter unity id>;
    private static final String PASSWORD = <enter id number or password>;
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(jdbcURL, USER, PASSWORD);
            System.out.println("Successfully connected to DB");

            System.out.println("Select your role:");
            System.out.println("1. Cashier");
            System.out.println("2. Warehouse Staff");
            System.out.println("3. Billing Staff");
            System.out.println("4. Store Manager");
            System.out.println("5. Registration Staff");
            System.out.print("Enter role number: ");
            int role = Integer.parseInt(scanner.nextLine());

            if (role < 1 || role > 5) {
                System.out.println("Invalid role. Exiting...");
                return;
            }

            while (true) {
                showMenu(role);
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == getExitOption(role)) {
                    System.out.println("Exiting...");
                    break;
                }
                executeRoleBasedQuery(role, choice, conn, scanner);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MariaDB JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("Database connection closed.");
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
            scanner.close();
        }
    }

    private static void showMenu(int role) {
        switch (role) {
            case 1: // Cashier
                System.out.println("\nCashier Menu:");
                System.out.println("1. Generate Bill");
                System.out.println("2. Exit");
                break;
            case 2: // Warehouse Staff
                System.out.println("\nWarehouse Staff Menu:");
                System.out.println("1. Create Inventory for New Products");
                System.out.println("2. Update Inventory with Returns");
                System.out.println("3. Transfer Products Between Stores");
                System.out.println("4. Exit");
                break;
            case 3: // Billing Staff
                System.out.println("\nBilling Staff Menu:");
                System.out.println("1. Supplier Unpaid Orders");
                System.out.println("2. Generate Bill");
                System.out.println("3. Total Sales Report");
                System.out.println("4. Exit");
                break;
            case 4: // Store Manager
                System.out.println("\nStore Manager Menu:");
                System.out.println("1. Supplier Unpaid Orders");           // From Billing Staff
                System.out.println("2. Platinum Reward Checks");           // Manager-specific
                System.out.println("3. Generate Bill");                    // From Cashier/Billing Staff
                System.out.println("4. Total Sales Report");               // From Billing Staff
                System.out.println("5. Sales Growth Report");  
                System.out.println("6. Merchandise Stock Report");// From Registration Staff
                System.out.println("7. Customer Growth Report"); 
                System.out.println("8. Customer Activity Report");// Manager-specific
                System.out.println("9. Manage inventory"); // From Warehouse Staff
//                System.out.println("8. Update Inventory with Returns");    // From Warehouse Staff
//                System.out.println("9. Transfer Products Between Stores"); // From Warehouse Staff
                System.out.println("10. Manage Promotions");               // Manager-specific
                System.out.println("11. Manage Store Information");        // Manager-specific
                System.out.println("12. Manage Supplier Information");     // Manager-specific
                System.out.println("13. Manage Customer Information");     // From Registration Staff
                System.out.println("14. Manage Staff Information");        // From Registration Staff
                System.out.println("15. Exit");
                break;
            case 5: // Registration Staff
                System.out.println("\nRegistration Staff Menu:");
                System.out.println("1. Customer Growth Report");
                System.out.println("2. Manage Customer Information");
                System.out.println("3. Manage Staff Information");
                System.out.println("4. Exit");
                break;
        }
        System.out.print("Enter your choice: ");
    }

    private static int getExitOption(int role) {
        switch (role) {
            case 1: return 2;  // Cashier
            case 2: return 4;  // Warehouse Staff
            case 3: return 4;  // Billing Staff
            case 4: return 15; // Store Manager (updated to 15 due to all options)
            case 5: return 4;  // Registration Staff
            default: return -1;
        }
    }

    private static void executeRoleBasedQuery(int role, int choice, Connection connection, Scanner scanner) {
        try {
            switch (role) {
                case 1: // Cashier
                    if (choice == 1) Query_helper.executeQuery(3, connection, scanner); // Bill Generated
                    else System.out.println("Invalid choice");
                    break;

                case 2: // Warehouse Staff
                    if (choice >= 1 && choice <= 3) {
                        Query_helper.manageInventory(connection, scanner); // Sub-menu handled in Query_helper
                    } else {
                        System.out.println("Invalid choice");
                    }
                    break;

                case 3: // Billing Staff
                    switch (choice) {
                        case 1: Query_helper.executeQuery(1, connection, scanner); break; // Supplier Unpaid Orders
                        case 2: Query_helper.executeQuery(3, connection, scanner); break; // Bill Generated
                        case 3: Query_helper.executeQuery(4, connection, scanner); break; // Total Sales Report
                        default: System.out.println("Invalid choice");
                    }
                    break;

                case 4: // Store Manager
                    switch (choice) {
                        case 1: Query_helper.executeQuery(1, connection, scanner); break; // Supplier Unpaid Orders
                        case 2: Query_helper.executeQuery(2, connection, scanner); break; // Platinum Reward Checks
                        case 3: Query_helper.executeQuery(3, connection, scanner); break; // Bill Generated
                        case 4: Query_helper.executeQuery(4, connection, scanner); break; // Total Sales Report
                        case 5: Query_helper.executeQuery(5, connection, scanner); break; // Sales Growth Report
                        case 6: Query_helper.executeQuery(6, connection, scanner); break; // Merchandise Stock Report
                        case 7: Query_helper.executeQuery(7, connection, scanner); break; // Customer Growth Report
                        case 8: Query_helper.executeQuery(8, connection, scanner); break; //Customer Activity Report
//                        case 7: // Create Inventory for New Products
//                        case 8: // Update Inventory with Returns
                        case 9: // Transfer Products Between Stores
                            Query_helper.manageInventory(connection, scanner); // Sub-menu handled in Query_helper
                            break;
                        case 10: Query_helper.managePromotions(connection, scanner); break; // Manage Promotions
                        case 11: // Manage Store Information
                            Query_helper.manageEntity(connection, scanner, "Store",
                                "INSERT INTO Store (store_id, address, phone) VALUES (?, ?, ?)",
                                "UPDATE Store SET address = ?, phone = ? WHERE store_id = ?",
                                "DELETE FROM Store WHERE store_id = ?",
                                new String[]{"store_id", "address", "phone"});
                            break;
                        case 12: // Manage Supplier Information
                            Query_helper.manageEntity(connection, scanner, "Supplier",
                                "INSERT INTO Supplier (supplier_id, supplier_name, supplier_contact, location, email) VALUES (?, ?, ?, ?, ?)",
                                "UPDATE Supplier SET supplier_name = ?, supplier_contact = ?, location = ?, email = ? WHERE supplier_id = ?",
                                "DELETE FROM Supplier WHERE supplier_id = ?",
                                new String[]{"supplier_id", "supplier_name", "supplier_contact", "location", "email"});
                            break;
                        case 13: // Manage Customer Information
                            Query_helper.manageEntity(connection, scanner, "Member",
                                "INSERT INTO Member (member_id, first_name, last_name, membership_level, email, phone, home_address, active_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                "UPDATE Member SET first_name = ?, last_name = ?, membership_level = ?, email = ?, phone = ?, home_address = ?, active_status = ? WHERE member_id = ?",
                                "DELETE FROM Member WHERE member_id = ?",
                                new String[]{"member_id", "first_name", "last_name", "membership_level", "email", "phone", "home_address", "active_status"});
                            break;
                        case 14: // Manage Staff Information
                            Query_helper.manageEntity(connection, scanner, "Staff",
                                "INSERT INTO Staff (staff_id, first_name, last_name, birth_date, employment_date, role_name, description, address, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                "UPDATE Staff SET first_name = ?, last_name = ?, birth_date = ?, employment_date = ?, role_name = ?, description = ?, address = ?, age = ? WHERE staff_id = ?",
                                "DELETE FROM Staff WHERE staff_id = ?",
                                new String[]{"staff_id", "first_name", "last_name", "birth_date", "employment_date", "role_name", "description", "address", "age"});
                            break;
                        default: System.out.println("Invalid choice");
                    }
                    break;

                case 5: // Registration Staff
                    switch (choice) {
                        case 1: Query_helper.executeQuery(5, connection, scanner); break; // Customer Growth Report
                        case 2: // Manage Customer Information
                            Query_helper.manageEntity(connection, scanner, "Member",
                                "INSERT INTO Member (member_id, first_name, last_name, membership_level, email, phone, home_address, active_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                "UPDATE Member SET first_name = ?, last_name = ?, membership_level = ?, email = ?, phone = ?, home_address = ?, active_status = ? WHERE member_id = ?",
                                "DELETE FROM Member WHERE member_id = ?",
                                new String[]{"member_id", "first_name", "last_name", "membership_level", "email", "phone", "home_address", "active_status"});
                            break;
                        case 3: // Manage Staff Information
                            Query_helper.manageEntity(connection, scanner, "Staff",
                                "INSERT INTO Staff (staff_id, first_name, last_name, birth_date, employment_date, role_name, description, address, age) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                                "UPDATE Staff SET first_name = ?, last_name = ?, birth_date = ?, employment_date = ?, role_name = ?, description = ?, address = ?, age = ? WHERE staff_id = ?",
                                "DELETE FROM Staff WHERE staff_id = ?",
                                new String[]{"staff_id", "first_name", "last_name", "birth_date", "employment_date", "role_name", "description", "address", "age"});
                            break;
                        default: System.out.println("Invalid choice");
                    }
                    break;

                default:
                    System.out.println("Invalid role");
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
    }
}