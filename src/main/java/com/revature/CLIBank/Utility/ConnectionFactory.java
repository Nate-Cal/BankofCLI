package com.revature.CLIBank.Utility;

// import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionFactory {

    private static final String url = System.getenv("DATABASE-PATH");


    public static Connection getAutoCommitConnect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        return connection;
    }

    /**
     * Manual connection of the database
     */
    public static Connection getManualCommitConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        configureForeignKeyEnforcement(connection);
        connection.setAutoCommit(false);
        return connection;
    }


    public static void configureForeignKeyEnforcement(Connection connection) throws SQLException {
        try(Statement statement = connection.createStatement()){
            String sql = "PRAGMA foreign_keys = true";
            statement.execute(sql);
        }
    }

}
