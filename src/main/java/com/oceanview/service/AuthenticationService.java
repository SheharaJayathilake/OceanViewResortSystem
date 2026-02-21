package com.oceanview.service;

import com.oceanview.dao.impl.UserDAOImpl;
import com.oceanview.model.User;
import com.oceanview.exception.AuthenticationException;
import com.oceanview.exception.DAOException;
import com.oceanview.util.PasswordUtil;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authentication Service handling user login and session management
 * Implements business rules for security and authentication
 */
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());
    private final UserDAOImpl userDAO;
    
    public AuthenticationService(UserDAOImpl userDAO) {
        this.userDAO = userDAO;
    }
    
    /**
     * Authenticate user with username and password
     * @param username Username
     * @param password Plain text password
     * @return Authenticated user object
     * @throws AuthenticationException if authentication fails
     */
    public User authenticate(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty()) {
            throw new AuthenticationException("Username is required");
        }
        
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required");
        }
        
        try {
            Optional<User> userOptional = userDAO.findByUsername(username.trim());
            
            if (!userOptional.isPresent()) {
                LOGGER.warning("Failed login attempt for username: " + username);
                throw new AuthenticationException("Invalid username or password");
            }
            
            User user = userOptional.get();
            
            // Check if user is active
            if (!user.isActive()) {
                LOGGER.warning("Inactive user login attempt: " + username);
                throw new AuthenticationException("Account is inactive. Contact administrator.");
            }
            
            // Verify password (using simple MD5 for compatibility with sample data)
            String hashedPassword = PasswordUtil.md5Hash(password);
            
            if (!hashedPassword.equals(user.getPasswordHash())) {
                LOGGER.warning("Invalid password for user: " + username);
                throw new AuthenticationException("Invalid username or password");
            }
            
            // Update last login timestamp
            userDAO.updateLastLogin(user.getUserId());
            
            LOGGER.info("User authenticated successfully: " + username);
            return user;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Database error during authentication", e);
            throw new AuthenticationException("Authentication failed due to system error");
        }
    }
    
    /**
     * Validate user session
     * @param user User object from session
     * @return true if session is valid
     */
    public boolean validateSession(User user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }
        
        try {
            Optional<User> currentUser = userDAO.findById(user.getUserId());
            return currentUser.isPresent() && currentUser.get().isActive();
        } catch (DAOException e) {
            LOGGER.log(Level.WARNING, "Error validating session", e);
            return false;
        }
    }
    
    /**
     * Check if user has specific permission
     * @param user User object
     * @param permission Permission to check
     * @return true if user has permission
     */
    public boolean hasPermission(User user, String permission) {
        if (user == null || permission == null) {
            return false;
        }
        return user.hasPermission(permission);
    }
    
    /**
     * Change user password
     * @param userId User ID
     * @param oldPassword Old password
     * @param newPassword New password
     * @throws AuthenticationException if password change fails
     */
    public void changePassword(Integer userId, String oldPassword, String newPassword) 
            throws AuthenticationException {
        
        if (!PasswordUtil.isStrongPassword(newPassword)) {
            throw new AuthenticationException(
                "Password must be at least 8 characters with uppercase, lowercase, digit, and special character");
        }
        
        try {
            Optional<User> userOptional = userDAO.findById(userId);
            
            if (!userOptional.isPresent()) {
                throw new AuthenticationException("User not found");
            }
            
            User user = userOptional.get();
            String oldPasswordHash = PasswordUtil.md5Hash(oldPassword);
            
            if (!oldPasswordHash.equals(user.getPasswordHash())) {
                throw new AuthenticationException("Current password is incorrect");
            }
            
            String newPasswordHash = PasswordUtil.md5Hash(newPassword);
            userDAO.updatePassword(userId, newPasswordHash);
            
            LOGGER.info("Password changed for user: " + userId);
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error changing password", e);
            throw new AuthenticationException("Failed to change password");
        }
    }
}
