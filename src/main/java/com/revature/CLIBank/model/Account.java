package com.revature.CLIBank.model;

import java.util.UUID;

/**
 * Bank account belonging to an user
 * Holds the login PIN, current balance, account type, and frozen status.
 * Maps to the Accounts table.
 */
public class Account {
    private UUID accountID;
    private UUID userID;
    private AccountType accountType;
    private int pin;
    private double balance;
    private boolean frozen;

    /**
     * Creates a new account for an existing user.
     * Generates a unique account ID, starts the balance at 0.0 and leaves the account unfrozen.
     * @param userID the owner of this account
     * @param accountType checking or savings
     * @param pin PIN used to log in to this account
     */
    public Account(UUID userID, AccountType accountType, int pin) {
        this.accountID = UUID.randomUUID();
        this.userID = userID;
        this.accountType = accountType;
        this.pin = pin;
        this.balance = 0.0;
        this.frozen = false;
    }

    /**
     * Rebuilds an account from an existing database row
     * @param accountID the ID already stored in Accounts
     * @param userID the owner of this account
     * @param accountType checking or savings
     * @param pin PIN used to log in to this account
     * @param balance current balance from the database
     * @param frozen true if transactions are blocked on this account
     */
    public Account(UUID accountID, UUID userID, AccountType accountType, int pin, double balance, boolean frozen) {
        this.accountID = accountID;
        this.userID = userID;
        this.accountType = accountType;
        this.pin = pin;
        this.balance = balance;
        this.frozen = frozen;
    }

    public UUID getAccountID() {
        return accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }


    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType userName) {
        this.accountType = userName;
    }


    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }


    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }


    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

}
