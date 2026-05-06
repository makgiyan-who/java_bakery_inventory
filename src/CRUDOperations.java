// ============================================================
// CRUDOperations.java
// Purpose : All database CRUD operations for the Bakery System
// Author  : Ken
// ============================================================

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CRUDOperations {

    // ==========================================================
    // UTILITY: printDivider()
    // Purpose : Prints a separator line for cleaner console output
    // ==========================================================
    private static void printDivider() {
        System.out.println("  " + "-".repeat(60));
    }


    // ==========================================================
    // UTILITY: viewAllCategories()
    // Purpose : Displays available categories so user can pick
    //           a valid category_id during insert and update
    // ==========================================================
    public static void viewAllCategories() {

        String sql = "SELECT id, name FROM categories ORDER BY id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Cannot fetch categories. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            System.out.println("\n  Available Categories:");
            printDivider();
            System.out.printf("  %-5s %-20s%n", "ID", "Category Name");
            printDivider();

            boolean hasData = false;

            while (resultSet.next()) {
                hasData = true;
                int id       = resultSet.getInt("id");
                String name  = resultSet.getString("name");
                System.out.printf("  %-5d %-20s%n", id, name);
            }

            if (!hasData) {
                System.out.println("  No categories found.");
            }

            printDivider();

        } catch (SQLException e) {
            System.out.println("  [!] Error fetching categories: " + e.getMessage());

        } finally {
            // Always close resources to prevent memory leaks
            closeResources(resultSet, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 1: insertProduct()   → CREATE
    // Purpose : Adds a new product into the products table
    // Called  : From Main.java menu option 1
    // ==========================================================
    public static void insertProduct() {

        System.out.println("\n  === ADD NEW PRODUCT ===");

        // Show categories first so user knows valid IDs
        viewAllCategories();

        // Collect input through InputHelper (Giane's file)
        String name       = InputHelper.getString(
                                "  Product name       : ", 150);
        int categoryId    = InputHelper.getInt(
                                "  Category ID        : ", 1, 100);
        double price      = InputHelper.getDouble(
                                "  Price (e.g. 25.50) : ", 0.01);
        int stock         = InputHelper.getPositiveInt(
                                "  Stock quantity     : ");

        // SQL with placeholders — no string concatenation
        String sql = "INSERT INTO products (name, category_id, price, stock) "
                   + "VALUES (?, ?, ?, ?)";

        Connection connection       = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Insert failed. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);

            // Bind each ? placeholder to actual values
            statement.setString(1, name);
            statement.setInt(2, categoryId);
            statement.setDouble(3, price);
            statement.setInt(4, stock);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\n  [✓] Product \"" + name + "\" added successfully!");
            } else {
                System.out.println("\n  [!] Insert failed. No rows affected.");
            }

        } catch (SQLException e) {
            // FK violation = invalid category_id
            if (e.getErrorCode() == 1452) {
                System.out.println("\n  [!] Invalid Category ID. Please choose from the list.");
            } else {
                System.out.println("\n  [!] Error inserting product: " + e.getMessage());
            }

        } finally {
            closeResources(null, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 2: viewAllProducts()   → READ (all records)
    // Purpose : Displays all products with category name
    // Called  : From Main.java menu option 2
    // ==========================================================
    public static void viewAllProducts() {

        System.out.println("\n  === ALL PRODUCTS ===");

        // JOIN to show category name instead of just the ID
        String sql = "SELECT p.id, p.name, c.name AS category, "
                   + "p.price, p.stock "
                   + "FROM products p "
                   + "JOIN categories c ON p.category_id = c.id "
                   + "ORDER BY p.id";

        Connection connection       = null;
        PreparedStatement statement = null;
        ResultSet resultSet         = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Cannot fetch products. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            printDivider();
            System.out.printf(
                "  %-5s %-25s %-12s %-10s %-8s%n",
                "ID", "Product Name", "Category", "Price", "Stock"
            );
            printDivider();

            boolean hasData = false;
            int totalProducts = 0;

            while (resultSet.next()) {
                hasData = true;
                totalProducts++;

                int id         = resultSet.getInt("id");
                String name    = resultSet.getString("name");
                String category= resultSet.getString("category");
                double price   = resultSet.getDouble("price");
                int stock      = resultSet.getInt("stock");

                System.out.printf(
                    "  %-5d %-25s %-12s %-10.2f %-8d%n",
                    id, name, category, price, stock
                );
            }

            if (!hasData) {
                System.out.println("  No products found in the database.");
            } else {
                printDivider();
                System.out.println("  Total products: " + totalProducts);
            }

            printDivider();

        } catch (SQLException e) {
            System.out.println("  [!] Error fetching products: " + e.getMessage());

        } finally {
            closeResources(resultSet, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 3: searchProductById()   → READ (by ID)
    // Purpose : Finds and displays a single product by its ID
    // Called  : From Main.java menu option 3
    // ==========================================================
    public static void searchProductById() {

        System.out.println("\n  === SEARCH PRODUCT BY ID ===");

        int id = InputHelper.getID("  Enter Product ID: ");

        String sql = "SELECT p.id, p.name, c.name AS category, "
                   + "p.price, p.stock "
                   + "FROM products p "
                   + "JOIN categories c ON p.category_id = c.id "
                   + "WHERE p.id = ?";

        Connection connection       = null;
        PreparedStatement statement = null;
        ResultSet resultSet         = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Cannot search. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // bind the ID placeholder

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Product found — display all its details
                printDivider();
                System.out.println("  Product Found:");
                printDivider();
                System.out.println("  ID       : " + resultSet.getInt("id"));
                System.out.println("  Name     : " + resultSet.getString("name"));
                System.out.println("  Category : " + resultSet.getString("category"));
                System.out.printf( "  Price    : %.2f%n", resultSet.getDouble("price"));
                System.out.println("  Stock    : " + resultSet.getInt("stock"));
                printDivider();
            } else {
                System.out.println("\n  [!] No product found with ID: " + id);
            }

        } catch (SQLException e) {
            System.out.println("  [!] Error searching product: " + e.getMessage());

        } finally {
            closeResources(resultSet, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 4: updateProduct()   → UPDATE
    // Purpose : Modifies an existing product's details
    // Called  : From Main.java menu option 4
    // ==========================================================
    public static void updateProduct() {

        System.out.println("\n  === UPDATE PRODUCT ===");

        // Show current products so user knows which ID to pick
        viewAllProducts();

        int id = InputHelper.getID("  Enter Product ID to update: ");

        // First verify the product actually exists
        if (!productExists(id)) {
            System.out.println("\n  [!] Product with ID " + id + " does not exist.");
            return;
        }

        // Show categories for reference
        viewAllCategories();

        // Collect new values from user
        String newName     = InputHelper.getString(
                                 "  New product name       : ", 150);
        int newCategoryId  = InputHelper.getInt(
                                 "  New Category ID        : ", 1, 100);
        double newPrice    = InputHelper.getDouble(
                                 "  New price              : ", 0.01);
        int newStock       = InputHelper.getInt(
                                 "  New stock quantity     : ", 0,
                                 Integer.MAX_VALUE);

        String sql = "UPDATE products "
                   + "SET name = ?, category_id = ?, price = ?, stock = ? "
                   + "WHERE id = ?";

        Connection connection       = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Update failed. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);

            // Bind all placeholders in order
            statement.setString(1, newName);
            statement.setInt(2, newCategoryId);
            statement.setDouble(3, newPrice);
            statement.setInt(4, newStock);
            statement.setInt(5, id);  // WHERE id = ?

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\n  [✓] Product ID " + id + " updated successfully!");
            } else {
                System.out.println("\n  [!] Update failed. Product may not exist.");
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1452) {
                System.out.println("\n  [!] Invalid Category ID. Please choose from the list.");
            } else {
                System.out.println("\n  [!] Error updating product: " + e.getMessage());
            }

        } finally {
            closeResources(null, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 5: deleteProduct()   → DELETE
    // Purpose : Removes a product from the database permanently
    // Called  : From Main.java menu option 5
    // ==========================================================
    public static void deleteProduct() {

        System.out.println("\n  === DELETE PRODUCT ===");

        // Show all products so user can pick the right ID
        viewAllProducts();

        int id = InputHelper.getID("  Enter Product ID to delete: ");

        // Check existence before attempting delete
        if (!productExists(id)) {
            System.out.println("\n  [!] Product with ID " + id + " does not exist.");
            return;
        }

        // Safety confirmation before irreversible action
        boolean confirmed = InputHelper.confirm(
            "Are you sure you want to delete Product ID " + id + "?"
        );

        if (!confirmed) {
            System.out.println("\n  [✓] Delete cancelled.");
            return;
        }

        String sql = "DELETE FROM products WHERE id = ?";

        Connection connection       = null;
        PreparedStatement statement = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Delete failed. No DB connection.");
                return;
            }

            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("\n  [✓] Product ID " + id + " deleted successfully.");
            } else {
                System.out.println("\n  [!] Delete failed. Product may not exist.");
            }

        } catch (SQLException e) {
            System.out.println("  [!] Error deleting product: " + e.getMessage());

        } finally {
            closeResources(null, statement, connection);
        }
    }


    // ==========================================================
    // OPERATION 6: restockProduct()   → TRANSACTION
    // Purpose : Adds stock to a product AND logs the restock
    //           Uses commit/rollback — both must succeed
    //           or neither change is saved
    // Called  : From Main.java menu option 6
    // ==========================================================
    public static void restockProduct() {

        System.out.println("\n  === RESTOCK PRODUCT (Transaction) ===");

        // Show all products for reference
        viewAllProducts();

        int id       = InputHelper.getID("  Enter Product ID to restock: ");
        int quantity = InputHelper.getPositiveInt("  Enter quantity to add   : ");

        // SQL 1: add stock to the product
        String updateSQL = "UPDATE products SET stock = stock + ? WHERE id = ?";

        // SQL 2: log the restock event
        // Note: restock_log table is created in the schema
        String logSQL    = "INSERT INTO restock_log "
                         + "(product_id, quantity_added, restock_date) "
                         + "VALUES (?, ?, NOW())";

        Connection connection              = null;
        PreparedStatement updateStatement  = null;
        PreparedStatement logStatement     = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) {
                System.out.println("  [!] Restock failed. No DB connection.");
                return;
            }

            // Check product exists before starting transaction
            if (!productExists(id)) {
                System.out.println("\n  [!] Product with ID " + id + " does not exist.");
                return;
            }

            // ── TRANSACTION START ──────────────────────────────
            // Turn off auto-commit so we control when to save
            connection.setAutoCommit(false);

            // Execute SQL 1: update stock
            updateStatement = connection.prepareStatement(updateSQL);
            updateStatement.setInt(1, quantity);
            updateStatement.setInt(2, id);
            int updateRows = updateStatement.executeUpdate();

            // Execute SQL 2: insert log record
            logStatement = connection.prepareStatement(logSQL);
            logStatement.setInt(1, id);
            logStatement.setInt(2, quantity);
            int logRows = logStatement.executeUpdate();

            // Only commit if BOTH statements succeeded
            if (updateRows > 0 && logRows > 0) {
                connection.commit(); // ← SAVE both changes
                System.out.println(
                    "\n  [✓] Restock successful! Added " + quantity
                    + " units to Product ID " + id + "."
                );
                System.out.println("  [✓] Restock log recorded.");
            } else {
                connection.rollback(); // ← UNDO everything
                System.out.println("\n  [!] Restock failed. Changes rolled back.");
            }
            // ── TRANSACTION END ────────────────────────────────

        } catch (SQLException e) {
            // Something crashed mid-transaction — rollback to be safe
            System.out.println("\n  [!] Transaction error: " + e.getMessage());

            try {
                if (connection != null) {
                    connection.rollback(); // ← UNDO on exception
                    System.out.println("  [!] All changes have been rolled back.");
                }
            } catch (SQLException rollbackEx) {
                System.out.println("  [!] Rollback also failed: " + rollbackEx.getMessage());
            }

        } finally {
            // Restore auto-commit and close all resources
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // ← restore default
                }
            } catch (SQLException e) {
                System.out.println("  [!] Could not restore auto-commit: " + e.getMessage());
            }

            closeResources(null, updateStatement, null);
            closeResources(null, logStatement, connection);
        }
    }


    // ==========================================================
    // UTILITY: productExists()
    // Purpose : Checks if a product with the given ID exists
    //           Prevents update/delete on non-existent records
    // Returns : true if found, false if not
    // ==========================================================
    private static boolean productExists(int id) {

        String sql = "SELECT id FROM products WHERE id = ?";

        Connection connection       = null;
        PreparedStatement statement = null;
        ResultSet resultSet         = null;

        try {
            connection = DBConnection.getConnection();

            if (connection == null) return false;

            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            return resultSet.next(); // true if a row was found

        } catch (SQLException e) {
            System.out.println("  [!] Error checking product: " + e.getMessage());
            return false;

        } finally {
            closeResources(resultSet, statement, connection);
        }
    }


    // ==========================================================
    // UTILITY: closeResources()
    // Purpose : Closes ResultSet, PreparedStatement, Connection
    //           Called in every finally block
    //           Passing null for any param safely skips it
    // ==========================================================
    private static void closeResources(
            ResultSet rs,
            PreparedStatement ps,
            Connection conn) {

        try {
            if (rs   != null) rs.close();
            if (ps   != null) ps.close();
            if (conn != null) DBConnection.closeConnection(conn);
        } catch (SQLException e) {
            System.out.println("  [!] Error closing resources: " + e.getMessage());
        }
    }
}