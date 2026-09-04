package com.revature.CLIBank.BusinessLogic;

import java.util.UUID;

public class AccountInfo {
    private UUID accountID;
    private String userName;
    private int pin;



    public boolean account(String userName, int pin) {
        this.accountID = UUID.randomUUID();
        this.userName = userName;
        this.pin = pin;
        return true;
    }

    public UUID getAccountID() {
        return accountID;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }
}
