package com.revature.CLIBank.BusinessLogic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.revature.CLIBank.model.AccountInfo;

class BankTransactionsTest {

    @Test
    void deposit() {
        AccountInfo account = new AccountInfo(UUID.randomUUID(), 1234);
        account.setBalance(100L);
        account.setFrozen(false);

        BankTransactions bankTransactions = new BankTransactions();
        long result = bankTransactions.deposit(account, 50L);

        assertEquals(50L, result);
        assertEquals(150L, account.getBalance());
    }

    @Test
    void transfer() {
    }
}