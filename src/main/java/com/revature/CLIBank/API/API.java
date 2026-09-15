package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;

import java.util.List;
import java.util.UUID;

public class API {

    private User user;
    private String result;
    private final BankTransactions bankTransactions;

    public API() {
        this.user = null;
        this.result = null;
        this.bankTransactions = new BankTransactions();
    }

    /**
     * Send the credentials from the user to business layer
     * to add them to the database, so they pass the requirements.
     *
     * @author Nicholas DiGirolamo
     * @param username
     * @param password
     * @return boolean, the success of the registration
     */
    public boolean register(String username, String password) {
        boolean unameStat = AccountValidation.isUsernameValid(username);
        boolean pwStat = AccountValidation.isPasswordValid(password);

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

    /**
     * Send the credentials from the user to check if such a
     * user exists in the database. If so, update the session
     * to store the user corresponding to those credentials.
     *
     * @author Nicholas DiGirolamo
     * @param username
     * @param password
     * @return boolean, the success of the login attempt
     */
    public boolean login(String username, String password) {
        boolean uEmpty = username.isEmpty();
        boolean pEmpty = password.isEmpty();

        if(pEmpty) this.result = "Please enter a password";
        if(uEmpty) this.result = "Please enter a username";
        if(uEmpty || pEmpty) return false;

        User stagedUser = new User(username, password);

        if(stagedUser.exists) {
            this.user = stagedUser;
            this.result = "Login successful.";
        } else {
            this.result = "Login failed. Try again.";
        }

        return stagedUser.exists;
    }

    public void deposit(String acct, String amount) {
        AccountInfo ai = new AccountInfo(UUID.fromString(acct));
        this.bankTransactions.deposit(ai, parseMoney(amount));
    }

    public void withdraw(String acct, String amount) {
        AccountInfo ai = new AccountInfo(UUID.fromString(acct));
        this.bankTransactions.deposit(ai, parseMoney(amount));
    }

    public void transfer(String src, String dest, String amount) {
        String[] parts = amount.split("..");
        AccountInfo srcAcct = new AccountInfo(UUID.fromString(src));
        AccountInfo destAcct = new AccountInfo(UUID.fromString(dest));

        this.bankTransactions.transfer(srcAcct, destAcct,
                100*Long.parseLong(parts[0]) + Long.parseLong(parts[1]));
    }

    /**
     * Retrieve `rows` number of the most recent transactions from a
     * single bank account.
     *
     * @author Nicholas DiGirolamo
     * @param acct, a String representation of a bank account UUID--not a user.
     * @param rows, the number of rows to return.
     *              rows >= 0: that number of rows are returned
     *              rows < 0: return all rows.
     */
    public void getAcctTransactions(String acct, int rows) {
        if(rows == 0) { this.result = ""; return; }

        List<AccountInfo> accounts = this.user.getAccounts();
        StringBuilder sb = new StringBuilder();

        for(AccountInfo ac : accounts) {
            if(ac.getAccountID().toString().equals(acct)) {
                List<Transaction> listTransactions;

                if(rows > 0) listTransactions = ac.getTransactions(rows);
                else listTransactions = ac.getTransactions();

                try {
                    for (Transaction t : listTransactions) {
                        sb.append(t.getSourceAccountId());
                        sb.append(" ");
                        sb.append(t.getDestinationAccountId());
                        sb.append(" ");
                        sb.append(t.getAmount());
                        sb.append(" ");
                        sb.append(t.getTimestamp());
                        sb.append("\n");
                    }
                } catch (NullPointerException npe) {
                    sb.append("Warning: null transaction.\n");
                }
            }
        }
        this.result = sb.toString();
    }

    /**
     * Get all transactions associated with a user, querying every
     * account.
     *
     * @author Nicholas DiGirolamo
     */
    public void getTransactions() {
        List<AccountInfo> accounts = this.user.getAccounts();
        StringBuilder sb = new StringBuilder();

        if(accounts.isEmpty()) {
            this.result = "You have no accounts. Open one to see information.";
            return;
        }

        for(AccountInfo ai : accounts) {
            getAcctTransactions(ai.getAccountID().toString(), -1);
            sb.append(this.result);
        }

        this.result = sb.toString();
    }

    /**
     * Retrieve the list of all accounts associated with the user
     * the API session currently holds.
     *
     * @author Nicholas DiGirolamo
     */
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

    public void creatAcct(String pin, String type) {
        AccountType at = validateAcctType(type);
        if(at == null) return;

        int correctPin = validatePin(pin);
        if(correctPin == -1) return;

        AccountInfo ai = new AccountInfo(this.user.getUserID(), correctPin, at);
        AccountRepo.insertAccount(ai);
    }

    public void deleteAcct(String uuid, String pin, String type) {
        UUID id = UUID.fromString(uuid);
        int corrPin = validatePin(pin);
        AccountType at = validateAcctType(type);
        if(corrPin == -1) return;

        AccountRepo ar = new AccountRepo();
        AccountInfo ai = ar.findAccountById(id);
        // AccountRepo.removeUser(ai);
    }

    public User getUser() {
        return this.user;
    }

    public String getResult() {
        return this.result;
    }

    /**
     * Ingest a String representing money and return
     * a long, in cents.
     *
     * @author Nicholas DiGirolamo
     * @param str, a string meant to hold a dollar amount.
     *             This may be of the form `$DOLLARS.CENTS`
     *             or `DOLLARS.CENTS`.
     * @return long, the number of cents equivalent to the amount
     * specified.
     */
    long parseMoney(String str) {
        long dollars = 0, cents = 0;

        try {
            String[] parts = str.split("\\.");

            /* Remove dollar sign if and only if it is at the beginning,
             * otherwise, rely on exception handling. */
            if(parts[0].charAt(0) == '$')
                parts[0] = parts[0].substring(1);

            dollars = Long.parseLong(parts[0]);
            cents = Long.parseLong(parts[1].substring(0, 2));
        } catch(NumberFormatException nfe) {
            return Long.MIN_VALUE;
        } catch(ArithmeticException ae) {
            return Long.MAX_VALUE;
        } catch(IndexOutOfBoundsException ioob) {
            return 100*dollars;
        }

        if(cents < 0) return Long.MIN_VALUE;

        return 100*dollars + cents;
    }

    private int validatePin(String pin) {
        int correctPin;

        try {
            correctPin = Integer.parseInt(pin);
        } catch(NumberFormatException nfe) {
            this.result = "Invalid PIN.";
            return -1;
        }

        if(correctPin < 0 || correctPin >= 10_000) {
            this.result = "Invalid PIN.";
            return -1;
        }
        return correctPin;
    }

    AccountType validateAcctType(String type) {
        if(type.equalsIgnoreCase("checking")) {
            return AccountType.CHECKING;
        } else if(type.equalsIgnoreCase("savings")) {
            return AccountType.SAVINGS;
        } else {
            this.result = "Invalid account type.";
            return null;
        }
    }
}
