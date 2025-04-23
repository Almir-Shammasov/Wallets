package org.almir.wallets.service;

import org.almir.wallets.entity.User;
import org.almir.wallets.enums.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing user-related operations.
 */
public interface UserService {
    /**
     * Registers a new user in the system with the provided details.
     *
     * @param email    the email address of the user, must be unique
     * @param password the password for the user account
     * @param name     the full name of the user
     * @param role     the role assigned to the user (e.g., ADMIN, USER)
     * @return the created {@link User} entity
     * @throws IllegalArgumentException if the email is already registered
     */
    User registerUser(String email, String password, String name, Role role);

    Page<User> getUsers(Pageable pageable);
    User updateUser(Long userId, String email, String password, String name, Role role);
    void deleteUser(Long userId);
}
