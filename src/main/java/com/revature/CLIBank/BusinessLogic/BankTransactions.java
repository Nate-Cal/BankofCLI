package com.revature.CLIBank.BusinessLogic;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.AccountInfo;
import com.revature.CLIBank.model.Transaction;
import com.revature.CLIBank.model.TransactionType;


public class BankTransactions {

    AccountRepo accountRepo ;
    //Mo
    public long deposit(AccountInfo accountInfo, long amount) {
        BankActions bankActions = new BankActions();
        if (accountInfo == null || !bankActions.checkDeposit(accountInfo, amount)) {
            BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
            return 0;
        }
        long balance, newBalance;
        balance = accountInfo.getBalance();
        // Overflow must be rejected before anything is saved.
        try { newBalance = Math.addExact(balance, amount); }
        catch (ArithmeticException e) {
            BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
            return 0;
        }
        // Keep Mo's checks; persist both records before changing the account object.
        if (accountRepo == null) accountRepo = new AccountRepo();
        if (!accountRepo.saveBalanceAndHistory(accountInfo, newBalance,
                new Transaction(accountInfo.getAccountID(), TransactionType.DEPOSIT, amount))) {
            BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
            return 0;
        }
        accountInfo.setBalance(newBalance);

        //return the amount deposited
        BankLog.outcome(BankLog.Event.DEPOSIT, true, null);
        return amount;
    }

    //Mo
    public long withdraw(AccountInfo accountInfo, long amount) {
        BankActions bankActions = new BankActions();
        if(accountInfo == null || !bankActions.checkWithdraw(accountInfo, amount)){
            BankLog.outcome(BankLog.Event.WITHDRAWAL, false, null);
            return 0;
        }
        long balance, newBalance;
        balance = accountInfo.getBalance();
        newBalance = balance - amount;
        // Keep Mo's checks; persist both records before changing the account object.
        if (accountRepo == null) accountRepo = new AccountRepo();
        if (!accountRepo.saveBalanceAndHistory(accountInfo, newBalance,
                new Transaction(accountInfo.getAccountID(), TransactionType.WITHDRAWAL, amount))) {
            BankLog.outcome(BankLog.Event.WITHDRAWAL, false, null);
            return 0;
        }
        accountInfo.setBalance(newBalance);

        BankLog.outcome(BankLog.Event.WITHDRAWAL, true, null);
        return amount;
    }

    //Mo
    public synchronized long transfer(AccountInfo sendingAccount, AccountInfo receivingAccount, long amount) {
        BankActions bankActions = new BankActions();
        if (!bankActions.checkTransfer(sendingAccount, receivingAccount, amount)) {
            BankLog.outcome(BankLog.Event.TRANSFER, false, null);
            return 0;
        }

        if (accountRepo == null) //done for unit test
            accountRepo = new AccountRepo();
        if(accountRepo.transferMoney(sendingAccount, receivingAccount, amount)){
            BankLog.outcome(BankLog.Event.TRANSFER, true, null);
            return amount;
        }
        BankLog.outcome(BankLog.Event.TRANSFER, false, null);
        return 0;

       /* long balanceOne = sendingAccount.getBalance();
        long balanceTwo = receivingAccount.getBalance();

        sendingAccount.setBalance(balanceOne - amount);
        receivingAccount.setBalance(balanceTwo - amount);

        // if no error, conn.commit(); will go here

        return amount; */


    }

    public AccountRepo getAccountRepo() {
        return accountRepo;
    }

    public void setAccountRepo(AccountRepo accountRepo) {
        this.accountRepo = accountRepo;
    }
}