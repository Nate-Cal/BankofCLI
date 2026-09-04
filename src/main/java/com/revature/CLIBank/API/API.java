package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.AccountInfo;

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
            System.out.print("Create a Username: ");
            String setUserName = scanner.nextLine();
            System.out.println("Please create a PIN: ");
            System.out.println("PIN must include ...");
            String setPin = scanner.nextLine();
            int pin = Integer.parseInt(setPin);

            // For storing Account Information
            AccountInfo account = new AccountInfo();
            account.account(setUserName, pin);
            System.out.println("Account created! Your account credentials are:");
            System.out.println("Username: " + setUserName);
            System.out.println("PIN: " + pin);
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
            System.out.print("Please enter your PIN: ");
            String getPin = scanner.nextLine();
            int pin = Integer.parseInt(getPin);
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

}
