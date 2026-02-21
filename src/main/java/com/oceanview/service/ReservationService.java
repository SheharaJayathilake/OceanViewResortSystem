package com.oceanview.service;

import com.oceanview.dao.impl.*;
import com.oceanview.model.*;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;
import com.oceanview.util.ReservationNumberGenerator;
import com.oceanview.util.ValidationUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reservation Service with complex business logic
 * Implements transaction management and validation rules
 */
public class ReservationService {
    private static final Logger LOGGER = Logger.getLogger(ReservationService.class.getName());

    private final ReservationDAOImpl reservationDAO;
    private final GuestDAOImpl guestDAO;
    private final RoomTypeDAOImpl roomTypeDAO;

    public ReservationService(ReservationDAOImpl reservationDAO,
            GuestDAOImpl guestDAO,
            RoomTypeDAOImpl roomTypeDAO) {
        this.reservationDAO = reservationDAO;
        this.guestDAO = guestDAO;
        this.roomTypeDAO = roomTypeDAO;
    }

    /**
     * Create new reservation with complete validation
     * 
     * @param reservation Reservation object
     * @return Created reservation with generated number
     * @throws BusinessException if validation or creation fails
     */
    public Reservation createReservation(Reservation reservation) throws BusinessException {

        validateReservation(reservation);

        try {

            Optional<Guest> guest = guestDAO.findById(reservation.getGuestId());
            if (!guest.isPresent()) {
                throw new BusinessException("Guest not found. Please register guest first.");
            }

            Optional<RoomType> roomType = roomTypeDAO.findById(reservation.getRoomTypeId());
            if (!roomType.isPresent()) {
                throw new BusinessException("Invalid room type selected");
            }

            if (!roomType.get().isAvailable()) {
                throw new BusinessException("Selected room type is currently unavailable");
            }

            boolean isAvailable = reservationDAO.checkAvailability(
                    reservation.getRoomTypeId(),
                    reservation.getCheckInDate(),
                    reservation.getCheckOutDate());

            if (!isAvailable) {
                throw new BusinessException(
                        "Room type not available for selected dates. Please choose different dates.");
            }

            if (reservation.getNumberOfGuests() > roomType.get().getCapacity()) {
                throw new BusinessException(
                        String.format("Number of guests (%d) exceeds room capacity (%d)",
                                reservation.getNumberOfGuests(), roomType.get().getCapacity()));
            }

            reservation.setReservationNumber(ReservationNumberGenerator.generate());

            BigDecimal totalAmount = reservation.calculateTotal(roomType.get().getRatePerNight());
            reservation.setTotalAmount(totalAmount);

            reservation.setStatus(Reservation.ReservationStatus.PENDING);
            reservation.setPaymentStatus(Reservation.PaymentStatus.PENDING);

            Reservation createdReservation = reservationDAO.create(reservation);

            LOGGER.info(String.format("Reservation created: %s for guest %d, Amount: %.2f",
                    createdReservation.getReservationNumber(),
                    createdReservation.getGuestId(),
                    createdReservation.getTotalAmount()));

            return createdReservation;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error creating reservation", e);
            throw new BusinessException("Failed to create reservation: " + e.getMessage());
        }
    }

    /**
     * Find reservation by reservation number
     * 
     * @param reservationNumber Reservation number
     * @return Reservation with guest and room details
     * @throws BusinessException if not found
     */
    public Reservation findReservationByNumber(String reservationNumber) throws BusinessException {
        if (reservationNumber == null || reservationNumber.trim().isEmpty()) {
            throw new BusinessException("Reservation number is required");
        }

        try {
            Optional<Reservation> reservation = reservationDAO.findByReservationNumber(reservationNumber.trim());

            if (!reservation.isPresent()) {
                throw new BusinessException("Reservation not found: " + reservationNumber);
            }

            return reservation.get();

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error finding reservation", e);
            throw new BusinessException("Failed to retrieve reservation: " + e.getMessage());
        }
    }

    /**
     * Calculate and retrieve bill for reservation
     * 
     * @param reservationNumber Reservation number
     * @return Bill details with breakdown
     * @throws BusinessException if calculation fails
     */
    public ReservationBill calculateBill(String reservationNumber) throws BusinessException {
        try {
            Reservation reservation = findReservationByNumber(reservationNumber);

            // Use stored procedure to calculate bill
            BigDecimal totalAmount = reservationDAO.calculateBillWithStoredProcedure(reservation.getReservationId());

            // Create bill object
            ReservationBill bill = new ReservationBill();
            bill.setReservation(reservation);
            bill.setTotalAmount(totalAmount);
            bill.setNumberOfNights(reservation.getNumberOfNights());
            bill.setRatePerNight(reservation.getRoomType().getRatePerNight());
            bill.setCalculatedDate(LocalDate.now());

            LOGGER.info("Bill calculated for reservation: " + reservationNumber);
            return bill;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error calculating bill", e);
            throw new BusinessException("Failed to calculate bill: " + e.getMessage());
        }
    }

    /**
     * Update reservation status
     * 
     * @param reservationNumber Reservation number
     * @param newStatus         New status
     * @return true if update successful
     * @throws BusinessException if update fails or invalid transition
     */
    public boolean updateReservationStatus(String reservationNumber,
            Reservation.ReservationStatus newStatus)
            throws BusinessException {

        try {
            Reservation reservation = findReservationByNumber(reservationNumber);

            // Validate status transition
            if (!isValidStatusTransition(reservation.getStatus(), newStatus)) {
                throw new BusinessException(
                        String.format("Invalid status transition from %s to %s",
                                reservation.getStatus(), newStatus));
            }

            reservation.setStatus(newStatus);
            boolean updated = reservationDAO.update(reservation);

            if (updated) {
                LOGGER.info(String.format("Reservation %s status updated to %s",
                        reservationNumber, newStatus));
            }

            return updated;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error updating reservation status", e);
            throw new BusinessException("Failed to update reservation: " + e.getMessage());
        }
    }

    /**
     * Confirm reservation
     * 
     * @param reservationNumber Reservation number
     * @return true if confirmation successful
     * @throws BusinessException if confirmation fails
     */
    public boolean confirmReservation(String reservationNumber) throws BusinessException {
        return updateReservationStatus(reservationNumber, Reservation.ReservationStatus.CONFIRMED);
    }

    /**
     * Check-in guest
     * 
     * @param reservationNumber Reservation number
     * @return true if check-in successful
     * @throws BusinessException if check-in fails
     */
    public boolean checkInReservation(String reservationNumber) throws BusinessException {
        Reservation reservation = findReservationByNumber(reservationNumber);

        if (!reservation.canCheckIn()) {
            throw new BusinessException(
                    "Cannot check-in. Reservation must be confirmed and check-in date must be today.");
        }

        return updateReservationStatus(reservationNumber, Reservation.ReservationStatus.CHECKED_IN);
    }

    /**
     * Check-out guest
     * 
     * @param reservationNumber Reservation number
     * @return true if check-out successful
     * @throws BusinessException if check-out fails
     */
    public boolean checkOutReservation(String reservationNumber) throws BusinessException {
        Reservation reservation = findReservationByNumber(reservationNumber);

        if (!reservation.canCheckOut()) {
            throw new BusinessException("Cannot check-out. Guest must be checked-in first.");
        }

        // Verify payment is complete
        if (reservation.getPaymentStatus() != Reservation.PaymentStatus.PAID) {
            throw new BusinessException("Cannot check-out. Payment not complete.");
        }

        return updateReservationStatus(reservationNumber, Reservation.ReservationStatus.CHECKED_OUT);
    }

    /**
     * Process payment for reservation
     * 
     * @param reservationNumber Reservation number
     * @return true if payment successful
     * @throws BusinessException if payment fails
     */
    public boolean payReservation(String reservationNumber) throws BusinessException {
        try {
            Reservation reservation = findReservationByNumber(reservationNumber);

            // Generate bill to make sure total_amount is calculated in the database
            calculateBill(reservationNumber);

            // Validate that we can pay
            if (reservation.getStatus() == Reservation.ReservationStatus.CANCELLED) {
                throw new BusinessException("Cannot process payment for cancelled reservation");
            }

            if (reservation.getPaymentStatus() == Reservation.PaymentStatus.PAID) {
                throw new BusinessException("Reservation is already paid");
            }

            reservation.setPaymentStatus(Reservation.PaymentStatus.PAID);
            boolean updated = reservationDAO.update(reservation);

            if (updated) {
                LOGGER.info(String.format("Payment processed for reservation %s", reservationNumber));
            }

            return updated;

        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error processing payment", e);
            throw new BusinessException("Failed to process payment: " + e.getMessage());
        }
    }

    /**
     * Cancel reservation
     * 
     * @param reservationNumber Reservation number
     * @return true if cancellation successful
     * @throws BusinessException if cancellation fails
     */
    public boolean cancelReservation(String reservationNumber) throws BusinessException {
        Reservation reservation = findReservationByNumber(reservationNumber);

        if (!reservation.isModifiable()) {
            throw new BusinessException("Cannot cancel reservation in current status");
        }

        return updateReservationStatus(reservationNumber, Reservation.ReservationStatus.CANCELLED);
    }

    /**
     * Get all reservations
     * 
     * @return List of all reservations
     * @throws BusinessException if retrieval fails
     */
    public List<Reservation> getAllReservations() throws BusinessException {
        try {
            return reservationDAO.findAll();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving reservations", e);
            throw new BusinessException("Failed to retrieve reservations: " + e.getMessage());
        }
    }

    /**
     * Validate reservation data
     */
    private void validateReservation(Reservation reservation) throws BusinessException {
        if (reservation == null) {
            throw new BusinessException("Reservation data is required");
        }

        if (reservation.getGuestId() == null) {
            throw new BusinessException("Guest information is required");
        }

        if (reservation.getRoomTypeId() == null) {
            throw new BusinessException("Room type selection is required");
        }

        if (!ValidationUtil.isValidDateRange(
                reservation.getCheckInDate(), reservation.getCheckOutDate())) {
            throw new BusinessException("Invalid date range. Check-out must be after check-in.");
        }

        if (!ValidationUtil.isFutureDate(reservation.getCheckInDate())) {
            throw new BusinessException("Check-in date cannot be in the past");
        }

        if (reservation.getNumberOfNights() < 1) {
            throw new BusinessException("Reservation must be for at least one night");
        }

        if (!ValidationUtil.isPositiveNumber(reservation.getNumberOfGuests())) {
            throw new BusinessException("Number of guests must be at least 1");
        }
    }

    /**
     * Validate status transition rules
     */
    private boolean isValidStatusTransition(Reservation.ReservationStatus current,
            Reservation.ReservationStatus next) {
        switch (current) {
            case PENDING:
                return next == Reservation.ReservationStatus.CONFIRMED ||
                        next == Reservation.ReservationStatus.CANCELLED;
            case CONFIRMED:
                return next == Reservation.ReservationStatus.CHECKED_IN ||
                        next == Reservation.ReservationStatus.CANCELLED;
            case CHECKED_IN:
                return next == Reservation.ReservationStatus.CHECKED_OUT;
            case CHECKED_OUT:
            case CANCELLED:
                return false;
            default:
                return false;
        }
    }
}
