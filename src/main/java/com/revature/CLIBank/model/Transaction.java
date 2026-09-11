package com.revature.CLIBank.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Transaction for a deposit, withdrawal, or transfer.
 * Maps to the Transactions table
 */
public class Transaction {
    private UUID transactionId;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;

    /**
     * Creates a new transfer between two accounts.
     * Generates a unique ID and sets the timestamp to now.
     *
     * @param sourceAccountId account money leaves
     * @param destinationAccountId account money enters
     * @param type should be transfer
     * @param amount amount moved
     */
    public Transaction(UUID sourceAccountId, UUID destinationAccountId, TransactionType type, double amount) {
        this.transactionId = UUID.randomUUID(); 
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Creates a new deposit or withdrawal on a single account.
     * Generates a unique ID, sets destination and timestamps now.
     * @param sourceAccountId account being deposited to or withdrawn from
     * @param type Type of transaction
     * @param amount amount moved
     */
    public Transaction(UUID sourceAccountId, TransactionType type, double amount) {
        this.transactionId = UUID.randomUUID();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = null;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Rebuilds a transaction from an existing database row.
     * @param transactionId the ID already stored in Transactions
     * @param sourceAccountId account money left
     * @param destinationAccountId account money entered
     * @param type deposit, withdrawal, or transfer
     * @param amount amount moved
     * @param timestamp when the transaction occurred
     */
    public Transaction(UUID transactionId, UUID sourceAccountId, UUID destinationAccountId, TransactionType type, double amount, LocalDateTime timestamp) {
        this.transactionId = transactionId;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(UUID sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public UUID getDestinationAccountId() {
        return destinationAccountId;
    }

    public void setDestinationAccountId(UUID destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }     
 
}



