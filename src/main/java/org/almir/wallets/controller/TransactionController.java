package org.almir.wallets.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.dto.TransferRequestDTO;
import org.almir.wallets.dto.WithdrawRequestDTO;
import org.almir.wallets.entity.Transaction;
import org.almir.wallets.mapper.TransactionMapper;
import org.almir.wallets.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

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
}
