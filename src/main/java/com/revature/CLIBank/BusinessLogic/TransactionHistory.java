package com.revature.CLIBank.BusinessLogic;

import com.revature.CLIBank.model.*;
import com.revature.CLIBank.Repository.RepositoryException;
import java.util.*;

/** Reads history for the signed-in user's accounts, newest first */
public class TransactionHistory {
    public List<Transaction> get(User user, String accountId, int rows) {
        try {
            if (user == null) throw new IllegalArgumentException("Please log in first.");
            List<AccountInfo> accounts = user.getAccounts();
            if (accountId != null) {
                UUID id = UUID.fromString(accountId);
                accounts = accounts.stream().filter(a -> id.equals(a.getAccountID())).toList();
                if (accounts.isEmpty()) throw new IllegalArgumentException("You do not own that account.");
            }
            // Transfers between two of your accounts should appear only once.
            Map<UUID, Transaction> unique = new HashMap<>();
            for (AccountInfo account : accounts) {
                for (Transaction transaction : account.getTransactions()) {
                    unique.put(transaction.getTransactionId(), transaction);
                }
            }
            List<Transaction> result = new ArrayList<>(unique.values());
            result.sort(Comparator.comparing(Transaction::getTimestamp).reversed()
                    .thenComparing(Transaction::getTransactionId));
            if (rows >= 0 && rows < result.size()) result = new ArrayList<>(result.subList(0, rows));
            BankLog.outcome(BankLog.Event.TRANSACTION_HISTORY, true, null);
            return result;
        } catch (RepositoryException | IllegalArgumentException e) {
            BankLog.outcome(BankLog.Event.TRANSACTION_HISTORY, false, null);
            throw e;
        }
    }
}
