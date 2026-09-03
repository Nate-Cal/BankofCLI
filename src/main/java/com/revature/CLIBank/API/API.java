package com.revature.CLIBank.API;

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
            String name = scanner.nextLine();
            System.out.println("Please create a PIN: ");
            System.out.println("PIN must include ...");
            String pinAsString = scanner.nextLine();
            int pin = Integer.parseInt(pinAsString);
        }
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

    public static void transfer() {

    }

}
