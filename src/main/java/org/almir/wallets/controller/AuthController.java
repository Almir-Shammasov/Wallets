package org.almir.wallets.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.almir.wallets.dto.AuthRequestDTO;
import org.almir.wallets.dto.AuthResponseDTO;
import org.almir.wallets.dto.RegisterRequestDTO;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.security.JwtUtil;
import org.almir.wallets.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication API",
        description = "Endpoints for user login and registration")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "Login a user and generate JWT token",
            description = "Authenticate a user by email and password, and return a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully logged in, JWT token returned"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public AuthResponseDTO login(
            @Parameter(description = "User login credentials") @RequestBody AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.email());
        final String jwt = jwtUtil.generateToken(userDetails.getUsername(),
                userDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority()
                    .substring(5));

        return new AuthResponseDTO(jwt, userDetails.getUsername());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user and generate JWT token",
            description = "Register a new user, save to the system, and return a JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successfully registered, JWT token returned"),
            @ApiResponse(responseCode = "400", description = "Invalid registration data")
    })
    public AuthResponseDTO register(
            @Parameter(description = "User registration details") @Valid @RequestBody RegisterRequestDTO request) {
        User user = userService.registerUser(
                request.email(),
                request.password(),
                request.name(),
                Role.USER
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        final String jwt = jwtUtil.generateToken(
                userDetails.getUsername(),
                userDetails.getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .substring(5));

        return new AuthResponseDTO(jwt, userDetails.getUsername());
    }
}
