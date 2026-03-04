package com.oceanview.dao.impl;

import com.oceanview.model.Room;
import com.oceanview.model.RoomType;
import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/** DAO for individual room operations */
public class RoomDAOImpl {
    private static final Logger LOGGER = Logger.getLogger(RoomDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;

    private static final String SELECT_ALL = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM rooms r JOIN room_types rt ON r.room_type_id = rt.room_type_id ORDER BY r.room_number";

    private static final String SELECT_BY_ID = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM rooms r JOIN room_types rt ON r.room_type_id = rt.room_type_id WHERE r.room_id = ?";

    private static final String SELECT_BY_TYPE = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM rooms r JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
            "WHERE r.room_type_id = ? AND r.is_active = 1 ORDER BY r.room_number";

    // Finds the first available room of a given type for the requested date range.
    // A room is "available" if it has NO overlapping active reservations.
    private static final String FIND_AVAILABLE_ROOM = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM rooms r " +
            "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
            "WHERE r.room_type_id = ? AND r.is_active = 1 " +
            "AND r.room_id NOT IN (" +
            "    SELECT res.room_id FROM reservations res " +
            "    WHERE res.room_id IS NOT NULL " +
            "    AND res.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
            "    AND res.check_in_date < ? AND res.check_out_date > ?" +
            ") " +
            "ORDER BY r.room_number LIMIT 1";

    private static final String COUNT_AVAILABLE = "SELECT COUNT(*) FROM rooms r " +
            "WHERE r.room_type_id = ? AND r.is_active = 1 " +
            "AND r.room_id NOT IN (" +
            "    SELECT res.room_id FROM reservations res " +
            "    WHERE res.room_id IS NOT NULL " +
            "    AND res.status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
            "    AND res.check_in_date < ? AND res.check_out_date > ?" +
            ")";

    private static final String IS_ROOM_AVAILABLE = "SELECT COUNT(*) FROM reservations " +
            "WHERE room_id = ? " +
            "AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN') " +
            "AND check_in_date < ? AND check_out_date > ?";

    private static final String INSERT_ROOM = "INSERT INTO rooms (room_number, room_type_id, floor, is_active) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_ROOM = "UPDATE rooms SET room_number = ?, room_type_id = ?, floor = ?, is_active = ? WHERE room_id = ?";

    private static final String DELETE_ROOM = "DELETE FROM rooms WHERE room_id = ?";

    private static final String SELECT_ALL_ACTIVE = "SELECT r.*, rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM rooms r JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
            "WHERE r.is_active = 1 ORDER BY r.room_number";

    private static final String COUNT_RESERVATIONS_FOR_ROOM = "SELECT COUNT(*) FROM reservations " +
            "WHERE room_id = ? AND status IN ('PENDING', 'CONFIRMED', 'CHECKED_IN')";

    public RoomDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    public Optional<Room> findById(Integer roomId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID);
            pstmt.setInt(1, roomId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding room by ID: " + roomId, e);
            throw new DAOException("Failed to find room: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public List<Room> findByRoomType(int roomTypeId) throws DAOException {
        List<Room> rooms = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_TYPE);
            pstmt.setInt(1, roomTypeId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            return rooms;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding rooms for type: " + roomTypeId, e);
            throw new DAOException("Failed to find rooms: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public Optional<Room> findAvailableRoom(int roomTypeId, LocalDate checkIn, LocalDate checkOut)
            throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(FIND_AVAILABLE_ROOM);
            pstmt.setInt(1, roomTypeId);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToRoom(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding available room", e);
            throw new DAOException("Failed to find available room: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public int countAvailable(int roomTypeId, LocalDate checkIn, LocalDate checkOut) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(COUNT_AVAILABLE);
            pstmt.setInt(1, roomTypeId);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting available rooms", e);
            throw new DAOException("Failed to count available rooms: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public boolean isRoomAvailable(int roomId, LocalDate checkIn, LocalDate checkOut) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(IS_ROOM_AVAILABLE);
            pstmt.setInt(1, roomId);
            pstmt.setDate(2, Date.valueOf(checkOut));
            pstmt.setDate(3, Date.valueOf(checkIn));
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking room availability for room: " + roomId, e);
            throw new DAOException("Failed to check room availability: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public List<Room> findAll() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            return rooms;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all rooms", e);
            throw new DAOException("Failed to retrieve rooms: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public List<Room> findAllActive() throws DAOException {
        List<Room> rooms = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_ALL_ACTIVE);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                rooms.add(mapResultSetToRoom(rs));
            }
            return rooms;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving active rooms", e);
            throw new DAOException("Failed to retrieve active rooms: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public Room create(Room room) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_ROOM, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getRoomTypeId());
            pstmt.setInt(3, room.getFloor());
            pstmt.setBoolean(4, room.isActive());
            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                room.setRoomId(rs.getInt(1));
            }
            return room;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating room", e);
            throw new DAOException("Failed to create room: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    public boolean update(Room room) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_ROOM);
            pstmt.setString(1, room.getRoomNumber());
            pstmt.setInt(2, room.getRoomTypeId());
            pstmt.setInt(3, room.getFloor());
            pstmt.setBoolean(4, room.isActive());
            pstmt.setInt(5, room.getRoomId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating room", e);
            throw new DAOException("Failed to update room: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    public boolean delete(int roomId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(DELETE_ROOM);
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room", e);
            throw new DAOException("Failed to delete room: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    public int countActiveReservations(int roomId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(COUNT_RESERVATIONS_FOR_ROOM);
            pstmt.setInt(1, roomId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting reservations for room", e);
            throw new DAOException("Failed to count reservations: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomTypeId(rs.getInt("room_type_id"));
        room.setFloor(rs.getInt("floor"));
        room.setActive(rs.getBoolean("is_active"));

        try {
            String typeName = rs.getString("type_name");
            if (typeName != null) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeId(room.getRoomTypeId());
                roomType.setTypeName(typeName);
                roomType.setRatePerNight(rs.getBigDecimal("rate_per_night"));
                roomType.setCapacity(rs.getInt("capacity"));
                room.setRoomType(roomType);
            }
        } catch (SQLException ignored) {
        }

        return room;
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing ResultSet", e);
        }
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error closing Statement", e);
        }
        if (conn != null) {
            connectionManager.releaseConnection(conn);
        }
    }
}
