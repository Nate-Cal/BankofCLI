package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;

import com.revature.CLIBank.Repository.RepositoryException;
import com.revature.CLIBank.Utility.Money;
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
        new AccountRepo().initSchema();
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
        // Report failures without exposing technical details or logging credentials
        try {
            boolean unameStat = username != null && AccountValidation.isUsernameValid(username);
            boolean pwStat = password != null && AccountValidation.isPasswordValid(password);

            if(unameStat && pwStat) {
                User tmpUser = new User(username, 0, password);
                AccountRepo.insertUser(tmpUser);
                this.result = "Registration successful.";
                BankLog.outcome(BankLog.Event.REGISTRATION, true, null);
            } else {
                this.result =
                        """
                        Username must include 1 Uppercase, 1 Lowercase, and must be between 8 and 16 characters.
                        Password must include 1 Uppercase, 1 Lowercase, 1 number, 1 special character, and be 16-24 characters.
                        """;
            }

            if (!(unameStat && pwStat)) BankLog.outcome(BankLog.Event.REGISTRATION, false, null);
            return unameStat && pwStat;
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.REGISTRATION, false, null);
            return false;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.REGISTRATION, false, null);
            return false;
        }
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
        // Report failures without exposing technical details or logging credentials
        try {
            // A failed login must not keep a previous user's session
            this.user = null;
            boolean uEmpty = username == null || username.isEmpty();
            boolean pEmpty = password == null || password.isEmpty();

            if(pEmpty) this.result = "Please enter a password";
            if(uEmpty) this.result = "Please enter a username";
            if(uEmpty || pEmpty) {
                BankLog.outcome(BankLog.Event.LOGIN, false, null);
                return false;
            }

            User stagedUser = new User(username, password);

            if(stagedUser.exists) {
                this.user = stagedUser;
                this.result = "Login successful.";
            } else {
                this.result = "Login failed. Try again.";
            }

            BankLog.outcome(BankLog.Event.LOGIN, stagedUser.exists, null);
            return stagedUser.exists;
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.LOGIN, false, null);
            return false;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.LOGIN, false, null);
            return false;
        }
    }

    public void deposit(String acct, String amount) {
        // Report failures without exposing technical details or logging credentials
        try {
            AccountInfo ai = new AccountInfo(UUID.fromString(acct));
            // Reject an account outside the current session before changing money
            if (this.user == null || !this.user.getUserID().equals(ai.getUserID())) {
                this.result = "Please log in and choose one of your own accounts.";
                BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
                return;
            }
            long specBalance = parseMoney(amount);

            if(this.bankTransactions.deposit(ai, specBalance) == 0) {
                this.result = "No money was deposited. Check your prompt again.";
            } else {
                this.result = "$" + Money.fromCents(specBalance) + " successfully deposited into "
                        + ai.getAccountID().toString() + ".";
            }
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.DEPOSIT, false, null);
            return;
        }
    }

    public void withdraw(String acct, String amount) {
        // Report failures without exposing technical details or logging credentials
        try {
            AccountInfo ai = new AccountInfo(UUID.fromString(acct));
            // Reject an account outside the current session before changing money
            if (this.user == null || !this.user.getUserID().equals(ai.getUserID())) {
                this.result = "Please log in and choose one of your own accounts.";
                BankLog.outcome(BankLog.Event.WITHDRAWAL, false, null);
                return;
            }
            long specBalance = parseMoney(amount);

            if(this.bankTransactions.withdraw(ai, specBalance) == 0) {
                this.result = "No money was withdrawn. Check your prompt again.";
            } else {
                this.result = "$" + Money.fromCents(specBalance) + " successfully withdrawn from "
                        + ai.getAccountID().toString() + ".";
            }
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.WITHDRAWAL, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.WITHDRAWAL, false, null);
            return;
        }
    }

    public void transfer(String src, String dest, String amount) {
        // Report failures without exposing technical details or logging credentials
        try {
            // Use the same amount parser as deposits/withdrawals
            long money = parseMoney(amount);
            AccountInfo srcAcct = new AccountInfo(UUID.fromString(src));
            AccountInfo destAcct = new AccountInfo(UUID.fromString(dest));
            if (this.user == null || !this.user.getUserID().equals(srcAcct.getUserID())) {
                this.result = "You do not own the source account.";
                BankLog.outcome(BankLog.Event.TRANSFER, false, null);
                return;
            }
            this.result = this.bankTransactions.transfer(srcAcct, destAcct, money) == 0
                    ? "No money was transferred." : "Transfer successful.";
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.TRANSFER, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.TRANSFER, false, null);
            return;
        }
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
        // Business layer handles ownership, ordering and the history event
        if (rows == 0) { this.result = ""; return; }
        try {
            List<Transaction> transactions = new TransactionHistory().get(this.user, acct, rows);
            StringBuilder sb = new StringBuilder();
            for (Transaction t : transactions) {
                sb.append(t.getType()).append(" $").append(Money.fromCents(t.getAmount()))
                        .append(" ").append(t.getSourceAccountId());
                if (t.getDestinationAccountId() != null) sb.append(" -> ").append(t.getDestinationAccountId());
                sb.append(" ").append(t.getTimestamp()).append("\n");
            }
            this.result = transactions.isEmpty() ? "No transactions yet." : sb.toString();
        } catch (RepositoryException e) {
            this.result = "Transaction history is unavailable. Please try again.";
        } catch (IllegalArgumentException e) {
            this.result = "Please log in and choose one of your accounts with a valid account number.";
        }
    }

    /**
     * Get all transactions associated with a user, querying every
     * account.
     *
     * @author Nicholas DiGirolamo
     */
    public void getTransactions() {
        getAcctTransactions(null, -1);
    }

    public void getTransactions(int rows) {
        getAcctTransactions(null, rows);
    }

    /**
     * Retrieve the list of all accounts associated with the user
     * the API session currently holds.
     *
     * @author Nicholas DiGirolamo
     */
    public void getAccts() {
        // Report failures without exposing technical details or logging credentials
        if (this.user == null) {
            this.result = "Please log in first.";
            BankLog.outcome(BankLog.Event.ACCOUNTS, false, null);
            return;
        }
        try {
            StringBuilder sb = new StringBuilder();
            List<AccountInfo> accts = this.user.getAccounts();

            sb.append("Your accounts: ");
            sb.append(accts.size());
            sb.append("\n");
            sb.append("Account number/Type/Balance\n");
            for(AccountInfo ac : accts) {
                sb.append(ac.getAccountID());
                sb.append(" ");
                sb.append(ac.getAccountType());
                sb.append(" ");
                sb.append("$").append(Money.fromCents(ac.getBalance()));
                sb.append("\n");
            }
            this.result = sb.toString();
            BankLog.outcome(BankLog.Event.ACCOUNTS, true, null);
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.ACCOUNTS, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.ACCOUNTS, false, null);
            return;
        }
    }

    public void creatAcct(String pin, String type) {
        // Report failures without exposing technical details or logging credentials
        if (this.user == null) {
            this.result = "Please log in first.";
            BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, false, null);
            return;
        }
        try {
            AccountType at = validateAcctType(type);
            if(at == null) { BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, false, null); return; }

            int correctPin = validatePin(pin);
            if(correctPin == -1) { BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, false, null); return; }

            AccountInfo ai = new AccountInfo(this.user.getUserID(), correctPin, at);
            AccountRepo.insertAccount(ai);
            this.result = "Account successfully created.\nAccount number: " + ai.getAccountID();
            BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, true, null);
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.ACCOUNT_CREATE, false, null);
            return;
        }
    }

    public void deleteAcct(String uuid, String pin) {
        // Report failures without exposing technical details or logging credentials
        if (this.user == null) {
            this.result = "Please log in first.";
            BankLog.outcome(BankLog.Event.ACCOUNT_DELETE, false, null);
            return;
        }
        try {
            UUID id = UUID.fromString(uuid);
            int corrPin = validatePin(pin);

            for(AccountInfo ai : user.getAccounts()) {
                if(ai.getPin() == corrPin && ai.getAccountID().equals(id)) {
                    AccountRepo.deleteAccount(ai);
                    this.result = "Successfully deleted account " + uuid;
                    // Stop here so a successful result is not replaced by a failure.
                    BankLog.outcome(BankLog.Event.ACCOUNT_DELETE, true, null);
                    return;
                }
            }
            this.result = "Did not delete account " + uuid + ". Try again.";
            BankLog.outcome(BankLog.Event.ACCOUNT_DELETE, false, null);
        } catch (RepositoryException e) {
            this.result = "Service unavailable. Please try again.";
            BankLog.outcome(BankLog.Event.ACCOUNT_DELETE, false, null);
            return;
        } catch (IllegalArgumentException e) {
            this.result = "Invalid input. Please check your request.";
            BankLog.outcome(BankLog.Event.ACCOUNT_DELETE, false, null);
            return;
        }
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
        // Preserve truncation after two decimals, but handle one decimal and overflow safely
        if (str == null) return 0L;
        if (str.matches("\\$?-?\\d+\\.-\\d+")) return Long.MIN_VALUE;
        if (!str.matches("\\$?-?\\d+(\\.\\d*)?")) return 0L;
        try {
            String number = str.startsWith("$") ? str.substring(1) : str;
            return new java.math.BigDecimal(number).movePointRight(2)
                    .setScale(0, java.math.RoundingMode.DOWN).longValueExact();
        } catch (ArithmeticException | NumberFormatException e) {
            return 0L;
        }
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
        if("checking".equalsIgnoreCase(type)) {
            return AccountType.CHECKING;
        } else if("savings".equalsIgnoreCase(type)) {
            return AccountType.SAVINGS;
        } else {
            this.result = "Invalid account type.";
            return null;
        }
    }
}
