package com.revature.CLIBank.Utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String url = System.getenv("DATABASE-PATH");


    public static Connection getAutoCommitConnect() throws SQLException {
        Connection connection = DriverManager.getConnection(url);
        return connection;
    }

}
