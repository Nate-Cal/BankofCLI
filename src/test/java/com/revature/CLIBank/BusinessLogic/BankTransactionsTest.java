package com.revature.CLIBank.BusinessLogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class BankTransactionsTest {

    @Test
    void deposit() {
        AccountInfo account = new AccountInfo();
        account.account("alice", "password"); // gives accountID != null
        account.setStatus(true);              // required by checkDeposit
        account.setBalance(100L);             // start balance
        BankTransactions bankTransactions = new BankTransactions();

        long result = bankTransactions.deposit(account, 50L);

        assertEquals(50L, result);
        assertEquals(150L, account.getBalance());
    }
}