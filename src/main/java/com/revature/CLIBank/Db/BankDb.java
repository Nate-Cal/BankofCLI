package com.revature.CLIBank.Db;

import java.sql.*;
import java.time.format.DateTimeFormatter;

public class BankDb {
    private Connection con;

    /**
     * Utility method to create all tables in the database if they do not
     * already exist.
     * @author Nicholas DiGirolamo
     * @param conn, an open JDBC connection
     * @throws SQLException
     */
    private void creatTables(Connection conn) throws SQLException {
        if(conn == null || conn.isClosed()) return;

        Statement stmt = conn.createStatement();

        stmt.executeQuery("""
        CREATE TABLE Owners(id PRIMARY KEY, name TEXT) IF NOT EXISTS;
        """);

        stmt.executeQuery("""
        CREATE TABLE Accounts (uuid VARCHAR(12),
        owner REFERENCES Owners(id),
        opened TEXT,
        type FOREIGN KEY,
        balance TEXT DEFAULT '0' NOT NULL,
        frozen INTEGER DEFAULT FALSE) -- True: is frozen
        IF NOT EXISTS;
        """);

        stmt.executeQuery("""
        CREATE TABLE Transactions 
        (source REFERENCES Accounts(uuid), 
        dest REFERENCES Accounts(uuid), 
        amount TEXT, moment DATE) 
        IF NOT EXISTS;
        """);

        stmt.executeQuery("""
        CREATE TABLE AcctTypes (id PRIMARY KEY,
        checkSave BOOLEAN, name TEXT,
        transactionLimit INTEGER,
        penalty DECIMAL(10, 5),
        rules FOREIGN KEY)
        IF NOT EXISTS;
        """);
    }

    /**
     * Return all transactions for an account
     * @author Nicholas DiGirolamo
     * @param user, a bank account number
     * @return String, all transactions for the account
     */
    public String getTransactions(String user) {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT * FROM Transactions WHERE uuid = ? SORT BY moment;
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            while(rs.next()) {
                output.append(rs.getString("dest"));
                output.append(" ");
                output.append(rs.getString("amount"));
                output.append(" ");
                output.append(rs.getString("moment"));
                output.append("\n");
            }
            rs.close();
            return output.toString();
        } catch (SQLException e) {
            // pass
        }
        return "";
    }

    /**
     * Return the n most recent transactions
     * @author Nicholas DiGirolamo
     * @param n, the count of rows to be retrieved
     * @return String, n rows, of the n most recent transactions
     */
    public String getRecentTransactions(String user, int n) {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT TOP( ? ) * FROM Transactions WHERE uuid = ? SORT BY moment;
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setInt(1, n);
            stmt.setString(2, user);
            rs = stmt.executeQuery();
            while(rs.next()) {
                output.append(rs.getString("dest"));
                output.append(" ");
                output.append(rs.getString("amount"));
                output.append(" ");
                output.append(rs.getString("moment"));
                output.append("\n");
            }
            rs.close();
            return output.toString();
        } catch (SQLException e) {
            // pass
        }
        return "";
    }

    public String getBalance(String acct) {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT (balance) FROM Accounts WHERE uuid = ? ;
                """;
        try (PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setString(1, acct);
            rs = stmt.executeQuery();
            return rs.getString("balance");
        } catch (SQLException e) {
            // pass
        }
        return "";
    }

    /**
     * Add or remove a specified amount from a bank account.
     * Transfers are merely two of these.
     *
     * A cash deposit would be NULL src, non-NULL dest.
     * A cash withdrawal would be a non-NULL src, NULL dest.
     * A transfer would have both the src and dest be non-NULL. The
     * transaction would appear the same in both users' records.
     * A positive amount means they are the recipient; a negative
     * amount means they are the sender.
     *
     * @author Nicholas DiGirolamo
     * @param src, a String of the source bank account
     * @param dest, a String of the recipient bank account
     * @param amount, a String denoting a dollar amount. No negatives. This
     *                should be the <i>final</i> balance following the transaction.
     * @return The success of the change in balance.
     */
    public boolean postTransaction(String src, String dest, String amount) {
        ResultSet rs;
        String sql = """
                UPDATE Accounts SET balance = ? WHERE uuid = ? ;
                """;
        String transactSql = """
                INSERT INTO TRANSACTIONS Values ( ? , ?,  ? , ? );
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setString(1, src);
            stmt.setString(2, dest);
            stmt.setString(3, amount);
            stmt.executeQuery();
        } catch(SQLException e) {
            return false;
        }

        try(PreparedStatement stmt = this.con.prepareStatement(transactSql)) {
            stmt.setString(1, src);
            stmt.setString(2, dest);
            stmt.setString(3, amount);
            stmt.setString(4, DateTimeFormatter.ISO_INSTANT.toString());
            stmt.executeQuery();
        } catch(SQLException e) {
            // pass
            return false;
        }

        return true;
    }

    /**
     * Initialize the database during program initialization.
     */
    BankDb() {
        String url = "jdbc:sqlite:db/bankdb.db3";
        try(Connection tmpCon = DriverManager.getConnection(url)) {
            this.con = tmpCon;
            creatTables(this.con);
        } catch (SQLException e) {
            // Pass
        }
    }
}
