package com.oceanview.dao.impl;

import com.oceanview.exception.DAOException;
import com.oceanview.util.DatabaseConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Data Access Object for the payments table.
 * Records individual payment transactions against reservations,
 * supporting multiple payment methods and partial payments.
 */
public class PaymentDAOImpl {
    private static final Logger LOGGER = Logger.getLogger(PaymentDAOImpl.class.getName());
    private final DatabaseConnectionManager connectionManager;

    private static final String INSERT_PAYMENT = "INSERT INTO payments (reservation_id, amount, payment_method, transaction_reference, processed_by) "
            + "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_BY_RESERVATION = "SELECT * FROM payments WHERE reservation_id = ? ORDER BY payment_date DESC";

    private static final String SUM_BY_RESERVATION = "SELECT COALESCE(SUM(amount), 0) AS total_paid FROM payments WHERE reservation_id = ?";

    public PaymentDAOImpl() {
        this.connectionManager = DatabaseConnectionManager.getInstance();
    }

    /**
     * Record a new payment transaction.
     *
     * @param reservationId     the reservation being paid for
     * @param amount            payment amount
     * @param paymentMethod     CASH, CREDIT_CARD, DEBIT_CARD, or ONLINE
     * @param transactionRef    optional reference number for card/online payments
     * @param processedByUserId the user processing the payment
     * @return generated payment ID
     * @throws DAOException if insert fails
     */
    public int recordPayment(int reservationId, BigDecimal amount, String paymentMethod,
            String transactionRef, int processedByUserId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(INSERT_PAYMENT, Statement.RETURN_GENERATED_KEYS);

            pstmt.setInt(1, reservationId);
            pstmt.setBigDecimal(2, amount);
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, transactionRef);
            pstmt.setInt(5, processedByUserId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DAOException("Recording payment failed, no rows inserted.");
            }

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int paymentId = rs.getInt(1);
                LOGGER.info(String.format("Payment recorded: ID=%d, Reservation=%d, Amount=%.2f, Method=%s",
                        paymentId, reservationId, amount, paymentMethod));
                return paymentId;
            }

            throw new DAOException("Recording payment failed, no ID obtained.");

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error recording payment", e);
            throw new DAOException("Failed to record payment: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    /**
     * Get the total amount already paid for a reservation.
     *
     * @param reservationId reservation ID
     * @return sum of all payment amounts
     * @throws DAOException if query fails
     */
    public BigDecimal getTotalPaid(int reservationId) throws DAOException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = connectionManager.getConnection();
            pstmt = conn.prepareStatement(SUM_BY_RESERVATION);
            pstmt.setInt(1, reservationId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("total_paid");
            }
            return BigDecimal.ZERO;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error summing payments", e);
            throw new DAOException("Failed to retrieve total paid: " + e.getMessage(), e);
        } finally {
            closeResources(rs, pstmt, conn);
        }
    }

    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException ignored) {
        }
        try {
            if (stmt != null)
                stmt.close();
        } catch (SQLException ignored) {
        }
        try {
            if (conn != null)
                connectionManager.releaseConnection(conn);
        } catch (Exception ignored) {
        }
    }
}
