package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.AccountInfo;
import com.revature.CLIBank.BusinessLogic.BankTransactions;

import java.math.BigDecimal;
import java.util.Scanner ;
import java.util.UUID ;

public class AccountInterface {

    //Mo
    public static void askDeposit(AccountInfo accountInfo){
        System.out.println("What amount would you like to deposit?");
        Scanner scanner = new Scanner(System.in);
        BigDecimal amount = scanner.nextBigDecimal();

        BigDecimal amountDeposited = BankTransactions.deposit(accountInfo, amount);
        if (amountDeposited.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println(amountDeposited + "deposited.");
            System.out.println("New balance: " + accountInfo.getBalance());
        }
        else
        {
            System.out.println("Deposit failed.");

        }

    }

    //Mo
    public static void askWithdraw(AccountInfo accountInfo) {
        System.out.println("What amount would you like to withdraw?");
        Scanner scanner = new Scanner(System.in);
        BigDecimal amount = scanner.nextBigDecimal();

        BigDecimal amountWithdrawn = BankTransactions.withdraw(accountInfo, amount);
        if (amountWithdrawn.compareTo(BigDecimal.ZERO) > 0) {
            System.out.println(amount + "withdrawn.");
            System.out.println("New Balance: " + accountInfo.getBalance());
        }
        else
            System.out.println("Withdrawal failed.");

    }

    //Mo
    public static void askTransfer(){
        System.out.println("Which account would you like to transfer from?");
        Scanner scanner = new Scanner(System.in);
        String firstAccount = scanner.nextLine();
        UUID firstAccountID = UUID.fromString(firstAccount);
        System.out.println("Which account would you like to transfer to?");
        String secondAccount = scanner.nextLine();
        UUID secondAccountID = UUID.fromString(secondAccount);
        BankTransactions.transfer(firstAccountID, secondAccountID);
    }

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
        System.out.println("New balance: " /*+ newBalance*/);

    }

    //Mo
    public static void withdraw(UUID accountID) {
        double balance, newBalance;
        System.out.println("What amount would you like to withdraw?");
        Scanner scanner = new Scanner(System.in);
        double amount = scanner.nextDouble();
        /* balance = getBalance(accountID);
        getBalance() would be a method in the business layer
        newBalance                                                                                                          = balance - amount;
        setBalance(newBalance);
        setBalance would be a method in the business layer
        */
        System.out.println(amount + " withdrawn");
        System.out.println("New balance: " /*+ newBalance*/);

    }

}
