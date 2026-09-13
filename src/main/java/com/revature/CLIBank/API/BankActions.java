package com.revature.CLIBank.API;
import com.revature.CLIBank.BusinessLogic.AccountInfo;
import java.math.BigDecimal;
import java.util.UUID;

public class BankActions {

    //Mo
    public static boolean checkDeposit(AccountInfo accountInfo, BigDecimal amount){
        return accountInfo.getAccountID() != null
                && accountInfo.isStatus()
                && amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0;

    }
    public static boolean checkWithdraw(AccountInfo accountInfo, BigDecimal amount){
        //AccountInfo accountInfo = new AccountInfo(accountID);
        return accountInfo.getAccountID() != null
                && accountInfo.isStatus()
                && amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && accountInfo.getBalance().compareTo(amount) >= 0;

    }

   public static boolean checkTransfer(AccountInfo sendingAccount, AccountInfo receivingAccount, BigDecimal amount ){
        return sendingAccount != null
                && receivingAccount != null
                && sendingAccount.isStatus()
                && receivingAccount.isStatus()
                && !sendingAccount.getAccountID().equals(receivingAccount.getAccountID())
                && amount != null
                && amount.compareTo(BigDecimal.ZERO) > 0
                && sendingAccount.getBalance().compareTo(amount) >= 0;
    }

}
