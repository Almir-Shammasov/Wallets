package org.almir.wallets.exception;

public class LimitNotFoundException extends RuntimeException {
    public LimitNotFoundException(String message) {
        super(message);
    }
}
