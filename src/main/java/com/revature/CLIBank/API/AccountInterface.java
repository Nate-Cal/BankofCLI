package com.revature.CLIBank.API;

import java.util.Scanner ;
import java.util.UUID ;

public class AccountInterface {



    //Mo
    public static void deposit(UUID accountID) {
        double balance, newBalance;
        System.out.println("What amount would you like to deposit?");
        Scanner scanner = new Scanner(System.in);
        double amount = scanner.nextDouble();
        /*
        balance = getBalance(accountID);
        getBalance() would be a method in the business layer
        newBalance = amount + balance;
        setBalance(newBalance);
        setBalance would be a method in the business layer
        */
        System.out.println(amount + " deposited");
        System.out.println("New balance: " + newBalance);

    }

    //Mo
    public static void withdraw(UUID accountID) {
        double balance, newBalance;
        System.out.println("What amount would you like to withdraw?");
        Scanner scanner = new Scanner(System.in);
        double amount = scanner.nextDouble();
        /* balance = getBalance(accountID);
        getBalance() would be a method in the business layer
        newBalance = balance - amount;
        setBalance(newBalance);
        setBalance would be a method in the business layer
        */
        System.out.println(amount + " withdrawn");
        System.out.println("New balance: " + newBalance);

    }

}
