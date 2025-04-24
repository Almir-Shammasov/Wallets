package org.almir.wallets.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.LimitRequestDTO;
import org.almir.wallets.dto.LimitResponseDTO;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.CardAccessDeniedException;
import org.almir.wallets.mapper.LimitMapper;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.LimitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
public class LimitController {
    private final LimitService limitService;
    private final LimitMapper limitMapper;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<LimitResponseDTO> createLimit(@Valid @RequestBody LimitRequestDTO request) {
        checkAdminRole();

        Limit limit = limitService.createLimit(
                request.cardId(),
                request.type().name(),
                request.amount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(limitMapper.toResponseDto(limit));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<List<LimitResponseDTO>> getLimits(
            @PathVariable long cardId) {
        List<Limit> limits = limitService.getLimits(cardId);
        return ResponseEntity.ok(limits.stream().map(limitMapper::toResponseDto).toList());
    }

    @PutMapping("/{limitId}")
    public ResponseEntity<LimitResponseDTO> updateLimit(
            @PathVariable long limitId,
            @RequestBody LimitRequestDTO request
    ) {
        checkAdminRole();

        Limit limit = limitService.updateLimit(limitId, request.amount());
        return ResponseEntity.ok(limitMapper.toResponseDto(limit));
    }

    @DeleteMapping("/{limitId}")
    public ResponseEntity<Void> deleteLimit(@PathVariable Long limitId) {
        checkAdminRole();

        limitService.deleteLimit(limitId);
        return ResponseEntity.noContent().build();
    }

    private void checkAdminRole() {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!currentUser.getRole().name().equals(Role.ADMIN.name())) {
            throw new CardAccessDeniedException("Only admin can manage limits");
        }
    }
}
