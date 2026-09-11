package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;

import java.util.Scanner;

public class API {

    public static void main(String[] args) {
        System.out.println("Welcome to the Bank of CLI!");
        System.out.println();

        returningUser();

        System.out.println();

    }

    public static void returningUser() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Are you a returning User? (y/n): ");
            String yesNo = scanner.nextLine();
            if (yesNo.equals("y")) {
                System.out.println();
                login();
                return;
            } if (yesNo.equals("n")) {
                System.out.println();
                signUp();
            } else {
                System.out.println("Invalid input");
                returningUser();
            }
        }
    }
    public static void signUp() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome New User! Please create an account...");
            AccountInfo account = new AccountInfo();
            String setUserName;
            boolean validUserName;
            do {
                System.out.print("Create a Username: ");
                setUserName = scanner.nextLine();
                validUserName = AccountValidation.isUserNameValid(setUserName);

                if (!validUserName) {
                    AccountValidation.userNameInvalid();
                }
            } while (!validUserName);

            String setPassWord;
            boolean validPassword;
            do {
                System.out.println("Please create a Password: ");
                setPassWord = scanner.nextLine();
                validPassword = AccountValidation.isPassWordValid(setPassWord);

                if (!validPassword) {
                    AccountValidation.passWordInvalid();
                }
            } while (!validPassword);



            // For storing Account Information

            account.account(setUserName, setPassWord);
            System.out.println("Account created! Your account credentials are:");
            System.out.println("Username: " + setUserName);
            System.out.println("Password " + setPassWord);
            System.out.println("AccountID: " + account.getAccountID());
        }
    }

    public static void login(/*String userName, int pin*/) {
//        AccountInfo account = new AccountInfo();
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("What is your Username: ");
            String getUserName = scanner.nextLine();
            System.out.println();
            System.out.println("Hello " + getUserName);
            System.out.print("Please enter your Password: ");
            String getPassWord = scanner.nextLine();

        }

        /*
            Waiting to store Account Information
            Will call account() with stored information to check login status
         */
    }

    public static void deposit() {

    }

    public static void withdraw() {

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
