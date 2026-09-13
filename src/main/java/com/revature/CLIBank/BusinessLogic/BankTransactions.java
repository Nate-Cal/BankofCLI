package com.revature.CLIBank.BusinessLogic;

import com.revature.CLIBank.API.BankActions;

import java.math.BigDecimal;
import java.util.Scanner;
import java.util.UUID;

public class BankTransactions {
    //Mo
    public static BigDecimal deposit(AccountInfo accountInfo, BigDecimal amount) {
        if (!BankActions.checkDeposit(accountInfo, amount)) {
            return BigDecimal.ZERO;
        }
        BigDecimal balance, newBalance;
        balance = accountInfo.getBalance();
        /*TODO: Dummy getBalance() has been added BusinessLogic/AccountInfo.
        Logic for getBalance() needs to be written. */
        newBalance = balance.add(amount);
        accountInfo.setBalance(newBalance);
        /*TODO: Dummy setBalance() has been added in BusinessLogic/AccountInfo.java
        Logic for setBalance() needs to be written. */

        return amount;
    }

    //Mo
    public static BigDecimal withdraw(AccountInfo accountInfo, BigDecimal amount) {
        if(!BankActions.checkWithdraw(accountInfo, amount)){
            return BigDecimal.ZERO;
        }
        BigDecimal balance, newBalance;
        balance = accountInfo.getBalance();
        newBalance = balance.subtract(amount);
        accountInfo.setBalance(newBalance);

        return amount;
    }

    //Mo
    public static void transfer(UUID accountIDone, UUID accountIDtwo){

    }
}

