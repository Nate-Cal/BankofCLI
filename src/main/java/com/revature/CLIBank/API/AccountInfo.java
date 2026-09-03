package com.revature.CLIBank.API;

public class AccountInfo {
    private String userName;
    private int pin;

    public boolean account(String userName, int pin) {
        this.userName = userName;
        this.pin = pin;
        return true;
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
