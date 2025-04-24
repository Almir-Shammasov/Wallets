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
     */
    User registerUser(String email, String password, String name, Role role);

    /**
     * Retrieves a paginated list of users.
     *
     * @param pageable the pagination information (page number, size, sorting)
     * @return a Page of User entities
     */
    Page<User> getUsers(Pageable pageable);
    /**
     * Updates an existing user with the provided details.
     *
     * @param userId the ID of the user to update
     * @param email the new email address for the user
     * @param password the new password for the user
     * @param name the new name for the user
     * @param role the new role for the user
     * @return the updated User entity
     */
    User updateUser(Long userId, String email, String password, String name, Role role);

    /**
     * Deletes a user by their ID.
     *
     * @param userId the ID of the user to delete
     */
    void deleteUser(Long userId);
}
