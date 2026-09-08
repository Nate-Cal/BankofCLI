package com.revature.CLIBank.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction {
    private UUID transactionId;
    private UUID sourceAccountId;
    private UUID destinationAccountId;
    private TransactionType type;
    private double amount;
    private LocalDateTime timestamp;

    public Transaction(UUID sourceAccountId, UUID destinationAccountId, TransactionType type, double amount) {
        this.transactionId = UUID.randomUUID(); 
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

    public Transaction(UUID sourceAccountId, TransactionType type, double amount) {
        this.transactionId = UUID.randomUUID();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = null;
        this.type = type;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
    }

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


