import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLDatabaseConnection {
    // Connect to your database.
    // Replace server name, username, and password with your credentials
    public static void main(String[] args) {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;"
                + "databaseName=kuliah;"
                + "integratedSecurity=true;"
                + "trustServerCertificate=true;";

        ResultSet resultSet = null;

        try (Connection connection = DriverManager.getConnection(connectionUrl);) {
            // Code here.
            System.out.println("connection good");
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            System.out.println("connection fail");
            e.printStackTrace();
        }
    }
}