package com.revature.CLIBank.Repository;

// Lets callers distinguish a database failure from an empty result
public class RepositoryException extends RuntimeException {
    public RepositoryException(Throwable cause) {
        super("Database operation failed", cause);
    }
}
