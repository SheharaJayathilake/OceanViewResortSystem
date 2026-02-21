package com.oceanview.dao.impl;

import com.oceanview.dao.BaseDAO;
import com.oceanview.model.Guest;
import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GuestDAO implementation
 */
public class GuestDAOImpl implements BaseDAO<Guest, Integer> {
    private static final Logger LOGGER = Logger.getLogger(GuestDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    private static final String INSERT_GUEST = 
        "INSERT INTO guests (guest_name, address, contact_number, email, " +
        "identification_type, identification_number) VALUES (?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT guest_id, guest_name, address, contact_number, email, " +
        "identification_type, identification_number, created_at, updated_at " +
        "FROM guests WHERE guest_id = ?";
    
    private static final String SELECT_ALL = 
        "SELECT guest_id, guest_name, address, contact_number, email, " +
        "identification_type, identification_number, created_at, updated_at " +
        "FROM guests ORDER BY created_at DESC";
    
    private static final String SELECT_BY_CONTACT = 
        "SELECT guest_id, guest_name, address, contact_number, email, " +
        "identification_type, identification_number, created_at, updated_at " +
        "FROM guests WHERE contact_number = ?";
    
    private static final String SEARCH_GUESTS = 
        "SELECT guest_id, guest_name, address, contact_number, email, " +
        "identification_type, identification_number, created_at, updated_at " +
        "FROM guests WHERE guest_name LIKE ? OR contact_number LIKE ? " +
        "ORDER BY guest_name";
    
    private static final String UPDATE_GUEST = 
        "UPDATE guests SET guest_name = ?, address = ?, contact_number = ?, " +
        "email = ?, identification_type = ?, identification_number = ? WHERE guest_id = ?";
    
    private static final String DELETE_GUEST = 
        "DELETE FROM guests WHERE guest_id = ?";
    
    private static final String COUNT_GUESTS = 
        "SELECT COUNT(*) FROM guests";
    
    public GuestDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public Guest create(Guest guest) throws DAOException {
        if (guest == null) {
            throw new DAOException("Guest object cannot be null");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_GUEST, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, guest.getGuestName());
            pstmt.setString(2, guest.getAddress());
            pstmt.setString(3, guest.getContactNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getIdentificationType());
            pstmt.setString(6, guest.getIdentificationNumber());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating guest failed, no rows affected");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                guest.setGuestId(rs.getInt(1));
                LOGGER.info("Guest created successfully: " + guest.getGuestName());
                return guest;
            } else {
                throw new DAOException("Creating guest failed, no ID obtained");
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating guest", e);
            throw new DAOException("Failed to create guest: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public Optional<Guest> findById(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid guest ID");
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
                return Optional.of(mapResultSetToGuest(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding guest by ID: " + id, e);
            throw new DAOException("Failed to find guest: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Find guest by contact number
     */
    public Optional<Guest> findByContact(String contactNumber) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_CONTACT);
            pstmt.setString(1, contactNumber);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapResultSetToGuest(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding guest by contact", e);
            throw new DAOException("Failed to find guest: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    /**
     * Search guests by name or contact
     */
    public List<Guest> searchGuests(String searchTerm) throws DAOException {
        List<Guest> guests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SEARCH_GUESTS);
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            
            return guests;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching guests", e);
            throw new DAOException("Failed to search guests: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<Guest> findAll() throws DAOException {
        List<Guest> guests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                guests.add(mapResultSetToGuest(rs));
            }
            
            return guests;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all guests", e);
            throw new DAOException("Failed to retrieve guests: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public boolean update(Guest guest) throws DAOException {
        if (guest == null || guest.getGuestId() == null) {
            throw new DAOException("Invalid guest object for update");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_GUEST);
            
            pstmt.setString(1, guest.getGuestName());
            pstmt.setString(2, guest.getAddress());
            pstmt.setString(3, guest.getContactNumber());
            pstmt.setString(4, guest.getEmail());
            pstmt.setString(5, guest.getIdentificationType());
            pstmt.setString(6, guest.getIdentificationNumber());
            pstmt.setInt(7, guest.getGuestId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating guest", e);
            throw new DAOException("Failed to update guest: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    @Override
    public boolean delete(Integer id) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(DELETE_GUEST);
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting guest", e);
            throw new DAOException("Failed to delete guest: " + e.getMessage(), e);
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
            pstmt = conn.prepareStatement(COUNT_GUESTS);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting guests", e);
            throw new DAOException("Failed to count guests: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    private Guest mapResultSetToGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("guest_id"));
        guest.setGuestName(rs.getString("guest_name"));
        guest.setAddress(rs.getString("address"));
        guest.setContactNumber(rs.getString("contact_number"));
        guest.setEmail(rs.getString("email"));
        guest.setIdentificationType(rs.getString("identification_type"));
        guest.setIdentificationNumber(rs.getString("identification_number"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            guest.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            guest.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return guest;
    }
    
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
