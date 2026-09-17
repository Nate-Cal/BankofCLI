package com.revature.CLIBank.API;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import com.revature.CLIBank.TestAccounts;
import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.model.*;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

public class APITest {

    @Test
    public void login_fail_badcreds() {
        API api = new API();
        /* Bad username, bad password that do not meet standads */
        Assertions.assertFalse(api.login("username", "password"));
    }

    /**
     * Test login when there are credentials that pass the
     * requirements but do not exist within the database.
     * @author Nicholas DiGirolamo
     */
    @Test
    public void login_fail_goodcreds() {
        API api = new API();
        Assertions.assertFalse(api.login(uniqueUsername(), "Pa$$w0rdPa$$w0rd"));
    }

    @Test
    public void login_fail_empty() {
        API api = new API();

        api.login("", "Pa$$w0rdPassw0rd");
        Assertions.assertEquals("Please enter a username", api.getResult());
    }

    @Test
    public void zeroTransactions() {
        API api = new API();

        api.getAcctTransactions(null, 0);
        Assertions.assertTrue(api.getResult().isEmpty());
    }

    /**
     * This program does not support mill (1/1000 dollar).
     * The API truncates numbers after the decimal point to
     * two decimal positions.
     */
    @Test
    public void moneyMill() {
        API api = new API();

        long intended = 12345L;
        Assertions.assertEquals(intended, api.parseMoney("$123.456789"));
    }

    @Test
    public void negativeCents() {
        API api = new API();
        Assertions.assertEquals(Long.MIN_VALUE, api.parseMoney("100.-10"));
    }

    @Test
    public void dollarAmountEquality() {
        API api = new API();

        long intended = 10000L;
        long amt0 = api.parseMoney("100.00");
        long amt1 = api.parseMoney("$100");

        Assertions.assertNotEquals(0L, amt0);
        Assertions.assertEquals(intended, amt0);
        Assertions.assertEquals(intended, amt1);
        Assertions.assertEquals(amt0, amt1);
    }

    @Test
    public void registerTestFail() {
        API api = new API();

        api.register("a", "a");
        Assertions.assertEquals("""
             Username must include 1 Uppercase, 1 Lowercase, and must be between 8 and 16 characters.
             Password must include 1 Uppercase, 1 Lowercase, 1 number, 1 special character, and be 16-24 characters.
             """, api.getResult());
    }

    @Test
    public void registerTestPass() {
        API api = new API();
        String username = uniqueUsername();
        api.register(username, "Pa$$w0rdPa$$w0rd");
        Assertions.assertEquals("User " + username + " successfully registered.", api.getResult());
    }

    @Test
    public void usernameRepeat() {
        API api = new API();
        String username = uniqueUsername();

        api.register(username, "Pa$$w0rdPa$$w0rd");
        Assertions.assertEquals("User " + username + " successfully registered.", api.getResult());

        api.register(username, "Abc123$%Abc123$%");
        Assertions.assertEquals("Username taken. Choose a different username.", api.getResult());
    }

    @Test
    public void transferNonUUID() {
        API api = new API();

        String username = uniqueUsername();
        assertTrue(api.register(username, "Pa$$w0rdPa$$w0rd"));
        assertTrue(api.login(username, "Pa$$w0rdPa$$w0rd"));
        api.transfer("foo", "bar", "100.00");
        Assertions.assertEquals("One or both accounts are invalid bank account numbers.", api.getResult());
    }

    @Test
    public void randomUUIDTransfer() {
        API api = new API();
        String username = uniqueUsername();
        assertTrue(api.register(username, "Pa$$w0rdPa$$w0rd"));
        assertTrue(api.login(username, "Pa$$w0rdPa$$w0rd"));

        String src = UUID.randomUUID().toString();
        String dest = UUID.randomUUID().toString();
        api.transfer(src, dest, "100.00");
        Assertions.assertEquals("Source account does not exist.", api.getResult());
    }

    // Independent usernames keep registration tests repeatable.
    private static String uniqueUsername() {
        return "Test" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    @Test void singleDecimalDigitIsTenths() {
        API api = new API();
        assertEquals(1250L, api.parseMoney("12.5"));
        assertEquals(1200L, api.parseMoney("12.0"));
    }

    @Test void negativeFractionKeepsItsSign() {
        assertEquals(-50L, new API().parseMoney("-0.50"));
    }

    @Test void malformedAndOverflowAmountsAreRejected() {
        API api = new API();
        assertEquals(0L, api.parseMoney("12.3.4"));
        assertEquals(0L, api.parseMoney("9223372036854775808.00"));
        assertEquals(0L, api.parseMoney(null));
    }

    private AccountInfo savedAccount() {
        AccountInfo account = new AccountInfo(UUID.randomUUID(), 1234);
        account.setBalance(10000);
        TestAccounts.save(account);
        return account;
    }

    private API loggedIn(AccountInfo account) {
        User owner = new AccountRepo().findUserById(account.getUserID());
        API api = new API();
        assertTrue(api.login(owner.getName(), TestAccounts.TEST_PASSWORD));
        return api;
    }

    @Test void incorrectPinIsLoggedWithoutChangingBalance() {
        AccountInfo account = savedAccount();
        API api = loggedIn(account);
        Logger logger = (Logger) LoggerFactory.getLogger(API.class);
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
        try {
            api.withdraw(account.getAccountID().toString(), "10.00", "9876");
            assertEquals("Incorrect PIN", api.getResult());
            assertEquals(10000L, new AccountInfo(account.getAccountID()).getBalance());
            assertTrue(account.getTransactions().isEmpty());
            assertTrue(events.list.stream().anyMatch(e -> e.getLevel() == Level.ERROR
                    && e.getFormattedMessage().contains("incorrect PIN")));
            assertTrue(events.list.stream().noneMatch(e -> e.getFormattedMessage().contains("9876")));
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }

    @Test void foreignAccountDepositIsRejectedAndLogged() {
        AccountInfo ownerAccount = savedAccount();
        AccountInfo otherAccount = savedAccount();
        API api = loggedIn(ownerAccount);
        Logger logger = (Logger) LoggerFactory.getLogger(API.class);
        ListAppender<ILoggingEvent> events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
        try {
            api.deposit(otherAccount.getAccountID().toString(), "10.00");
            assertEquals("You do not own the destination account.", api.getResult());
            assertEquals(10000L, new AccountInfo(otherAccount.getAccountID()).getBalance());
            assertTrue(events.list.stream().anyMatch(e -> e.getLevel() == Level.ERROR
                    && e.getFormattedMessage().contains("ownership")));
        } finally {
            logger.detachAppender(events);
            events.stop();
        }
    }

    @Test void negativeFractionDoesNotBecomeDeposit() {
        AccountInfo account = savedAccount();
        API api = loggedIn(account);
        api.deposit(account.getAccountID().toString(), "-0.50");
        assertEquals(10000L, new AccountInfo(account.getAccountID()).getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test void limitedHistoryShowsOnlyAvailableFormattedRecords() {
        AccountInfo account = savedAccount();
        API api = loggedIn(account);
        api.deposit(account.getAccountID().toString(), "12.5");
        api.getTransactions(100);
        assertEquals(1L, api.getResult().lines().count());
        assertTrue(api.getResult().contains("DEPOSIT $12.50"));
        assertFalse(api.getResult().contains("null"));
    }

    @Test void emptyHistoryDoesNotInventRows() {
        API api = loggedIn(savedAccount());
        api.getTransactions(100);
        assertTrue(api.getResult().isEmpty());
    }
}
