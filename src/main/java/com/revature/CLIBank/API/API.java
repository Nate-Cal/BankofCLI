package com.revature.CLIBank.API;

import com.revature.CLIBank.BusinessLogic.*;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;

import com.revature.CLIBank.Utility.Money;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;

public class API {

    // Log early API rejections without recording passwords, PINs, or raw input.
    private static final Logger logger = LoggerFactory.getLogger(API.class);

    private User user;
    private String result;
    private final BankTransactions bankTransactions;
    private final AccountRepo accountRepo;

    public API() {
        this.user = null;
        this.result = null;
        this.bankTransactions = new BankTransactions();
        this.accountRepo = new AccountRepo();
        accountRepo.initSchema();
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
        if(this.accountRepo.findUserByName(username) != null) {
            this.result = "Username taken. Choose a different username.";
            logger.error("REGISTRATION rejected: username already exists");
            return false;
        }

        boolean unameStat = AccountValidation.isUsernameValid(username);
        boolean pwStat = AccountValidation.isPasswordValid(password);

        if(unameStat && pwStat) {
            User tmpUser = new User(username, 0, password);
            AccountRepo.insertUser(tmpUser);
            this.result = "User " + username + " successfully registered.";
        } else {
            this.result =
                    """
                    Username must include 1 Uppercase, 1 Lowercase, and must be between 8 and 16 characters.
                    Password must include 1 Uppercase, 1 Lowercase, 1 number, 1 special character, and be 16-24 characters.
                    """;
        }

        if (!unameStat || !pwStat) logger.error("REGISTRATION rejected: invalid credentials format");
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
        if(uEmpty || pEmpty) {
            logger.error("LOGIN rejected: missing credentials");
            return false;
        }

        User stagedUser = this.accountRepo.findUserByNameAndPassword(username, password);

        if(stagedUser == null) {
            this.result = "Login failed. Try again.";
            logger.error("LOGIN failed");
            return false;
        }

        this.user = stagedUser;
        this.result = "Login successful.";
        logger.info("LOGIN succeeded");
        return true;
    }

    /**
     * Add money to an account that the signed-in user owns
     *
     * @author Nicholas DiGirolamo
     * @param acct
     * @param amount
     */
    public void deposit(String acct, String amount) {
        AccountInfo ai = null;

        try {
            UUID.fromString(acct);
        } catch (IllegalArgumentException e) {
            this.result = "Not a valid bank account number.";
            logger.error("TRANSACTION rejected: invalid account ID");
            return;
        }

        for (AccountInfo a : this.user.getAccounts())
            if (a.getAccountID().toString().equals(acct))
                ai = a;

        if(ai == null) {
            this.result = "You do not own the destination account.";
            logger.error("DEPOSIT rejected: account ownership check failed");
            return;
        }

        long specBalance = parseMoney(amount);

        // Nonzero requests are logged by BankTransactions; zero stops here.
        if (specBalance == 0) logger.error("DEPOSIT rejected: zero or invalid amount");
        if(specBalance == 0 || this.bankTransactions.deposit(ai, specBalance) == 0) {
            this.result = "No money was deposited. Check your prompt again.";
        } else {
            this.result = "$" + Money.fromCents(specBalance) + " successfully deposited into "
                    + ai.getAccountID().toString() + ".";
        }
    }

    /**
     * Remove a specified amount from an account
     *
     * @author Nicholas DiGirolamo
     * @param acct
     * @param amount
     */
    public void withdraw(String acct, String amount, String pin) {
        AccountInfo ai = null;

        try {
            UUID.fromString(acct);
        } catch (IllegalArgumentException e) {
            this.result = "Not a valid bank account number.";
            logger.error("TRANSACTION rejected: invalid account ID");
            return;
        }

        int corrPin = validatePin(pin);
        // validatePin returns -1 for invalid input; retain the existing zero-PIN rejection.
        if(corrPin <= 0) {
            this.result = "Invalid PIN.";
            logger.error("PIN validation rejected");
            return;
        }

        for (AccountInfo a : this.user.getAccounts())
            if (a.getAccountID().toString().equals(acct))
                ai = a;

        if(ai == null) {
            this.result = "You do not own the source account.";
            logger.error("WITHDRAWAL rejected: account ownership check failed");
            return;
        }

        long specBalance = parseMoney(amount);

        if(ai.getPin() != corrPin) {
            this.result = "Incorrect PIN";
            logger.error("WITHDRAWAL rejected: incorrect PIN");
            return;
        }

        // Nonzero requests are logged by BankTransactions; zero stops here.
        if (specBalance == 0) logger.error("WITHDRAW rejected: zero or invalid amount");
        if(specBalance == 0 || this.bankTransactions.withdraw(ai, specBalance) == 0) {
            this.result = "No money was withdrawn. Check your prompt again.";
        } else {
            this.result = "$" + Money.fromCents(specBalance) + " successfully withdrawn from "
                    + ai.getAccountID().toString() + ".";
        }
    }

    public void transfer(String src, String dest, String amount) {
        long money = parseMoney(amount);

        if(money <= 0L) {
            this.result = "Zero, negative, or improperly formatted amount.";
            logger.error("TRANSFER rejected: invalid amount");
            return;
        }

        try {
            UUID.fromString(src);
            UUID.fromString(dest);
        } catch(IllegalArgumentException iae) {
            this.result = "One or both accounts are invalid bank account numbers.";
            logger.error("TRANSFER rejected: invalid account ID");
            return;
        }

        AccountInfo srcAcct = null, destAcct = null;
        for (AccountInfo a : this.user.getAccounts()) {
            if (a.getAccountID().toString().equals(src)) {
                srcAcct = a;
            } else if (a.getAccountID().toString().equals(dest)) {
                destAcct = a;
            }
        }

        if(srcAcct == null) {
            this.result = "Source account does not exist.";
            logger.error("TRANSFER rejected: source account unavailable");
            return;
        }

        if(!srcAcct.getUserID().toString().equals(this.user.getUserID().toString())) {
            this.result = "You are not the owner of the source account.";
            logger.error("TRANSFER rejected: account ownership check failed");
            return;
        }

        if(destAcct == null) {
            this.result = "Destination account does not exist.";
            logger.error("TRANSFER rejected: destination account unavailable");
            return;
        }

        if(srcAcct.equals(destAcct)) {
            this.result = "Cannot transfer money to the same account.";
            logger.error("TRANSFER rejected: same account");
            return;
        }

        if(this.bankTransactions.transfer(srcAcct, destAcct, money) == 0) {
            this.result = "No money was transferred.";
        } else {
            this.result = "Transfer successful.";
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
        if(rows == 0) { this.result = ""; return; }

        List<AccountInfo> accounts = this.user.getAccounts();
        StringBuilder sb = new StringBuilder();

        for(AccountInfo ac : accounts) {
            if(ac.getAccountID().toString().equals(acct)) {
                List<Transaction> listTransactions;

                if(rows > 0) listTransactions = ac.getTransactions(rows);
                else listTransactions = ac.getTransactions(); /* Always negative */

                try {
                    for (Transaction t : listTransactions) {
                        // Display the stored type and dollars; single-account events have no destination.
                        sb.append(t.getType());
                        sb.append(" $");
                        sb.append(Money.fromCents(t.getAmount()));
                        sb.append(" ");
                        sb.append(t.getSourceAccountId());
                        if (t.getDestinationAccountId() != null) {
                            sb.append(" -> ");
                            sb.append(t.getDestinationAccountId());
                        }
                        sb.append(" ");
                        sb.append(t.getTimestamp());
                        sb.append("\n");
                    }
                } catch (NullPointerException npe) {
                    logger.error("TRANSACTION_HISTORY failed: incomplete transaction data");
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

    public void getTransactions(int rows) {
        if (rows <= 0) { this.result = ""; return; }
        this.getTransactions();
        // Limit existing lines instead of padding the result with null entries.
        StringBuilder sb = new StringBuilder();
        this.result.lines().limit(rows).forEach(line -> sb.append(line).append("\n"));
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
        sb.append("\n");
        sb.append("Account number/Type/Balance\n");
        for(AccountInfo ac : accts) {
            sb.append(ac.getAccountID());
            sb.append(" ");
            sb.append(ac.getAccountType());
            sb.append(" $");
            long bal = ac.getBalance();
            sb.append(Money.fromCents(bal));
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
        ai.setBalance(0);
        ai.setUserID(this.user.getUserID());
        ai.setAccountType(at);
        ai.setPin(correctPin);
        ai.setAccountID(ai.getAccountID());
        AccountRepo.insertAccount(ai);
        this.result = "Account successfully created.\nAccount number: " + ai.getAccountID();
    }

    public void deleteAcct(String uuid, String pin) {
        int corrPin = validatePin(pin);

        if(corrPin == -1) {
            this.result = "Invalid PIN";
            logger.error("ACCOUNT_DELETE rejected: invalid PIN");
            return;
        }

        try {
            UUID.fromString(uuid);
        } catch(IllegalArgumentException iae) {
            this.result = "Target account number invalid.";
            logger.error("ACCOUNT_DELETE rejected: invalid account ID");
            return;
        }

        for(AccountInfo ai : this.user.getAccounts()) {
            if(ai.getAccountID().toString().equals(uuid)) {
                AccountRepo.deleteAccount(ai);
                this.result = "Successfully deleted account " + uuid;
                return;
            }
        }
        this.result = "Did not delete account " + uuid + ". Try again.";
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
        // Preserve the existing invalid-input return values and truncation policy.
        // Decimal arithmetic fixes 12.5 and -0.50 without floating-point rounding.
        if (str == null) return 0L;
        String text = str.trim();
        if (text.startsWith("$")) text = text.substring(1);
        if (text.matches("[+-]?[0-9]+\\.-[0-9]+")) return Long.MIN_VALUE;
        if (!text.matches("[+-]?[0-9]+(?:\\.[0-9]*)?")) return 0L;
        try {
            return new BigDecimal(text).setScale(2, RoundingMode.DOWN)
                    .movePointRight(2).longValueExact();
        } catch (NumberFormatException | ArithmeticException e) {
            return 0L;
        }
    }

    private int validatePin(String pin) {
        int correctPin;

        try {
            correctPin = Integer.parseInt(pin);
        } catch(NumberFormatException nfe) {
            this.result = "Invalid PIN.";
            logger.error("PIN validation rejected");
            return -1;
        }

        if(correctPin < 0 || correctPin >= 10_000) {
            this.result = "Invalid PIN.";
            logger.error("PIN validation rejected");
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
            logger.error("ACCOUNT_CREATE rejected: invalid account type");
            return null;
        }
    }
}
