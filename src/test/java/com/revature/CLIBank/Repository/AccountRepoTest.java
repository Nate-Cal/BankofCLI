package com.revature.CLIBank.Repository;

import com.revature.CLIBank.Utility.ConnectionFactory;
import com.revature.CLIBank.model.*;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountRepoTest {

    private AccountRepo repo;

    @BeforeEach
    void setUp() {
        repo = new AccountRepo();
        repo.initSchema();
        wipeTables();
    }

    private void wipeTables() {
        try (
            Connection connection = ConnectionFactory.getAutoCommitConnect();
            Statement statement = connection.createStatement()
        ) {
            statement.execute("DELETE FROM Transactions");
            statement.execute("DELETE FROM Accounts");
            statement.execute("DELETE FROM Owners");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Test 
    void insertUserThenFindById() {
        User user = new User("TestUser", 21, "Pass123!");
        AccountRepo.insertUser(user);

        User found = repo.findUserById(user.getUserID());
        assertNotNull(found);
        assertEquals(user.getUserID(), found.getUserID());
        assertEquals("TestUser", found.getName());
        assertEquals(21, found.getAge());
        assertEquals("Pass123!", found.getPassword());
    }

    @Test 
    void findUserByIdReturnsNullWhenMissing() {
        assertNull(repo.findUserById(UUID.randomUUID()));
    }

    @Test 
    void findUserByNameAndPassword() {
        User user = new User("LoginUser", 30, "Sectret!");
        AccountRepo.insertUser(user);

        User found = repo.findUserByNameAndPassword("LoginUser", "Sectret!");
        assertNotNull(found);
        assertEquals(user.getUserID(), found.getUserID());
        assertNull(repo.findUserByNameAndPassword("LoginUser", "Wrong"));
    }

    @Test 
    void insertAccountThenFindById() {
        User user = new User("Owner", 30, "pass123!");
        AccountRepo.insertUser(user);

        AccountInfo account = new AccountInfo(user.getUserID(), 1234);
        AccountRepo.insertAccount(account);

        AccountInfo found = repo.findAccountById(account.getAccountID());
        assertNotNull(found);
        assertEquals(account.getAccountID(), found.getAccountID());
        assertEquals(user.getUserID(), found.getUserID());
        assertEquals(AccountType.CHECKING, found.getAccountType());
        assertEquals(0L, found.getBalance());
        assertFalse(found.isFrozen());
    }

    @Test 
    void findAccountsByUserIdReturnsThatUsersAccounts() {
        User user = new User("Owner2", 29, "Pass123!");
        AccountRepo.insertUser(user);

        AccountInfo checking = new AccountInfo(user.getUserID(), 1111);
        AccountInfo savings = new AccountInfo(user.getUserID(), 2222, AccountType.SAVINGS);
        AccountRepo.insertAccount(checking);
        AccountRepo.insertAccount(savings);

        List<AccountInfo> accounts = repo.findAccountsByUserId(user.getUserID());
        assertEquals(2, accounts.size());
    }

    @Test 
    void findAccountByIdReturnsNullWhenMissing() {
        assertNull(repo.findAccountById(UUID.randomUUID()));
    }

    @Test 
    void updateUserChangesDatabase() {
        User user = new User("Before", 24, "Old");
        AccountRepo.insertUser(user);

        user.setName("After");
        repo.updateUser(user);

        User found = repo.findUserById(user.getUserID());
        assertEquals("After", found.getName());
    }

    @Test 
    void updateAccountChangesBlanaceAndFrozen() {
        User user = new User("Owner3", 40, "Pass123!");
        AccountRepo.insertUser(user);

        AccountInfo account = new AccountInfo(user.getUserID(), 9999);
        AccountRepo.insertAccount(account);

        account.setBalance(25000L);
        account.setFrozen(true);
        repo.updateAccount(account);

        AccountInfo found = repo.findAccountById(account.getAccountID());
        assertEquals(25000L, found.getBalance());
        assertTrue(found.isFrozen());

    }

    // @Test 
    // void insertTransactionThenFindByAccountId() {
    //     User user = new User("Owner4", 22, "Pass123!");
    //     AccountRepo.insertUser(user);

    // }

}
