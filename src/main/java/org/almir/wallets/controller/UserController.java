package org.almir.wallets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.UserRequestDTO;
import org.almir.wallets.dto.UserResponseDTO;
import org.almir.wallets.entity.User;
import org.almir.wallets.mapper.UserMapper;
import org.almir.wallets.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Operations related to user management")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/create")
    @Operation(summary = "Create a user",
            description = "Creates a new user with the given data")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<UserResponseDTO> createUser(
            @Parameter(description = "User creation request") @Valid @RequestBody UserRequestDTO request) {
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.name(),
                request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDto(user));
    }

    @GetMapping
    @Operation(summary = "Get all users",
            description = "Returns a paginated list of users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    })
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        Page<User> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users.map(userMapper::toResponseDto));
    }

    @PutMapping("/update/{userId}")
    @Operation(summary = "Update user",
            description = "Updates user details by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "User ID to update") @PathVariable Long userId,
            @Parameter(description = "User update data") @Valid @RequestBody UserRequestDTO request
    ) {
        User user = userService.updateUser(
                userId,
                request.email(),
                request.password(),
                request.name(),
                request.role()
        );
        return ResponseEntity.ok(userMapper.toResponseDto(user));
    }

    @DeleteMapping("/delete/{userId}")
    @Operation(summary = "Delete user", description = "Deletes a user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID to delete") @PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
