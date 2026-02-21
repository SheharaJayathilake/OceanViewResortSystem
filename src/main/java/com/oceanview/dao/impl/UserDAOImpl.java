package com.oceanview.dao.impl;

import com.oceanview.dao.BaseDAO;
import com.oceanview.model.User;
import com.oceanview.model.User.UserRole;
import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UserDAO implementation with advanced SQL operations
 * Implements connection management and transaction handling
 */
public class UserDAOImpl implements BaseDAO<User, Integer> {
    private static final Logger LOGGER = Logger.getLogger(UserDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    // SQL Queries as constants
    private static final String INSERT_USER = 
        "INSERT INTO users (username, password_hash, full_name, email, role, is_active) " +
        "VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT user_id, username, password_hash, full_name, email, role, is_active, " +
        "created_at, last_login FROM users WHERE user_id = ?";
    
    private static final String SELECT_BY_USERNAME = 
        "SELECT user_id, username, password_hash, full_name, email, role, is_active, " +
        "created_at, last_login FROM users WHERE username = ?";
    
    private static final String SELECT_ALL = 
        "SELECT user_id, username, password_hash, full_name, email, role, is_active, " +
        "created_at, last_login FROM users ORDER BY created_at DESC";
    
    private static final String UPDATE_USER = 
        "UPDATE users SET full_name = ?, email = ?, role = ?, is_active = ? WHERE user_id = ?";
    
    private static final String UPDATE_PASSWORD = 
        "UPDATE users SET password_hash = ? WHERE user_id = ?";
    
    private static final String UPDATE_LAST_LOGIN = 
        "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
    
    private static final String DELETE_USER = 
        "DELETE FROM users WHERE user_id = ?";
    
    private static final String COUNT_USERS = 
        "SELECT COUNT(*) FROM users";
    
    public UserDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public User create(User user) throws DAOException {
        if (user == null) {
            throw new DAOException("User object cannot be null");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getFullName());
            pstmt.setString(4, user.getEmail());
            pstmt.setString(5, user.getRole().name());
            pstmt.setBoolean(6, user.isActive());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating user failed, no rows affected");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                user.setUserId(rs.getInt(1));
                LOGGER.info("User created successfully: " + user.getUsername());
                return user;
            } else {
                throw new DAOException("Creating user failed, no ID obtained");
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating user: " + user.getUsername(), e);
            throw new DAOException("Failed to create user: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public Optional<User> findById(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid user ID");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID);
            pstmt.setInt(1, id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by ID: " + id, e);
            throw new DAOException("Failed to find user: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find user by username for authentication
     * @param username Username to search
     * @return Optional containing user if found
     * @throws DAOException if query fails
     */
    public Optional<User> findByUsername(String username) throws DAOException {
        if (username == null || username.trim().isEmpty()) {
            throw new DAOException("Username cannot be empty");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_USERNAME);
            pstmt.setString(1, username);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding user by username: " + username, e);
            throw new DAOException("Failed to find user: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<User> findAll() throws DAOException {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            LOGGER.info("Retrieved " + users.size() + " users");
            return users;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all users", e);
            throw new DAOException("Failed to retrieve users: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public boolean update(User user) throws DAOException {
        if (user == null || user.getUserId() == null) {
            throw new DAOException("Invalid user object for update");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_USER);
            
            pstmt.setString(1, user.getFullName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().name());
            pstmt.setBoolean(4, user.isActive());
            pstmt.setInt(5, user.getUserId());
            
            int affectedRows = pstmt.executeUpdate();
            LOGGER.info("User updated: " + user.getUsername());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating user: " + user.getUserId(), e);
            throw new DAOException("Failed to update user: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Update user password
     * @param userId User ID
     * @param newPasswordHash New password hash
     * @return true if update successful
     * @throws DAOException if update fails
     */
    public boolean updatePassword(Integer userId, String newPasswordHash) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_PASSWORD);
            
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating password for user: " + userId, e);
            throw new DAOException("Failed to update password: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    /**
     * Update last login timestamp
     * @param userId User ID
     * @return true if update successful
     * @throws DAOException if update fails
     */
    public boolean updateLastLogin(Integer userId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_LAST_LOGIN);
            pstmt.setInt(1, userId);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error updating last login for user: " + userId, e);
            // Don't throw exception as this is non-critical
            return false;
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    @Override
    public boolean delete(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid user ID for deletion");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(DELETE_USER);
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            LOGGER.info("User deleted: " + id);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting user: " + id, e);
            throw new DAOException("Failed to delete user: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    @Override
    public long count() throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(COUNT_USERS);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting users", e);
            throw new DAOException("Failed to count users: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Map ResultSet to User object
     * @param rs ResultSet
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("is_active"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
    
    /**
     * Close database resources safely
     */
    private void closeResources(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
        }
        
        try {
            if (pstmt != null) pstmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing PreparedStatement", e);
        }
        
        if (conn != null) {
            connectionManager.releaseConnection(conn);
        }
    }
}
