package orm;

import constants.DbConstants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Connector {
    private static Connection connection;

    public static void createConnection(String userName, String password, String dbName) throws SQLException {
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("user", userName);
        connectionProperties.setProperty("password", password);
        connection = DriverManager.getConnection(DbConstants.DB_BASE_URL + dbName, connectionProperties);
    }

    public static Connection getConnection() {
        return connection;
    }
}
