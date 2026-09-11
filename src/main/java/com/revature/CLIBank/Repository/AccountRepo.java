package com.revature.CLIBank.Repository;

import com.revature.CLIBank.Utility.ConnectionFactory;
import com.revature.CLIBank.model.*;
import java.time.LocalDateTime;
import java.util.*;

import java.sql.*;

public class AccountRepo {

    /**
     * Creates the owners table if it does not exists
     */
    private void createOwnersTable() {
        String query = """
            CREATE TABLE IF NOT EXISTS Owners (
                userID TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                age INTEGER NOT NULL
            ); """;

        try(
                Connection connection = ConnectionFactory.getAutoCommitConnect();
                Statement simpleStatement = connection.createStatement();
        ) {
            simpleStatement.execute(query);

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Create the accounts table if it does not exists
     */
    private void createAccountsTable() {

        String query = """
                CREATE TABLE IF NOT EXISTS Accounts (
                    accountID TEXT PRIMARY KEY,
                    userID TEXT NOT NULL,
                    passWord TEXT NOT NULL,
                    accountType TEXT NOT NULL,
                    balance REAL NOT NULL DEFAULT 0.0,
                    frozen INTEGER NOT NULL DEFAULT 0,
                    FOREIGN KEY (userID) REFERENCES Owners(userID)
                );
                """;
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            Statement simpleStatement = connection.createStatement();
        ) {
            simpleStatement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    /**
     * Create the transactions table if it does not exist
     */
    private void createTransactionsTable() {
        String query = """
                CREATE TABLE IF NOT EXISTS Transactions (
                transactionID TEXT PRIMARY KEY,
                sourceAccountId TEXT NOT NULL,
                destinationAccountId TEXT,
                type TEXT NOT NULL,
                amount REAL NOT NULL,
                timestamp TEXT NOT NULL,
                FOREIGN KEY (sourceAccountId) REFERENCES Accounts(accountID),
                FOREIGN KEY (destinationAccountId) REFERENCES Accounts(accountID)
            );
                """;

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            Statement simpleStatement = connection.createStatement();
        ) {
            simpleStatement.execute(query);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to initialize the schema and acreate the tables for the database
     */
    public void initSchema() {
        createOwnersTable();
        createAccountsTable();
        createTransactionsTable();
    }


    /**
     * Method designed to insert an User into the table
     * It will take an user object
     */
    public void insertUser(User user) {
        String query = "INSERT INTO Owners (userID, name, age) VALUES (?, ?, ?)";
        
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, user.getUserID().toString());
            ps.setString(2, user.getName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Method designed to insert an account into the table in the database
     * It will take an Accountinfo object
     */
    public void insertAccount(AccountInfo account) {
        String query = """
            INSERT INTO Accounts (accountID, userID, passWord, accountType, balance, frozen)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
            try (
                Connection connection = ConnectionFactory.getAutoCommitConnect();
                PreparedStatement ps = connection.prepareStatement(query);
            ) {
                ps.setString(1, account.getAccountID().toString());
                ps.setString(2, account.getUserID().toString());
                ps.setString(3, account.getPassWord());
                ps.setString(4, account.getAccountType().name());
                ps.setDouble(5, account.getBalance());
                ps.setInt(6, account.isFrozen()? 1 : 0);
                ps.executeUpdate();
    
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    /** 
     * Method designed to insert a transaction into the table
     * It will take a transaction object
     */
    public void insertTransaction(Transaction transaction) {
        String query = """
            INSERT INTO Transactions (transactionID, sourceAccountId, destinationAccountId, type, amount, timestamp)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, transaction.getTransactionId().toString());
            ps.setString(2, transaction.getSourceAccountId().toString());

            if (transaction.getDestinationAccountId() == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, transaction.getDestinationAccountId().toString());
            }

            ps.setString(4, transaction.getType().name());
            ps.setDouble(5, transaction.getAmount());
            ps.setString(6, transaction.getTimestamp().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to read from the ResultSet and create the user object
     * It will return an User object
     */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User (
            UUID.fromString(rs.getString("userID")),
            rs.getString("name"),
            rs.getInt("age")
        );
    }

    /**
     * Method to read from the ResultSet and create the AccountInfo object
     * It will return an AccountInfo object
     */
    private AccountInfo mapAccount(ResultSet rs) throws SQLException {
        String ownerId = rs.getString("userID");
        return new AccountInfo(
            UUID.fromString(rs.getString("accountID")),
            ownerId == null ? null : UUID.fromString(ownerId),
            rs.getString("passWord"),
            AccountType.valueOf(rs.getString("accountType")),
            rs.getDouble("balance"),
            rs.getInt("frozen") != 0
        );
    }

    /**
     * Method to read from the ResultSet and create the transaction object
     * It will return a transaction object
     */
    private Transaction mapTransaction(ResultSet rs) throws SQLException{
        return new Transaction(
            UUID.fromString(rs.getString("transactionID")),
            UUID.fromString(rs.getString("sourceAccountId")),
            rs.getString("destinationAccountId") == null ? null : UUID.fromString(rs.getString("destinationAccountId")),
            TransactionType.valueOf(rs.getString("type")),
            rs.getDouble("amount"),
            LocalDateTime.parse(rs.getString("timestamp"))
        );
    }


    /** 
     * Method to find an user by ID
     * will return Null if that record does not exist
     * Return an user object if it finds it
     */
    public User findUserById(UUID userID) {
        String query = "SELECT * FROM Owners WHERE userID = ?";

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, userID.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUser(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    
    /** 
     * Method to find an account by ID
     * Return null if the record does not exists
     * Return the AccountInfo object if it finds it
     */
    public AccountInfo findAccountById(UUID accountID) {
        String query = "SELECT * FROM Accounts WHERE accountID = ?";

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {

            ps.setString(1, accountID.toString());
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return mapAccount(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Method to find every account owned by a user
     * Return an empty list if the user has no accounts
     * Return a list of AccountInfo objects if it finds them
     */
    public List<AccountInfo> findAccountsByUserId(UUID userID) {
        String query = "SELECT * FROM Accounts WHERE userID = ?";
        List<AccountInfo> accounts = new ArrayList<>();

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, userID.toString());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                accounts.add(mapAccount(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return accounts;
    }


    /** 
     * Method to find the transactions by account ID
     * Return a list of transaction objects
     */
    public List<Transaction> findTransactionsByAccountId(UUID accountID) {
        String query = """
            SELECT * FROM Transactions
            WHERE sourceAccountId = ? OR destinationAccountId = ?
            ORDER BY timestamp DESC
        """;
        List<Transaction> transactions = new ArrayList<>();

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query);
        ) {
            ps.setString(1, accountID.toString());
            ps.setString(2, accountID.toString());
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                transactions.add(mapTransaction(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }


    /** 
     * Method to update an user in the database
     * It will take an User object
     */
    public void updateUser(User user) {
        String query = "UPDATE Owners SET name = ?, age = ? WHERE userID = ?";

        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query)
        ) {

            ps.setString(1, user.getName());
            ps.setInt(2, user.getAge());
            ps.setString(3, user.getUserID().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to update an account in the database
     * It will recieve an AccountInfo object
     */
    public void updateAccount(AccountInfo account) {
        String query = """
            UPDATE Accounts
            SET userID = ?, passWord = ?, accountType = ?, balance = ?, frozen = ?
            WHERE accountID = ? 
            """;
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            PreparedStatement ps = connection.prepareStatement(query)
        ) {
            ps.setString(1, account.getUserID().toString());
            ps.setString(2, account.getPassWord());
            ps.setString(3, account.getAccountType().name());
            ps.setDouble(4, account.getBalance());
            ps.setInt(5, account.isFrozen() ? 1 : 0);
            ps.setString(6, account.getAccountID().toString());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
