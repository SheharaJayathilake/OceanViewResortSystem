package com.oceanview.service;

import com.oceanview.dao.impl.UserDAOImpl;
import com.oceanview.model.User;
import com.oceanview.model.User.UserRole;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;
import com.oceanview.util.PasswordUtil;
import com.oceanview.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/** Business logic for user management (admin-only operations) */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final UserDAOImpl userDAO;

    // Username: alphanumeric + underscores, 3-50 chars
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,50}$");

    public UserService(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }

    public List<User> getAllUsers() throws BusinessException {
        try {
            return userDAO.findAll();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            throw new BusinessException("Failed to retrieve users: " + e.getMessage());
        }
    }

    public User findUserById(Integer userId) throws BusinessException {
        if (userId == null || userId <= 0) {
            throw new BusinessException("Invalid user ID");
        }

        try {
            Optional<User> user = userDAO.findById(userId);

            if (!user.isPresent()) {
                throw new BusinessException("User not found with ID: " + userId);
            }

            return user.get();

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error finding user", e);
            throw new BusinessException("Failed to retrieve user: " + e.getMessage());
        }
    }

    public User createUser(User user, String plainPassword) throws BusinessException {
        validateUserForCreation(user);
        validatePassword(plainPassword);

        try {
            // Check for duplicate username
            Optional<User> existingUser = userDAO.findByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                throw new BusinessException("Username '" + user.getUsername() + "' is already taken");
            }

            // Check for duplicate email
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                Optional<User> emailUser = userDAO.findByEmail(user.getEmail());
                if (emailUser.isPresent()) {
                    throw new BusinessException("Email '" + user.getEmail() + "' is already in use");
                }
            }

            // Hash password (MD5 for compatibility with existing auth)
            String passwordHash = PasswordUtil.md5Hash(plainPassword);
            user.setPasswordHash(passwordHash);

            User createdUser = userDAO.create(user);
            LOGGER.info("New user created: " + createdUser.getUsername() + " with role " + createdUser.getRole());
            return createdUser;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error creating user", e);
            throw new BusinessException("Failed to create user: " + e.getMessage());
        }
    }

    public boolean updateUser(User user, Integer currentUserId) throws BusinessException {
        if (user.getUserId() == null) {
            throw new BusinessException("User ID is required for update");
        }

        validateUserForUpdate(user);

        try {
            // Verify user exists
            Optional<User> existingUser = userDAO.findById(user.getUserId());
            if (!existingUser.isPresent()) {
                throw new BusinessException("User not found with ID: " + user.getUserId());
            }

            User existing = existingUser.get();

            if (user.getUserId().equals(currentUserId) && !user.isActive()) {
                throw new BusinessException("You cannot deactivate your own account");
            }

            if (user.getUserId().equals(currentUserId) && user.getRole() != existing.getRole()) {
                throw new BusinessException("You cannot change your own role");
            }

            // Protect last active admin
            if (existing.getRole() == UserRole.ADMIN && existing.isActive()) {
                boolean demotingAdmin = user.getRole() != UserRole.ADMIN;
                boolean deactivatingAdmin = !user.isActive();

                if (demotingAdmin || deactivatingAdmin) {
                    long adminCount = userDAO.countByRole(UserRole.ADMIN);
                    if (adminCount <= 1) {
                        throw new BusinessException("Cannot modify the last active administrator. " +
                                "At least one active admin must exist.");
                    }
                }
            }

            // Check duplicate email
            if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
                Optional<User> emailUser = userDAO.findByEmail(user.getEmail());
                if (emailUser.isPresent() && !emailUser.get().getUserId().equals(user.getUserId())) {
                    throw new BusinessException("Email '" + user.getEmail() + "' is already in use by another user");
                }
            }

            // Username is immutable
            user.setUsername(existing.getUsername());

            boolean updated = userDAO.update(user);

            if (updated) {
                LOGGER.info("User updated: " + user.getUserId() + " by admin: " + currentUserId);
            }

            return updated;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error updating user", e);
            throw new BusinessException("Failed to update user: " + e.getMessage());
        }
    }

    public boolean deleteUser(Integer userId, Integer currentUserId) throws BusinessException {
        if (userId == null || userId <= 0) {
            throw new BusinessException("Invalid user ID for deletion");
        }

        if (userId.equals(currentUserId)) {
            throw new BusinessException("You cannot delete your own account");
        }

        try {
            Optional<User> existingUser = userDAO.findById(userId);
            if (!existingUser.isPresent()) {
                throw new BusinessException("User not found with ID: " + userId);
            }

            User user = existingUser.get();

            // Protect last active admin
            if (user.getRole() == UserRole.ADMIN && user.isActive()) {
                long adminCount = userDAO.countByRole(UserRole.ADMIN);
                if (adminCount <= 1) {
                    throw new BusinessException("Cannot delete the last active administrator");
                }
            }

            boolean deleted = userDAO.delete(userId);

            if (deleted) {
                LOGGER.info("User deleted: " + userId + " by admin: " + currentUserId);
            }

            return deleted;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user", e);
            throw new BusinessException(
                    "Failed to delete user. The user may have associated records (reservations or payments).");
        }
    }

    public void resetPassword(Integer userId, String newPassword) throws BusinessException {
        if (userId == null || userId <= 0) {
            throw new BusinessException("Invalid user ID for password reset");
        }

        validatePassword(newPassword);

        try {
            Optional<User> existingUser = userDAO.findById(userId);
            if (!existingUser.isPresent()) {
                throw new BusinessException("User not found with ID: " + userId);
            }

            String newPasswordHash = PasswordUtil.md5Hash(newPassword);
            boolean updated = userDAO.updatePassword(userId, newPasswordHash);

            if (updated) {
                LOGGER.info("Password reset for user: " + userId);
            } else {
                throw new BusinessException("Failed to reset password");
            }

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error resetting password", e);
            throw new BusinessException("Failed to reset password: " + e.getMessage());
        }
    }

    public boolean toggleUserStatus(Integer userId, Integer currentUserId) throws BusinessException {
        if (userId == null || userId <= 0) {
            throw new BusinessException("Invalid user ID");
        }

        if (userId.equals(currentUserId)) {
            throw new BusinessException("You cannot change your own active status");
        }

        try {
            Optional<User> existingUser = userDAO.findById(userId);
            if (!existingUser.isPresent()) {
                throw new BusinessException("User not found with ID: " + userId);
            }

            User user = existingUser.get();
            boolean newStatus = !user.isActive();

            // Protect last active admin
            if (user.getRole() == UserRole.ADMIN && user.isActive() && !newStatus) {
                long adminCount = userDAO.countByRole(UserRole.ADMIN);
                if (adminCount <= 1) {
                    throw new BusinessException("Cannot deactivate the last active administrator");
                }
            }

            user.setActive(newStatus);
            boolean updated = userDAO.update(user);

            if (updated) {
                String action = newStatus ? "activated" : "deactivated";
                LOGGER.info("User " + action + ": " + userId + " by admin: " + currentUserId);
            }

            return newStatus;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error toggling user status", e);
            throw new BusinessException("Failed to update user status: " + e.getMessage());
        }
    }

    public List<User> searchUsers(String searchTerm) throws BusinessException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BusinessException("Search term is required");
        }

        try {
            List<User> users = userDAO.searchUsers(searchTerm.trim());
            LOGGER.info("Found " + users.size() + " users matching: " + searchTerm);
            return users;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error searching users", e);
            throw new BusinessException("Failed to search users: " + e.getMessage());
        }
    }

    private void validateUserForCreation(User user) throws BusinessException {
        if (user == null) {
            throw new BusinessException("User information is required");
        }

        validateUsername(user.getUsername());
        validateFullName(user.getFullName());
        validateEmail(user.getEmail());

        if (user.getRole() == null) {
            throw new BusinessException("User role is required");
        }
    }

    private void validateUserForUpdate(User user) throws BusinessException {
        if (user == null) {
            throw new BusinessException("User information is required");
        }

        validateFullName(user.getFullName());
        validateEmail(user.getEmail());

        if (user.getRole() == null) {
            throw new BusinessException("User role is required");
        }
    }

    private void validateUsername(String username) throws BusinessException {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("Username is required");
        }

        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            throw new BusinessException(
                    "Username must be 3-50 characters, containing only letters, numbers, and underscores");
        }
    }

    private void validateFullName(String fullName) throws BusinessException {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new BusinessException("Full name is required");
        }

        if (fullName.trim().length() < 2) {
            throw new BusinessException("Full name must be at least 2 characters");
        }
    }

    private void validateEmail(String email) throws BusinessException {
        if (email != null && !email.trim().isEmpty()) {
            if (!ValidationUtil.isValidEmail(email)) {
                throw new BusinessException("Invalid email format");
            }
        }
    }

    private void validatePassword(String password) throws BusinessException {
        if (password == null || password.isEmpty()) {
            throw new BusinessException("Password is required");
        }

        if (!PasswordUtil.isStrongPassword(password)) {
            throw new BusinessException(
                    "Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
        }
    }
}
