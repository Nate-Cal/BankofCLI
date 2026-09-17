package com.revature.CLIBank;

import com.revature.CLIBank.Repository.AccountRepo;
import com.revature.CLIBank.Utility.PasswordEncryption;
import com.revature.CLIBank.model.AccountInfo;
import com.revature.CLIBank.model.User;

import java.util.UUID;

/**
 * Creates saved test accounts because successful transactions
 * require matching database records.
 */
public final class TestAccounts {

    // Original password used when logging into disposable test accounts.
    public static final String TEST_PASSWORD = "FixturePassword$2026";

    public static void save(AccountInfo account) {
        if (!"jdbc:sqlite:./data/test-bank.db"
                .equals(System.getenv("DATABASE-PATH"))) {
            throw new IllegalStateException(
                    "Run tests through Maven with the test database."
            );
        }

        AccountRepo repo = new AccountRepo();
        repo.initSchema();

        if (repo.findUserById(account.getUserID()) == null) {
            // This constructor accepts database-format data, so encrypt
            // the password before supplying it.
            User owner = new User(
                    account.getUserID(),
                    UUID.randomUUID().toString(),
                    21,
                    PasswordEncryption.encrypt(TEST_PASSWORD)
            );

            AccountRepo.insertUser(owner);
        }

        AccountRepo.insertAccount(account);
    }
}