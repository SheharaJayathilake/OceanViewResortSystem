package com.oceanview.model;

import java.util.Objects;

/**
 * User entity representing system users with role-based access
 * Implements security best practices for credential management
 */
public class User extends BaseModel {
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private UserRole role;
    private boolean isActive;

    public enum UserRole {
        ADMIN, RECEPTIONIST, MANAGER
    }

    public User() {
        super();
        this.isActive = true;
        this.role = UserRole.RECEPTIONIST;
    }

    public User(String username, String fullName, String email, UserRole role) {
        this();
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.username = username.trim();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty");
        }
        this.fullName = fullName.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role != null ? role : UserRole.RECEPTIONIST;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean hasPermission(String permission) {
        switch (this.role) {
            case ADMIN:
                return true;
            case MANAGER:
                return !permission.equals("DELETE_USER");
            case RECEPTIONIST:
                return permission.matches("(VIEW|CREATE|UPDATE)_RESERVATION");
            default:
                return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof User))
            return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return String.format("User[id=%d, username=%s, role=%s]",
                userId, username, role);
    }
}
