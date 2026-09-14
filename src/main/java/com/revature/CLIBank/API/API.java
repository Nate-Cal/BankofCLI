package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;

import java.util.List;
import java.util.UUID;

public class API {

    private User user;
    private String result;
    private BankTransactions bankTransactions;

    public API() {
        this.user = null;
        this.result = null;
        this.bankTransactions = new BankTransactions();
    }

    public boolean register(String username, String password) {
        boolean unameStat = AccountValidation.isUserNameValid(username);
        boolean pwStat = AccountValidation.isPassWordValid(password);

        if(unameStat && pwStat) {
            User tmpUser = new User(username, 0, password);
            AccountRepo.insertUser(tmpUser);
        } else {
            this.result =
             """
             Username must include 1 Uppercase, 1 Lowercase, and must be between 8 and 16 characters.
             Password must include 1 Uppercase, 1 Lowercase, 1 number, 1 special character, and be 16-24 characters.
             """;
        }

        return unameStat && pwStat;
    }

    public boolean login(String username, String password) {
        User stagedUser = new User(username, password);

        if(stagedUser.exists) {
            this.user = stagedUser;
        } else {
            this.result = "Login failed. Try again.";
        }

        return stagedUser.exists;
    }

    public void deposit(String acct, String amount) {
        AccountInfo ai = new AccountInfo(UUID.fromString(acct));

        String[] parts = amount.split("..");
        long dollars = Long.parseLong(parts[0]);
        long cents = Long.parseLong(parts[1]);
        long fund = 100*dollars + cents;

        this.bankTransactions.deposit(ai, fund);
    }

    public void withdraw(String acct, String amount) {
        AccountInfo ai = new AccountInfo(UUID.fromString(acct));

        String[] parts = amount.split("..");
        long dollars = Integer.parseInt(parts[0]);
        long cents = Integer.parseInt(parts[1]);
        long fund = 100*dollars + cents;

        this.bankTransactions.deposit(ai, fund);
    }

    public void transfer(String src, String dest, String amount) {
        String[] parts = amount.split("..");
        AccountInfo srcAcct = new AccountInfo(UUID.fromString(src));
        AccountInfo destAcct = new AccountInfo(UUID.fromString(dest))

        this.bankTransactions.transfer(srcAcct, destAcct,
                100*Long.parseLong(parts[0]) + Long.parseLong(parts[1]));
    }

    public void getAcctTransactions(String acct, int n) {
        List<AccountInfo> accounts = this.user.getAccounts();
        StringBuilder sb = new StringBuilder();

        for(AccountInfo ac : accounts) {
            if(ac.getAccountID().toString().equals(acct)) {
                List<Transaction> lt;

                if(n >= 0) lt = ac.getTransactions(n);
                else lt = ac.getTransactions();

                for(Transaction t : lt) {
                    sb.append(t.getSourceAccountId());
                    sb.append(" ");
                    sb.append(t.getDestinationAccountId());
                    sb.append(" ");
                    sb.append(t.getAmount());
                    sb.append(" ");
                    sb.append(t.getTimestamp());
                    sb.append("\n");
                }
            }
        }
        this.result = sb.toString();
    }

    public void getTransactions() {
        List<AccountInfo> accounts = this.user.getAccounts();
        StringBuilder sb = new StringBuilder();

        for(AccountInfo ai : accounts) {
            getAcctTransactions(ai.getAccountID().toString(), -1);
            sb.append(this.result);
        }

        this.result = sb.toString();
    }

    public void getAccts() {
        StringBuilder sb = new StringBuilder();
        List<AccountInfo> accts = this.user.getAccounts();

        sb.append("Your accounts: ");
        sb.append(accts.size());
        sb.append("Account number/Type/Balance\n");
        for(AccountInfo ac : accts) {
            sb.append(ac.getAccountID());
            sb.append(" ");
            sb.append(ac.getAccountType());
            sb.append(" ");
            sb.append(ac.getBalance());
            sb.append("\n");
        }
        this.result = sb.toString();
    }

    public User getUser() {
        return this.user;
    }

    public String getResult() {
        return this.result;
    }
}
