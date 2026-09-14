package com.revature.CLIBank.BusinessLogic;
import com.revature.CLIBank.model.AccountInfo;
import com.revature.CLIBank.API.BankActions;


public class BankTransactions {
    //Mo
    public long deposit(AccountInfo accountInfo, long amount) {
        BankActions bankActions = new BankActions();
        if (!bankActions.checkDeposit(accountInfo, amount)) {
            return 0;
        }
        long balance, newBalance;
        balance = accountInfo.getBalance();
        /*TODO: Dummy getBalance() has been added BusinessLogic/AccountInfo.
        Logic for getBalance() needs to be written. */
        newBalance = balance + amount;
        accountInfo.setBalance(newBalance);
        /*TODO: Dummy setBalance() has been added in BusinessLogic/AccountInfo.java
        Logic for setBalance() needs to be written. */

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
    public synchronized long transfer(AccountInfo sendingAccount, AccountInfo receivingAccount, long amount){
        BankActions bankActions = new BankActions();
        if(!bankActions.checkTransfer(sendingAccount, receivingAccount, amount)){
            return 0;
        }

        // TODO: repo team can provide one shared DB connection/transaction
        // Connection conn = ConnectionFactory.getConnection();
        // conn.setAutoCommit(false);
        // I (Mo) will handle the Atomicity requirement once that happens


        long balanceOne = sendingAccount.getBalance();
        long balanceTwo = receivingAccount.getBalance();

        sendingAccount.setBalance(balanceOne - amount);
        receivingAccount.setBalance(balanceTwo - amount);

        // if no error, conn.commit(); will go here

        return amount;


    }
}

