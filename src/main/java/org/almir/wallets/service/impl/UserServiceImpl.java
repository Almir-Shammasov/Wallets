package org.almir.wallets.service.impl;

import lombok.RequiredArgsConstructor;
import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.almir.wallets.exception.UserNotFoundException;
import org.almir.wallets.exception.UserWithEmailExistsException;
import org.almir.wallets.repository.UserRepository;
import org.almir.wallets.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User registerUser(String email, String password, String name, Role role) {
        if (userIsPresent(email)) {
            throw new UserWithEmailExistsException("Email already exist");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .role(role)
                .build();

        return userRepository.save(user);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<User> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public User updateUser(Long userId, String email, String password, String name, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!user.getEmail().equals(email) && userIsPresent(email)) {
            throw new UserWithEmailExistsException("Email already exists");
        }

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setName(name);
        user.setRole(role);

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }
        userRepository.deleteById(userId);
    }

    private boolean userIsPresent(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
