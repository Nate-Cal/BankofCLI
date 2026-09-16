package com.revature.CLIBank.BusinessLogic;

import com.revature.CLIBank.API.API;
import com.revature.CLIBank.Repository.*;
import com.revature.CLIBank.Utility.ConnectionFactory;
import com.revature.CLIBank.model.*;
import com.revature.CLIBank.TestAccounts;
import ch.qos.logback.classic.*;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

class HistoryIntegrationTest {
    private final AccountRepo repo = new AccountRepo();
    private final BankTransactions bank = new BankTransactions();
    private AccountInfo account;
    private User owner;
    private Logger logger;
    private ListAppender<ILoggingEvent> events;

    @BeforeEach void setup() {
        account = new AccountInfo(UUID.randomUUID(), 1234);
        account.setBalance(10000);
        TestAccounts.save(account);
        owner = repo.findUserById(account.getUserID());
        logger = (Logger) LoggerFactory.getLogger(BankLog.class);
        events = new ListAppender<>();
        events.start();
        logger.addAppender(events);
    }
    @AfterEach void cleanup() {
        logger.detachAppender(events);
        events.stop();
    }
    @Test void depositSurvivesReloadWithHistory() {
        assertEquals(1250, bank.deposit(account, 1250));
        assertEquals(11250, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(TransactionType.DEPOSIT, account.getTransactions().getFirst().getType());
        assertEquals(1250, account.getTransactions().getFirst().getAmount());
    }
    @Test void withdrawalSurvivesReloadWithHistory() {
        assertEquals(2500, bank.withdraw(account, 2500));
        assertEquals(7500, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(TransactionType.WITHDRAWAL, account.getTransactions().getFirst().getType());
    }
    @Test void rejectedWithdrawalCreatesNoHistory() {
        assertEquals(0, bank.withdraw(account, 10001));
        assertEquals(10000, new AccountInfo(account.getAccountID()).getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }
    @Test void frozenDepositCreatesNoHistory() {
        account.setFrozen(true);
        repo.updateAccount(account);
        assertEquals(0, bank.deposit(account, 100));
        assertTrue(account.getTransactions().isEmpty());
    }
    @Test void staleBalanceCannotOverwriteNewerDeposit() {
        AccountInfo stale = new AccountInfo(account.getAccountID());
        assertEquals(100, bank.deposit(account, 100));
        assertEquals(0, bank.deposit(stale, 100));
        assertEquals(10100, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(1, account.getTransactions().size());
    }
    @Test void historyFailureRollsBackBalanceAndLogsFailure() throws Exception {
        // A temporary trigger simulates SQLite refusing the history write.
        try (Connection c = ConnectionFactory.getAutoCommitConnect(); Statement s = c.createStatement()) {
            s.execute("CREATE TRIGGER fail_history BEFORE INSERT ON Transactions BEGIN SELECT RAISE(ABORT, 'test outage'); END;");
            try {
                assertEquals(0, bank.deposit(account, 100));
                assertEquals(10000, account.getBalance());
                assertEquals(10000, new AccountInfo(account.getAccountID()).getBalance());
                assertTrue(account.getTransactions().isEmpty());
                assertTrue(events.list.stream().noneMatch(e -> e.getLevel() == Level.INFO));
            } finally { s.execute("DROP TRIGGER fail_history"); }
        }
    }
    @Test void repositorySavePositive() {
        assertTrue(repo.saveBalanceAndHistory(account, 10100,
                new Transaction(account.getAccountID(), TransactionType.DEPOSIT, 100)));
        assertEquals(10100, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(1, account.getTransactions().size());
    }
    @Test void repositorySaveMissingAccountIsRejected() {
        AccountInfo missing = new AccountInfo(UUID.randomUUID(), 1234);
        assertFalse(repo.saveBalanceAndHistory(missing, 100,
                new Transaction(missing.getAccountID(), TransactionType.DEPOSIT, 100)));
    }
    @Test void overflowDepositRejected() {
        account.setBalance(Long.MAX_VALUE);
        repo.updateAccount(account);
        assertEquals(0, bank.deposit(account, 1));
        assertEquals(Long.MAX_VALUE, new AccountInfo(account.getAccountID()).getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }
    @Test void historyIsNewestFirstAndLimited() {
        bank.deposit(account, 100);
        bank.withdraw(account, 50);
        List<Transaction> history = new TransactionHistory().get(owner, account.getAccountID().toString(), 1);
        assertEquals(1, history.size());
        assertEquals(TransactionType.WITHDRAWAL, history.getFirst().getType());
    }
    @Test void historyRejectsAnotherUsersAccount() {
        User other = new User(UUID.randomUUID(), "Other", 20, "fixture");
        assertThrows(IllegalArgumentException.class,
                () -> new TransactionHistory().get(other, account.getAccountID().toString(), 10));
        assertEquals(Level.ERROR, events.list.getLast().getLevel());
    }
    @Test void historyRequiresLogin() {
        assertThrows(IllegalArgumentException.class, () -> new TransactionHistory().get(null, null, 10));
    }
    @Test void emptyHistoryIsSuccessful() {
        assertTrue(new TransactionHistory().get(owner, null, 10).isEmpty());
        assertEquals(Level.INFO, events.list.getLast().getLevel());
    }
    @Test void ownTransferAppearsOnceInCombinedHistory() {
        AccountInfo second = new AccountInfo(owner.getUserID(), 4321);
        AccountRepo.insertAccount(second);
        assertEquals(100, bank.transfer(account, second, 100));
        assertEquals(1, new TransactionHistory().get(owner, null, 10).size());
        assertEquals(100, new AccountInfo(second.getAccountID()).getBalance());
    }
    @Test void historyDatabaseFailureDoesNotBecomeEmptySuccess() {
        try (var mocked = org.mockito.Mockito.mockConstruction(AccountRepo.class,
                (r, context) -> org.mockito.Mockito.when(r.findAccountsByUserId(owner.getUserID()))
                        .thenThrow(new RepositoryException(new SQLException("outage"))))) {
            assertThrows(RepositoryException.class, () -> new TransactionHistory().get(owner, null, 10));
            assertEquals(Level.ERROR, events.list.getLast().getLevel());
        }
    }
    @Test void apiShowsReadableHistoryWithoutNullPadding() {
        bank.deposit(account, 1250);
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.getTransactions(100);
        assertTrue(api.getResult().contains("DEPOSIT $12.50"));
        assertFalse(api.getResult().contains("null"));
        assertEquals(1, api.getResult().lines().count());
    }
    @Test void apiLoginSuccessThenFailureClearsSession() {
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        assertFalse(api.login(owner.getName(), "wrong"));
        assertNull(api.getUser());
        assertTrue(events.list.stream().anyMatch(e -> e.getFormattedMessage().equals("LOGIN succeeded")));
        assertEquals(Level.ERROR, events.list.getLast().getLevel());
    }
    @Test void apiRejectsForeignDeposit() {
        API api = new API();
        api.deposit(account.getAccountID().toString(), "1.00");
        assertEquals(10000, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(Level.ERROR, events.list.getLast().getLevel());
    }
    @Test void registrationLogsSuccessAndRejectedInputWithoutPassword() {
        API api = new API();
        String name = "User" + UUID.randomUUID().toString().substring(0,8);
        String secret = "ValidPassword$2026";
        assertTrue(api.register(name, secret));
        assertFalse(api.register("x", secret));
        assertTrue(events.list.stream().anyMatch(e -> e.getFormattedMessage().equals("REGISTRATION succeeded")));
        assertEquals(Level.ERROR, events.list.getLast().getLevel());
        assertTrue(events.list.stream().noneMatch(e -> e.getFormattedMessage().contains(secret)));
    }
    @Test void apiDatabaseFailureIsFriendlyAndLogged() {
        API api = new API();
        try (var mocked = org.mockito.Mockito.mockConstruction(AccountRepo.class,
                (r, context) -> org.mockito.Mockito.when(r.findUserByNameAndPassword("name", "password"))
                        .thenThrow(new RepositoryException(new SQLException("outage"))))) {
            assertFalse(api.login("name", "password"));
            assertEquals("Service unavailable. Please try again.", api.getResult());
            assertEquals(Level.ERROR, events.list.getLast().getLevel());
        }
    }

    @Test void staleTransferCannotOverdrawStoredBalance() {
        AccountInfo stale = new AccountInfo(account.getAccountID());
        AccountInfo second = new AccountInfo(owner.getUserID(), 4321);
        AccountRepo.insertAccount(second);
        assertEquals(9000, bank.withdraw(account, 9000));
        assertEquals(0, bank.transfer(stale, second, 5000));
        assertEquals(1000, new AccountInfo(account.getAccountID()).getBalance());
        assertEquals(0, new AccountInfo(second.getAccountID()).getBalance());
        assertEquals(1, account.getTransactions().size());
    }
    @Test void transferHistoryFailureRollsBackBothBalances() throws Exception {
        AccountInfo second = new AccountInfo(owner.getUserID(), 4321);
        AccountRepo.insertAccount(second);
        try (Connection c = ConnectionFactory.getAutoCommitConnect(); Statement stmt = c.createStatement()) {
            stmt.execute("CREATE TRIGGER fail_transfer BEFORE INSERT ON Transactions BEGIN SELECT RAISE(ABORT, 'test outage'); END;");
            try {
                assertEquals(0, bank.transfer(account, second, 100));
                assertEquals(10000, new AccountInfo(account.getAccountID()).getBalance());
                assertEquals(0, new AccountInfo(second.getAccountID()).getBalance());
                assertTrue(account.getTransactions().isEmpty());
            } finally { stmt.execute("DROP TRIGGER fail_transfer"); }
        }
    }
    @Test void apiOneDecimalDepositSavesCorrectCents() {
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.deposit(account.getAccountID().toString(), "12.5");
        assertEquals(11250, new AccountInfo(account.getAccountID()).getBalance());
    }
    @Test void apiNegativeFractionDoesNotBecomePositiveDeposit() {
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.deposit(account.getAccountID().toString(), "-0.50");
        assertEquals(10000, new AccountInfo(account.getAccountID()).getBalance());
        assertTrue(account.getTransactions().isEmpty());
    }
    @Test void apiTransferPersistsAndShowsResult() {
        AccountInfo second = new AccountInfo(owner.getUserID(), 4321);
        AccountRepo.insertAccount(second);
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.transfer(account.getAccountID().toString(), second.getAccountID().toString(), "10.00");
        assertEquals("Transfer successful.", api.getResult());
        assertEquals(1000, new AccountInfo(second.getAccountID()).getBalance());
    }

    @Test void accountCreateAndDeleteLogActualOutcomes() {
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.creatAcct("4321", "savings");
        assertTrue(events.list.stream().anyMatch(e -> e.getFormattedMessage().equals("ACCOUNT_CREATE succeeded")));
        AccountInfo created = repo.findAccountsByUserId(owner.getUserID()).stream()
                .filter(a -> !a.getAccountID().equals(account.getAccountID())).findFirst().orElseThrow();
        api.deleteAcct(created.getAccountID().toString(), "wrong");
        assertNotNull(repo.findAccountById(created.getAccountID()));
        assertEquals(Level.ERROR, events.list.getLast().getLevel());
        api.deleteAcct(created.getAccountID().toString(), "4321");
        assertNull(repo.findAccountById(created.getAccountID()));
        assertEquals("ACCOUNT_DELETE succeeded", events.list.getLast().getFormattedMessage());
    }
    @Test void rejectedAccountCreationLogsError() {
        API api = new API();
        assertTrue(api.login(owner.getName(), owner.getPassword()));
        api.creatAcct("1234", "invalid");
        assertEquals("ACCOUNT_CREATE failed or was rejected", events.list.getLast().getFormattedMessage());
    }
    @Test void ignoredRegistrationInsertCannotLogSuccess() throws Exception {
        API api = new API();
        try (Connection c = ConnectionFactory.getAutoCommitConnect(); Statement stmt = c.createStatement()) {
            stmt.execute("CREATE TRIGGER ignore_owner BEFORE INSERT ON Owners BEGIN SELECT RAISE(IGNORE); END;");
            try {
                assertFalse(api.register("IgnoredUser", "ValidPassword$2026"));
                assertEquals("Service unavailable. Please try again.", api.getResult());
                assertTrue(events.list.stream().noneMatch(e -> e.getFormattedMessage().equals("REGISTRATION succeeded")));
            } finally { stmt.execute("DROP TRIGGER ignore_owner"); }
        }
    }
}
