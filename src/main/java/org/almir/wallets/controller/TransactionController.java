package org.almir.wallets.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.dto.TransferRequestDTO;
import org.almir.wallets.dto.WithdrawRequestDTO;
import org.almir.wallets.entity.Transaction;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.mapper.TransactionMapper;
import org.almir.wallets.service.TransactionService;
import org.almir.wallets.utils.SecurityUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final SecurityUtils securityUtils;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> transfer(@Valid @RequestBody TransferRequestDTO transferRequest) {
        Transaction transaction = transactionService.transfer(
                transferRequest.sourceCardId(),
                transferRequest.targetCardId(),
                transferRequest.amount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toResponseDto(transaction));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponseDTO> withdraw(@Valid @RequestBody WithdrawRequestDTO withdrawRequest) {
        Transaction transaction = transactionService.withdraw(
                withdrawRequest.cardId(),
                withdrawRequest.amount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toResponseDto(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = securityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        long userId = currentUser.getId();
        Role role = currentUser.getRole();

        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(
                userId, role.name(), pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/card/{cardNumber}")
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByCardNumber(
            @PathVariable String cardNumber) {
        User currentUser = securityUtils.getCurrentUser();
        long userId = currentUser.getId();
        String role = currentUser.getRole().name();

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByCardNumber(
                cardNumber, userId, role);
        return ResponseEntity.ok(transactions);
    }
}
