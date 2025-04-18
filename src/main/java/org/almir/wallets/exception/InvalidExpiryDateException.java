package org.almir.wallets.exception;

public class InvalidExpiryDateException extends RuntimeException {
    public InvalidExpiryDateException(String message) {
        super(message);
    }
}
