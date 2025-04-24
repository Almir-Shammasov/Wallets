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
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final UserRepository userRepository;

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
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

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
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        long userId = currentUser.getId();
        String role = currentUser.getRole().name();
        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByCardNumber(
                cardNumber, userId, role);
        return ResponseEntity.ok(transactions);
    }
}
