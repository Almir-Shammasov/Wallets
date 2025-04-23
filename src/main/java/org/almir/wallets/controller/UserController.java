package org.almir.wallets.controller;

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
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.name(),
                request.role()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(Pageable pageable) {
        Page<User> users = userService.getUsers(pageable);
        return ResponseEntity.ok(users.map(userMapper::toResponseDto));
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDTO request
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
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
