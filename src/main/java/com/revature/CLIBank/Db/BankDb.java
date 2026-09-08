package com.revature.CLIBank.Db;

import java.sql.*;
import java.util.UUID;

public class BankDb {
    private Connection con;

    private void creatTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.executeQuery("""
        CREATE TABLE Owners(id PRIMARY KEY, name TEXT);
        """);

        stmt.executeQuery("""
        CREATE TABLE Accounts (uuid PRIMARY KEY, owner FOREIGN KEY REFERENCES Owners(id), opened DATE, type FOREIGN KEY, balance DECIMAL(10, 5) >= 0 DEFAULT 0, frozen BOOLEAN DEFAULT FALSE);
        """);

        stmt.executeQuery("""
        CREATE TABLE Transactions (source FOREIGN KEY REFERENCES Accounts(uuid), dest FOREIGN KEY REFERENCES Accounts(uuid), amount DECIMAL(10, 5), moment DATE);
        """);

        stmt.executeQuery("""
        CREATE TABLE AcctTypes (id PRIMARY KEY, checkSave BOOLEAN, name TEXT, transactionlimit INTEGER, penalty DECIMAL(10, 5), rules FOREIGN KEY);
        """);

    }

    /**
     * Return all transactions for an account
     * @author Nicholas DiGirolamo
     * @return String, all transactions for the account
     */
    public String getTransactions() {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT * FROM Transactions WHERE uuid = ? SORT BY moment;
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setInt(1, 0); /* UUID, needs to be replaced */
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
     * @param n, the count of
     * @return String, n rows, of the n most recent transactions
     */
    public String getTransactions(UUID user, int n) {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT TOP(?) * FROM Transactions WHERE uuid = ? SORT BY moment;
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setInt(1, n);
            stmt.setInt(2, 0); /* UUID, needs to be replaced */
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

    public String checkBalance(UUID acct) {
        ResultSet rs;
        StringBuilder output = new StringBuilder();
        String sql = """
                SELECT (balance) FROM Accounts WHERE uuid = ? ;
                """;
        try (PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setInt(acct, 1);
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
     * @param acct
     * @param amount
     * @param sign, TRUE if deposit, FALSE if withdraw
     * @return
     */
    public boolean changeAmt(UUID acct, String amount, boolean sign) {
        ResultSet rs;
        String sql = """
                UPDATE Accounts SET balance = ? WHERE uuid = ? ;
                """;
        try(PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setString(acct, 1);
            return true;
        } catch(SQLException e) {
            return false;
        }
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
