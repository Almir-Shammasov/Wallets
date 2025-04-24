package org.almir.wallets.service;

import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.UserNotFoundException;
import org.almir.wallets.exception.UserWithEmailExistsException;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void registerUser_success() {
        String rawPassword = "pass123";
        String encodedPassword = "encoded";

        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser("new@mail.com", rawPassword, "John", Role.USER);

        assertEquals("new@mail.com", user.getEmail());
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void registerUser_emailExists() {
        when(userRepository.findByEmail("existing@mail.com"))
                .thenReturn(Optional.of(User.builder().build()));

        assertThrows(UserWithEmailExistsException.class, () ->
                userService.registerUser("existing@mail.com", "pass", "John", Role.USER));
    }

    @Test
    void getUsers_success() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of()));

        Page<User> result = userService.getUsers(pageable);

        assertNotNull(result);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateUser_success() {
        Long userId = 1L;
        User user = User.builder()
                .id(userId)
                .email("old@mail.com")
                .password("oldpass")
                .name("Old")
                .role(Role.USER)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User updated = userService.updateUser(userId, "new@mail.com", "newpass", "New", Role.ADMIN);

        assertEquals("new@mail.com", updated.getEmail());
        assertEquals("encoded", updated.getPassword());
        assertEquals("New", updated.getName());
        assertEquals(Role.ADMIN, updated.getRole());
    }

    @Test
    void updateUser_userNotFound() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                userService.updateUser(42L, "email", "pass", "Name", Role.USER));
    }

    @Test
    void updateUser_emailExists() {
        Long userId = 1L;
        User existing = User.builder().id(userId).email("old@mail.com").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.of(User.builder().build()));

        assertThrows(UserWithEmailExistsException.class, () ->
                userService.updateUser(userId, "new@mail.com", "pass", "name", Role.USER));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
    }
}
