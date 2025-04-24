package org.almir.wallets.exception;

public class UserWithEmailExistsException extends RuntimeException {
    public UserWithEmailExistsException(String message) {
        super(message);
    }
}
