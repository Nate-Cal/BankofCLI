package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;

public class API {

    private User user;
    String result;

    public API() {
        this.user = null;
        this.result = null;
    }

    public boolean register(String username, String password) {
        boolean unameStat = AccountValidation.isUserNameValid(username);
        boolean pwStat = AccountValidation.isPassWordValid(password);

        if(unameStat && pwStat) {
            User tmpUser = new User(username, 0, password);
            AccountRepo.insertUser(tmpUser);
        } else {
            this.result =
             """
             Username must include 1 Uppercase, 1 Lowercase, and must be between 8 and 16 characters.
             Password must include 1 Uppercase, 1 Lowercase, 1 number, 1 special character, and be 8-16 characters.
             """;
        }

        return unameStat && pwStat;
    }

    public boolean login(String username, String password) {
        User stagedUser = new User(username, password);

        if(stagedUser.exists) {
            this.user = stagedUser;
        } else {
            this.result = "Login failed. Try again.";
        }

        return stagedUser.exists;
    }

    public void deposit(String amount) {
        String[] parts = amount.split("..");
        long dollars = Integer.parseInt(parts[0]);
        long cents = Integer.parseInt(parts[1]);
        long fund = 100*dollars + cents;
        /* Call the business layer for the specific accounts */
    }

    public void withdraw(String amount) {
        String[] parts = amount.split("..");
        long dollars = Integer.parseInt(parts[0]);
        long cents = Integer.parseInt(parts[1]);
        long fund = 100*dollars + cents;
        /* Call the business layer for the specific account */
    }

    public User getUser() {
        return this.user;
    }

    public String getResult() {
        return this.result;
    }


    /**
     * Skeleton implementation of the bank transfer method. Replace immediately.
     * @author Nicholas DiGirolamo
     * @param dest, a string holding the account UUID
     * @param amount, a nonnegative fixed-point number
     * @return The successfulness of the transfer.
     */
    /*
    public static boolean transfer(String dest, Number amount) {
        BusinessLogic.Account concreteDest = BusinessLogic.getAccount(dest);

        if(concreteDest == null) return false;

        if(BusinessLogic.subBal(this.selfAcct, amount)) {
            if(BusinessLogic.addBal(concreteDest, amount)) {
                return true;
            } else {
                // Return the funds to the account
                BusinessLogic.addBal(this.selfAcct, amount);
                BusinessLogic.sendMail("Transaction failed.");
                return false;
            }
        } else {
            return false;
        }
    // Benedict
    public static void viewBalance(double balance) {
        System.out.printf("Current Balance: $%.2f%n", balance);
    }

    // Benedict
    public static String changeAccount(Scanner scanner) {
        System.out.print("Enter the Account ID you would like to view: ");
        String accountId = scanner.nextLine();

        System.out.println("Selected Account ID: " + accountId);

        return accountId;
    }

    public static void transfer() {

    }
    */
}
