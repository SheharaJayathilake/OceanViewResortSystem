package com.oceanview.dao.impl;

import com.oceanview.dao.BaseDAO;
import com.oceanview.model.RoomType;
import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomTypeDAOImpl implements BaseDAO<RoomType, Integer> {
    private static final Logger LOGGER = Logger.getLogger(RoomTypeDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;
    
    private static final String INSERT_ROOM_TYPE = 
        "INSERT INTO room_types (type_name, rate_per_night, capacity, description, is_available) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String SELECT_BY_ID = 
        "SELECT room_type_id, type_name, rate_per_night, capacity, description, " +
        "is_available FROM room_types WHERE room_type_id = ?";
    
    private static final String SELECT_ALL = 
        "SELECT room_type_id, type_name, rate_per_night, capacity, description, " +
        "is_available FROM room_types ORDER BY type_name";
    
    private static final String SELECT_AVAILABLE = 
        "SELECT room_type_id, type_name, rate_per_night, capacity, description, " +
        "is_available FROM room_types WHERE is_available = TRUE ORDER BY rate_per_night";
    
    private static final String UPDATE_ROOM_TYPE = 
        "UPDATE room_types SET type_name = ?, rate_per_night = ?, capacity = ?, " +
        "description = ?, is_available = ? WHERE room_type_id = ?";
    
    private static final String DELETE_ROOM_TYPE = 
        "DELETE FROM room_types WHERE room_type_id = ?";
    
    private static final String COUNT_ROOM_TYPES = 
        "SELECT COUNT(*) FROM room_types";
    
    public RoomTypeDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public RoomType create(RoomType roomType) throws DAOException {
        if (roomType == null) {
            throw new DAOException("RoomType object cannot be null");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_ROOM_TYPE, Statement.RETURN_GENERATED_KEYS);
            
            pstmt.setString(1, roomType.getTypeName());
            pstmt.setBigDecimal(2, roomType.getRatePerNight());
            pstmt.setInt(3, roomType.getCapacity());
            pstmt.setString(4, roomType.getDescription());
            pstmt.setBoolean(5, roomType.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new DAOException("Creating room type failed, no rows affected");
            }
            
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                roomType.setRoomTypeId(rs.getInt(1));
                LOGGER.info("Room type created: " + roomType.getTypeName());
                return roomType;
            } else {
                throw new DAOException("Creating room type failed, no ID obtained");
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating room type", e);
            throw new DAOException("Failed to create room type: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public Optional<RoomType> findById(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid room type ID");
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
                return Optional.of(mapResultSetToRoomType(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding room type by ID: " + id, e);
            throw new DAOException("Failed to find room type: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public List<RoomType> findAll() throws DAOException {
        List<RoomType> roomTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                roomTypes.add(mapResultSetToRoomType(rs));
            }
            
            LOGGER.info("Retrieved " + roomTypes.size() + " room types");
            return roomTypes;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all room types", e);
            throw new DAOException("Failed to retrieve room types: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    public List<RoomType> findAvailable() throws DAOException {
        List<RoomType> roomTypes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_AVAILABLE);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                roomTypes.add(mapResultSetToRoomType(rs));
            }
            
            return roomTypes;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving available room types", e);
            throw new DAOException("Failed to retrieve room types: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    @Override
    public boolean update(RoomType roomType) throws DAOException {
        if (roomType == null || roomType.getRoomTypeId() == null) {
            throw new DAOException("Invalid room type object for update");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_ROOM_TYPE);
            
            pstmt.setString(1, roomType.getTypeName());
            pstmt.setBigDecimal(2, roomType.getRatePerNight());
            pstmt.setInt(3, roomType.getCapacity());
            pstmt.setString(4, roomType.getDescription());
            pstmt.setBoolean(5, roomType.isAvailable());
            pstmt.setInt(6, roomType.getRoomTypeId());
            
            int affectedRows = pstmt.executeUpdate();
            LOGGER.info("Room type updated: " + roomType.getTypeName());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating room type", e);
            throw new DAOException("Failed to update room type: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }
    
    @Override
    public boolean delete(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid room type ID for deletion");
        }
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(DELETE_ROOM_TYPE);
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            LOGGER.info("Room type deleted: " + id);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room type", e);
            throw new DAOException("Failed to delete room type: " + e.getMessage(), e);
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
            pstmt = conn.prepareStatement(COUNT_ROOM_TYPES);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting room types", e);
            throw new DAOException("Failed to count room types: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }
    
    private RoomType mapResultSetToRoomType(ResultSet rs) throws SQLException {
        RoomType roomType = new RoomType();
        roomType.setRoomTypeId(rs.getInt("room_type_id"));
        roomType.setTypeName(rs.getString("type_name"));
        roomType.setRatePerNight(rs.getBigDecimal("rate_per_night"));
        roomType.setCapacity(rs.getInt("capacity"));
        roomType.setDescription(rs.getString("description"));
        roomType.setAvailable(rs.getBoolean("is_available"));
        return roomType;
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
