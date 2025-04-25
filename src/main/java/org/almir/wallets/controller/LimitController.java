package org.almir.wallets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.LimitRequestDTO;
import org.almir.wallets.dto.LimitResponseDTO;
import org.almir.wallets.entity.Limit;
import org.almir.wallets.mapper.LimitMapper;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.LimitService;
import org.almir.wallets.utils.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
@Tag(name = "Limit API", description = "Operations related to card transaction limits")
public class LimitController {
    private final LimitService limitService;
    private final LimitMapper limitMapper;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(summary = "Create a new limit",
            description = "Create a limit for a specific card. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Limit successfully created"),
            @ApiResponse(responseCode = "403", description = "Forbidden — only admins can perform this action")
    })
    public ResponseEntity<LimitResponseDTO> createLimit(
            @Parameter(description = "Limit creation DTO") @Valid @RequestBody LimitRequestDTO request) {
        securityUtils.checkAdminRole();

        Limit limit = limitService.createLimit(
                request.cardId(),
                request.type().name(),
                request.amount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(limitMapper.toResponseDto(limit));
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "Get limits by card ID",
            description = "Retrieve all limits set for a specific card")
    @ApiResponse(responseCode = "200", description = "List of limits retrieved successfully")
    public ResponseEntity<List<LimitResponseDTO>> getLimits(
            @Parameter(description = "Card ID") @PathVariable long cardId) {
        List<Limit> limits = limitService.getLimits(cardId);
        return ResponseEntity.ok(limits.stream().map(limitMapper::toResponseDto).toList());
    }

    @PutMapping("/{limitId}")
    @Operation(summary = "Update limit amount",
            description = "Update the amount of an existing limit. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Limit successfully updated"),
            @ApiResponse(responseCode = "403", description = "Forbidden — only admins can perform this action")
    })
    public ResponseEntity<LimitResponseDTO> updateLimit(
            @Parameter(description = "Limit ID to update") @PathVariable long limitId,
            @Parameter(description = "New limit request data") @RequestBody LimitRequestDTO request
    ) {
        securityUtils.checkAdminRole();

        Limit limit = limitService.updateLimit(limitId, request.amount());
        return ResponseEntity.ok(limitMapper.toResponseDto(limit));
    }

    @DeleteMapping("/{limitId}")
    @Operation(summary = "Delete a limit", description = "Remove a limit by its ID. Admin only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Limit successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Forbidden — only admins can perform this action")
    })
    public ResponseEntity<Void> deleteLimit(
            @Parameter(description = "Limit ID to delete") @PathVariable Long limitId) {
        securityUtils.checkAdminRole();

        limitService.deleteLimit(limitId);
        return ResponseEntity.noContent().build();
    }
}
