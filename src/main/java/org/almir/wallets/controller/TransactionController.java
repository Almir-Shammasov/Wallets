package org.almir.wallets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Transaction API", description = "Operations related to card transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final SecurityUtils securityUtils;

    @PostMapping("/transfer")
    @Operation(summary = "Transfer money", description = "Transfers money from one card to another")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transfer completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Unauthorized operation")
    })
    public ResponseEntity<TransactionResponseDTO> transfer(
            @Parameter(description = "Transfer request details") @Valid @RequestBody TransferRequestDTO transferRequest) {
        Transaction transaction = transactionService.transfer(
                transferRequest.sourceCardId(),
                transferRequest.targetCardId(),
                transferRequest.amount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toResponseDto(transaction));
    }

    @PostMapping("/withdraw")
    @Operation(summary = "Withdraw money", description = "Withdraws money from a card")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Withdrawal successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request or insufficient funds")
    })
    public ResponseEntity<TransactionResponseDTO> withdraw(
            @Parameter(description = "Withdraw request details") @Valid @RequestBody WithdrawRequestDTO withdrawRequest) {
        Transaction transaction = transactionService.withdraw(
                withdrawRequest.cardId(),
                withdrawRequest.amount()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(transactionMapper.toResponseDto(transaction));
    }

    @GetMapping
    @Operation(summary = "Get all transactions",
            description = "Retrieve paginated list of transactions for the current user or admin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions returned")
    })
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        User currentUser = securityUtils.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size);
        long userId = currentUser.getId();
        Role role = currentUser.getRole();

        List<TransactionResponseDTO> transactions = transactionService.getAllTransactions(
                userId, role.name(), pageable);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/card/{cardNumber}")
    @Operation(summary = "Get transactions by card number",
            description = "Retrieve all transactions for a specific card")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of transactions returned"),
            @ApiResponse(responseCode = "403", description = "Access denied for this card")
    })
    public ResponseEntity<List<TransactionResponseDTO>> getTransactionsByCardNumber(
            @Parameter(description = "Card number") @PathVariable String cardNumber) {
        User currentUser = securityUtils.getCurrentUser();
        long userId = currentUser.getId();
        String role = currentUser.getRole().name();

        List<TransactionResponseDTO> transactions = transactionService.getTransactionsByCardNumber(
                cardNumber, userId, role);
        return ResponseEntity.ok(transactions);
    }
}
