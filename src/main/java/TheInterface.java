package com.revature.CLIBank.API;

import java.util.Scanner;

public class TheInterface {
    public static void main(String[] args) {
        System.out.println("Welcome to the Bank of CLI!");
        System.out.println();
        login();

        System.out.println();



    }


    public static void login() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("What is your Username: ");
            String name = scanner.nextLine();
            System.out.println("Hello " + name);
            System.out.print("Please enter your PIN: ");
            String pinAsString = scanner.nextLine();
            int pin = Integer.parseInt(pinAsString);
        }
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

}