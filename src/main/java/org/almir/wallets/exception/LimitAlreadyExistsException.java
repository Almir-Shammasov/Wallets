package org.almir.wallets.exception;

public class LimitAlreadyExistsException extends RuntimeException {
    public LimitAlreadyExistsException(String message) {
        super(message);
    }
}
