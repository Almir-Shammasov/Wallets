package org.almir.wallets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException e) {
        return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCardNotFound(CardNotFoundException e) {
        return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidCardNumberException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCardNumber(InvalidCardNumberException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InvalidExpiryDateException.class)
    public ResponseEntity<Map<String, String>> handleInvalidExpiryDate(InvalidExpiryDateException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<Map<String, String>> handleInsufficientFunds(InsufficientFundsException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(OverLimitException.class)
    public ResponseEntity<Map<String, String>> handleLimitExceeded(OverLimitException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CardAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(CardAccessDeniedException e) {
        return errorResponse(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler(LimitAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleLimitAlreadyExistsException(LimitAlreadyExistsException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(LimitNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLimitNotFoundException(LimitNotFoundException e) {
        return errorResponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidCardStatusException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCardStatus(InvalidCardStatusException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UserWithEmailExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserWithEmailException(UserWithEmailExistsException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleCardAlreadyExistsException(CardAlreadyExistsException e) {
        return errorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, String>> errorResponse(HttpStatus status, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);

        return ResponseEntity.status(status).body(error);
    }
}
