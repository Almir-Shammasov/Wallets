package org.almir.wallets.exception;

public class OverLimitException extends RuntimeException {
    public OverLimitException(String message) {
        super(message);
    }
}
