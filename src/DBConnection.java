import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
//heelo
    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "bakery_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "devKen04202007";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
        + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: MySQL JDBC Driver not found.");
            System.out.println("Details: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("ERROR: Could not connect to the database.");
            System.out.println("Check your USERNAME, PASSWORD, and that MySQL is running.");
            System.out.println("Details: " + e.getMessage());
        }
        return connection;
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.out.println("WARNING: Failed to close the connection.");
                System.out.println("Details: " + e.getMessage());
            }
        }
    }

    public static void testConnection() {
        System.out.println("Testing database connection...");
        Connection connection = getConnection();
        if (connection != null) {
            System.out.println("SUCCESS: Connected to bakery_db.");
            closeConnection(connection);
        } else {
            System.out.println("FAILED: Could not connect to bakery_db.");
        }
    }
}