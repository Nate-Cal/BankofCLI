package com.revature.CLIBank.API;

import com.revature.CLIBank.model.AccountInfo;
import com.revature.CLIBank.BusinessLogic.BankTransactions;

import java.util.Scanner ;


public class AccountInterface {

    //Mo
    public void askDeposit(AccountInfo accountInfo){
        System.out.println("What amount would you like to deposit?");
        Scanner scanner = new Scanner(System.in);
        long amount = scanner.nextLong();
        BankTransactions bankTransactions = new BankTransactions();
        long amountDeposited = bankTransactions.deposit(accountInfo, amount);
        if (amountDeposited > 0) {
            System.out.println(amountDeposited + "deposited.");
            System.out.println("New balance: " + accountInfo.getBalance());
        }
        else
        {
            System.out.println("Deposit failed.");

        }

    }

    //Mo
    public void askWithdraw(AccountInfo accountInfo) {
        System.out.println("What amount would you like to withdraw?");
        Scanner scanner = new Scanner(System.in);
        long amount = scanner.nextLong();

        BankTransactions bankTransactions = new BankTransactions();
        long amountWithdrawn = bankTransactions.withdraw(accountInfo, amount);
        if (amountWithdrawn > 0) {
            System.out.println(amount + "withdrawn.");
            System.out.println("New Balance: " + accountInfo.getBalance());
        }
        else
            System.out.println("Withdrawal failed.");

    }

    //Mo
    public void askTransfer(AccountInfo sendingAccount, AccountInfo receivingAccount) {
        System.out.println("Enter the transfer amount.");
        Scanner scanner = new Scanner(System.in);
        long amount = scanner.nextLong();
        BankTransactions bankTransactions = new BankTransactions();

        long amountTransferred = bankTransactions.transfer(sendingAccount, receivingAccount, amount);
        if (amountTransferred > 0) {
            System.out.println(amount + " transferred from " + sendingAccount.getAccountName());
            System.out.println(sendingAccount.getAccountName() + " balance:  " + sendingAccount.getBalance());
            System.out.println(receivingAccount.getAccountName() + " balance:  " + receivingAccount.getBalance());

        }
        else
            System.out.println("Transfer failed.");

    }


    }






