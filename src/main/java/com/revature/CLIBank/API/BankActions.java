package com.revature.CLIBank.API;
import com.revature.CLIBank.model.AccountInfo;

public class BankActions {

    //Mo
    public boolean checkDeposit(AccountInfo accountInfo, long amount){
        return accountInfo.getAccountID() != null
                && accountInfo.isFrozen()
                && amount > 0;

    }
    public boolean checkWithdraw(AccountInfo accountInfo, long amount){
        //AccountInfo accountInfo = new AccountInfo(accountID);
        return accountInfo.getAccountID() != null
                && accountInfo.isFrozen()
                && amount > 0
                && amount <= accountInfo.getBalance();

    }

   public boolean checkTransfer(AccountInfo sendingAccount, AccountInfo receivingAccount, long amount ){
        return sendingAccount != null
                && receivingAccount != null
                && sendingAccount.isFrozen()
                && receivingAccount.isFrozen()
                && !sendingAccount.getAccountID().equals(receivingAccount.getAccountID())
                && amount > 0
                && sendingAccount.getBalance() >= amount;
    }

}
