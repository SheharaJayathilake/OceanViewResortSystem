package com.oceanview.dao.impl;

import com.oceanview.dao.BaseDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.Guest;
import com.oceanview.model.RoomType;
import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReservationDAOImpl implements BaseDAO<Reservation, Integer> {
    private static final Logger LOGGER = Logger.getLogger(ReservationDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;

    private static final String INSERT_RESERVATION = "INSERT INTO reservations (reservation_number, guest_id, room_type_id, "
            +
            "check_in_date, check_out_date, number_of_guests, status, payment_status, " +
            "total_amount, special_requests, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_ID_WITH_DETAILS = "SELECT r.*, g.guest_name, g.contact_number, g.email, " +
            "rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM reservations r " +
            "JOIN guests g ON r.guest_id = g.guest_id " +
            "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
            "WHERE r.reservation_id = ?";

    private static final String SELECT_BY_RESERVATION_NUMBER = "SELECT r.*, g.guest_name, g.contact_number, g.email, " +
            "rt.type_name, rt.rate_per_night, rt.capacity " +
            "FROM reservations r " +
            "JOIN guests g ON r.guest_id = g.guest_id " +
            "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
            "WHERE r.reservation_number = ?";

    private static final String UPDATE_RESERVATION = "UPDATE reservations SET room_type_id = ?, check_in_date = ?, " +
            "check_out_date = ?, number_of_guests = ?, status = ?, payment_status = ?, " +
            "total_amount = ?, special_requests = ? WHERE reservation_id = ?";

    private static final String CALL_CALCULATE_BILL = "{CALL sp_calculate_bill(?, ?, ?)}";

    private static final String CHECK_AVAILABILITY_FUNCTION = "SELECT fn_check_availability(?, ?, ?) as is_available";

    public ReservationDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public Reservation create(Reservation reservation) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_RESERVATION, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, reservation.getReservationNumber());
            pstmt.setInt(2, reservation.getGuestId());
            pstmt.setInt(3, reservation.getRoomTypeId());
            pstmt.setDate(4, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(5, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setInt(6, reservation.getNumberOfGuests());
            pstmt.setString(7, reservation.getStatus().name());
            pstmt.setString(8, reservation.getPaymentStatus().name());
            pstmt.setBigDecimal(9, reservation.getTotalAmount());
            pstmt.setString(10, reservation.getSpecialRequests());
            pstmt.setInt(11, reservation.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Creating reservation failed");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                reservation.setReservationId(rs.getInt(1));
                LOGGER.info("Reservation created: " + reservation.getReservationNumber());
                return reservation;
            } else {
                throw new DAOException("Creating reservation failed, no ID obtained");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            throw new DAOException("Failed to create reservation: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    /**
     * Calculate bill using stored procedure
     */
    public BigDecimal calculateBillWithStoredProcedure(Integer reservationId) throws DAOException {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = connectionManager.getConnection();
            cstmt = conn.prepareCall(CALL_CALCULATE_BILL);

            cstmt.setInt(1, reservationId);
            cstmt.registerOutParameter(2, Types.DECIMAL);
            cstmt.registerOutParameter(3, Types.INTEGER);

            cstmt.execute();

            BigDecimal totalAmount = cstmt.getBigDecimal(2);
            int numberOfNights = cstmt.getInt(3);

            LOGGER.info(String.format("Bill calculated: ID=%d, Amount=%.2f, Nights=%d",
                    reservationId, totalAmount, numberOfNights));

            return totalAmount;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating bill", e);
            throw new DAOException("Failed to calculate bill: " + e.getMessage(), e);
        } finally {
            closeResources(null, cstmt, conn);
        }
    }

    /**
     * Check room availability using database function
     */
    public boolean checkAvailability(Integer roomTypeId, LocalDate checkIn, LocalDate checkOut)
            throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(CHECK_AVAILABILITY_FUNCTION);

            pstmt.setInt(1, roomTypeId);
            pstmt.setDate(2, Date.valueOf(checkIn));
            pstmt.setDate(3, Date.valueOf(checkOut));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean("is_available");
            }
            return false;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking availability", e);
            throw new DAOException("Failed to check availability: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    /**
     * Find reservation by reservation number
     */
    public Optional<Reservation> findByReservationNumber(String reservationNumber) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_RESERVATION_NUMBER);
            pstmt.setString(1, reservationNumber);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToReservation(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation", e);
            throw new DAOException("Failed to find reservation: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    @Override
    public Optional<Reservation> findById(Integer id) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SELECT_BY_ID_WITH_DETAILS);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToReservation(rs));
            }
            return Optional.empty();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation", e);
            throw new DAOException("Failed to find reservation: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    // Additional methods: findAll(), update(), delete(), count()
    // Implementation similar to other DAOs

    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setReservationId(rs.getInt("reservation_id"));
        reservation.setReservationNumber(rs.getString("reservation_number"));
        reservation.setGuestId(rs.getInt("guest_id"));
        reservation.setRoomTypeId(rs.getInt("room_type_id"));

        Date checkIn = rs.getDate("check_in_date");
        if (checkIn != null)
            reservation.setCheckInDate(checkIn.toLocalDate());

        Date checkOut = rs.getDate("check_out_date");
        if (checkOut != null)
            reservation.setCheckOutDate(checkOut.toLocalDate());

        reservation.setNumberOfGuests(rs.getInt("number_of_guests"));
        reservation.setStatus(Reservation.ReservationStatus.valueOf(rs.getString("status")));
        reservation.setPaymentStatus(Reservation.PaymentStatus.valueOf(rs.getString("payment_status")));
        reservation.setTotalAmount(rs.getBigDecimal("total_amount"));
        reservation.setSpecialRequests(rs.getString("special_requests"));
        reservation.setCreatedBy(rs.getInt("created_by"));

        // Map Guest details if available in ResultSet
        try {
            String guestName = rs.getString("guest_name");
            if (guestName != null) {
                Guest guest = new Guest();
                guest.setGuestId(reservation.getGuestId());
                guest.setGuestName(guestName);
                guest.setContactNumber(rs.getString("contact_number"));
                guest.setEmail(rs.getString("email"));
                reservation.setGuest(guest);
            }
        } catch (SQLException ignored) {
            // Column might not exist in some queries
        }

        // Map RoomType details if available in ResultSet
        try {
            String typeName = rs.getString("type_name");
            if (typeName != null) {
                RoomType roomType = new RoomType();
                roomType.setRoomTypeId(reservation.getRoomTypeId());
                roomType.setTypeName(typeName);
                roomType.setRatePerNight(rs.getBigDecimal("rate_per_night"));
                roomType.setCapacity(rs.getInt("capacity"));
                reservation.setRoomType(roomType);
            }
        } catch (SQLException ignored) {
            // Column might not exist in some queries
        }

        return reservation;
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

    // Add these methods to the ReservationDAOImpl class from earlier:

    @Override
    public List<Reservation> findAll() throws DAOException {
        List<Reservation> reservations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String query = "SELECT r.*, g.guest_name, g.contact_number, g.email, " +
                "rt.type_name, rt.rate_per_night, rt.capacity " +
                "FROM reservations r " +
                "JOIN guests g ON r.guest_id = g.guest_id " +
                "JOIN room_types rt ON r.room_type_id = rt.room_type_id " +
                "ORDER BY r.created_at DESC";

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservations.add(reservation);
            }

            LOGGER.info("Retrieved " + reservations.size() + " reservations");
            return reservations;

        } catch (

        SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all reservations", e);
            throw new DAOException("Failed to retrieve reservations: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    @Override
    public boolean update(Reservation reservation) throws DAOException {
        if (reservation == null || reservation.getReservationId() == null) {
            throw new DAOException("Invalid reservation object for update");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(UPDATE_RESERVATION);

            pstmt.setInt(1, reservation.getRoomTypeId());
            pstmt.setDate(2, Date.valueOf(reservation.getCheckInDate()));
            pstmt.setDate(3, Date.valueOf(reservation.getCheckOutDate()));
            pstmt.setInt(4, reservation.getNumberOfGuests());
            pstmt.setString(5, reservation.getStatus().name());
            pstmt.setString(6, reservation.getPaymentStatus().name());
            pstmt.setBigDecimal(7, reservation.getTotalAmount());
            pstmt.setString(8, reservation.getSpecialRequests());
            pstmt.setInt(9, reservation.getReservationId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LOGGER.info("Reservation updated: " + reservation.getReservationNumber());
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation", e);
            throw new DAOException("Failed to update reservation: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    @Override
    public boolean delete(Integer id) throws DAOException {
        if (id == null || id <= 0) {
            throw new DAOException("Invalid reservation ID for deletion");
        }

        Connection conn = null;
        PreparedStatement pstmt = null;

        String query = "DELETE FROM reservations WHERE reservation_id = ?";

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                LOGGER.info("Reservation deleted: " + id);
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting reservation", e);
            throw new DAOException("Failed to delete reservation: " + e.getMessage(), e);
        } finally {
            closeResources(null, pstmt, conn);
        }
    }

    @Override
    public long count() throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String query = "SELECT COUNT(*) FROM reservations";

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting reservations", e);
            throw new DAOException("Failed to count reservations: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

}
