package com.revature.CLIBank.API;
import com.revature.CLIBank.BusinessLogic.AccountInfo;
import java.math.BigDecimal;
import java.util.UUID;

public class BankActions {

    //Mo
    public static boolean checkDeposit(AccountInfo accountInfo, BigDecimal amount){
        //TODO: repository team should provide account lookup using accountID
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

   /* public boolean checkTransfer(UUID accountIDone, UUID accountIDtwo, BigDecimal amount ){
        checkWithdrawal(accountIDone, amount)
    }
    */
}
