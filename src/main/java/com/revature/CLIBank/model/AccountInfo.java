package com.revature.CLIBank.model;

import java.util.UUID;

public class AccountInfo {
    private UUID accountID;
    private String userName;
    private String passWord;

    public void account(String userName, String passWord) {
        this.accountID = UUID.randomUUID();
        this.userName = userName;
        this.passWord = passWord;
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

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "AccountInfo{" +
                "accountID=" + accountID +
                ", userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
