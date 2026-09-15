package com.revature.CLIBank.BusinessLogic;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.AccountInfo;


public class BankTransactions {
    //Mo
    public long deposit(AccountInfo accountInfo, long amount) {
        BankActions bankActions = new BankActions();
        if (!bankActions.checkDeposit(accountInfo, amount)) {
            return 0;
        }
        long balance, newBalance;
        balance = accountInfo.getBalance();
        newBalance = balance + amount;
        accountInfo.setBalance(newBalance);

        //return the amount deposited
        return amount;
    }

    //Mo
    public long withdraw(AccountInfo accountInfo, long amount) {
        BankActions bankActions = new BankActions();
        if(!bankActions.checkWithdraw(accountInfo, amount)){
            return 0;
        }
        long balance, newBalance;
        balance = accountInfo.getBalance();
        newBalance = balance - amount;
        accountInfo.setBalance(newBalance);

        return amount;
    }

    //Mo
    public synchronized long transfer(AccountInfo sendingAccount, AccountInfo receivingAccount, long amount) {
        BankActions bankActions = new BankActions();
        if (!bankActions.checkTransfer(sendingAccount, receivingAccount, amount)) {
            return 0;
        }

        AccountRepo accountRepo = new AccountRepo();
         if(accountRepo.transferMoney(sendingAccount, receivingAccount, amount)){
             return amount;
         }
        return 0;

       /* long balanceOne = sendingAccount.getBalance();
        long balanceTwo = receivingAccount.getBalance();

        sendingAccount.setBalance(balanceOne - amount);
        receivingAccount.setBalance(balanceTwo - amount);

        // if no error, conn.commit(); will go here

        return amount; */


    }
}

