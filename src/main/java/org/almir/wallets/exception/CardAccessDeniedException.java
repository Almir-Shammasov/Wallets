package org.almir.wallets.exception;

public class CardAccessDeniedException extends RuntimeException {
    public CardAccessDeniedException(String message) {
        super(message);
    }
}
