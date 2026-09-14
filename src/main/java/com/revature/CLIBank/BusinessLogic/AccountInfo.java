package com.revature.CLIBank.BusinessLogic;

import java.util.UUID;

/**
 *
 */
public class AccountInfo {
    private UUID accountID;
    private String userName;
    private String passWord;
    //private boolean status = true; //-Mo
    //private long balance ;

    //private String accountName; //-Mo
    //TODO: add an attribute for Account Names (e.g checking and savings) - Mo


    //TODO: add an attribute for Account Names (e.g checking and savings) - Mo

   /*
   public String getAccountName() {
        return accountName;
    }

    //TODO: write getAccountName() logic - Mo

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    //TODO: write setAccountName() logic - Mo





    private String accountName; //Mo - should be added


    //Mo
    public boolean isStatus() {
        return status;
    }
    //TODO: add a "status" field to the table

   //Mo
    public void setStatus(boolean status) {
        this.status = status;
    }
    //TODO: set if the account is active/inactive from repository layer

    */




    public void account(String userName, String passWord) {
        this.accountID = UUID.randomUUID();
        this.userName = userName;
        this.passWord = passWord;
    }

    /*
    //Mo, dummy method
    public long getBalance(){
        return balance;
    }

    //Mo, dummy method
    public void setBalance(long balance){
        this.balance = balance;

    }
    */


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

