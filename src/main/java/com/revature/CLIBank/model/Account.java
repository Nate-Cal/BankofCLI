package com.revature.CLIBank.model;

import java.util.UUID;

public class Account {
    private UUID accountID;
    private UUID userID;
    private AccountType accountType;
    private int pin;
    private double balance;

    public Account(UUID userID, AccountType accountType, int pin) {
        this.accountID = UUID.randomUUID();
        this.userID = userID;
        this.accountType = accountType;
        this.pin = pin;
        this.balance = 0.0;
    }

    public Account(UUID accountID, UUID userID, AccountType accountType, int pin, double balance) {
        this.accountID = accountID;
        this.userID = userID;
        this.accountType = accountType;
        this.pin = pin;
        this.balance = balance;
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



}
