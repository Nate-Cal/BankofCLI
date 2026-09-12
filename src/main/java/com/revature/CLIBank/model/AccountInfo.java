package com.revature.CLIBank.model;

import java.util.UUID;

public class AccountInfo {
    private UUID accountID;
    private UUID userID;
    private int pin;
    private AccountType accountType;
    private double balance;
    private boolean frozen;

    public AccountInfo() {}


    /** 
     * Constructor to initialize an account
     */
    public AccountInfo(UUID userID, int pin) {
        this.accountID = UUID.randomUUID();
        this.userID = userID;
        this.pin = pin;
        this.accountType = AccountType.CHECKING;
        this.balance = 0.0;
        this.frozen = false;
    }

    /** 
     * Constructor to initialize an account with a custom accountType
     */
    public AccountInfo(UUID userID, int pin, AccountType accountType) {
        this.accountID = UUID.randomUUID();
        this.userID = userID;
        this.pin = pin;
        this.accountType = accountType;
        this.balance = 0.0;
        this.frozen = false;
    }


    /** 
     * Constructor to retrieve an account from the database
     */
    public AccountInfo(UUID accountID, UUID userID, int pin, AccountType accountType, double balance, boolean frozen) {
        this.accountID = accountID;
        this.userID = userID;
        this.pin = pin;
        this.accountType = accountType;
        this.balance = balance;
        this.frozen = frozen;
    }

    public UUID getAccountID() {
        return accountID;
    }

    public void setAccountID(UUID accountID) {
        this.accountID = accountID;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    @Override
    public String toString() {
        return "AccountInfo [accountID=" + accountID + ", userID=" + userID + ", pin=" + pin + ", accountType=" + accountType + ", balance=" + balance + ", frozen=" + frozen + "]";
    }

}
