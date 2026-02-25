package com.oceanview.service;

import com.oceanview.dao.impl.RoomTypeDAOImpl;
import com.oceanview.dao.impl.ReservationDAOImpl;
import com.oceanview.model.RoomType;
import com.oceanview.model.Reservation;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;
import com.oceanview.util.ValidationUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service layer for room type management.
 * Enforces business rules such as preventing deletion of
 * room types that have active reservations.
 */
public class RoomService {
    private static final Logger LOGGER = Logger.getLogger(RoomService.class.getName());

    private final RoomTypeDAOImpl roomTypeDAO;
    private final ReservationDAOImpl reservationDAO;

    public RoomService(RoomTypeDAOImpl roomTypeDAO, ReservationDAOImpl reservationDAO) {
        this.roomTypeDAO = roomTypeDAO;
        this.reservationDAO = reservationDAO;
    }

    /**
     * Retrieve all room types ordered by name.
     */
    public List<RoomType> getAllRoomTypes() throws BusinessException {
        try {
            return roomTypeDAO.findAll();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching room types", e);
            throw new BusinessException("Failed to retrieve room types: " + e.getMessage());
        }
    }

    /**
     * Find a single room type by its ID.
     */
    public RoomType findById(int roomTypeId) throws BusinessException {
        try {
            Optional<RoomType> result = roomTypeDAO.findById(roomTypeId);
            if (!result.isPresent()) {
                throw new BusinessException("Room type not found with ID: " + roomTypeId);
            }
            return result.get();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error finding room type", e);
            throw new BusinessException("Failed to find room type: " + e.getMessage());
        }
    }

    /**
     * Create a new room type after full validation.
     */
    public RoomType createRoomType(String typeName, BigDecimal ratePerNight,
            int capacity, String description) throws BusinessException {
        validateRoomTypeInput(typeName, ratePerNight, capacity);
        checkDuplicateName(typeName, null);

        try {
            RoomType roomType = new RoomType(typeName, ratePerNight, capacity);
            roomType.setDescription(description);
            roomType.setAvailable(true);
            RoomType created = roomTypeDAO.create(roomType);
            LOGGER.info("Room type created: " + created.getTypeName());
            return created;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error creating room type", e);
            throw new BusinessException("Failed to create room type: " + e.getMessage());
        }
    }

    /**
     * Update an existing room type.
     */
    public boolean updateRoomType(int roomTypeId, String typeName, BigDecimal ratePerNight,
            int capacity, String description, boolean isAvailable) throws BusinessException {
        validateRoomTypeInput(typeName, ratePerNight, capacity);
        checkDuplicateName(typeName, roomTypeId);

        try {
            Optional<RoomType> existing = roomTypeDAO.findById(roomTypeId);
            if (!existing.isPresent()) {
                throw new BusinessException("Room type not found");
            }

            RoomType roomType = existing.get();
            roomType.setTypeName(typeName);
            roomType.setRatePerNight(ratePerNight);
            roomType.setCapacity(capacity);
            roomType.setDescription(description);
            roomType.setAvailable(isAvailable);

            boolean updated = roomTypeDAO.update(roomType);
            if (updated) {
                LOGGER.info("Room type updated: " + typeName);
            }
            return updated;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error updating room type", e);
            throw new BusinessException("Failed to update room type: " + e.getMessage());
        }
    }

    /**
     * Delete a room type only if it has no active reservations.
     * Prevents data integrity violations by checking foreign key references.
     */
    public boolean deleteRoomType(int roomTypeId) throws BusinessException {
        try {
            Optional<RoomType> existing = roomTypeDAO.findById(roomTypeId);
            if (!existing.isPresent()) {
                throw new BusinessException("Room type not found");
            }

            List<Reservation> allReservations = reservationDAO.findAll();
            long activeCount = allReservations.stream()
                    .filter(r -> r.getRoomTypeId() == roomTypeId)
                    .filter(r -> r.getStatus() == Reservation.ReservationStatus.PENDING
                            || r.getStatus() == Reservation.ReservationStatus.CONFIRMED
                            || r.getStatus() == Reservation.ReservationStatus.CHECKED_IN)
                    .count();

            if (activeCount > 0) {
                throw new BusinessException(
                        "Cannot delete room type '" + existing.get().getTypeName()
                                + "' — it has " + activeCount + " active reservation(s). "
                                + "Cancel or check out all reservations first.");
            }

            boolean deleted = roomTypeDAO.delete(roomTypeId);
            if (deleted) {
                LOGGER.info("Room type deleted: " + existing.get().getTypeName());
            }
            return deleted;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room type", e);
            throw new BusinessException("Failed to delete room type: " + e.getMessage());
        }
    }

    /**
     * Get total count of room types.
     */
    public long count() throws BusinessException {
        try {
            return roomTypeDAO.count();
        } catch (DAOException e) {
            throw new BusinessException("Failed to count room types: " + e.getMessage());
        }
    }

    private void validateRoomTypeInput(String typeName, BigDecimal rate, int capacity) throws BusinessException {
        if (!ValidationUtil.isNotEmpty(typeName)) {
            throw new BusinessException("Room type name is required");
        }
        if (typeName.trim().length() < 2 || typeName.trim().length() > 50) {
            throw new BusinessException("Room type name must be between 2 and 50 characters");
        }
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Rate per night must be greater than zero");
        }
        if (rate.compareTo(new BigDecimal("99999.99")) > 0) {
            throw new BusinessException("Rate per night cannot exceed 99,999.99");
        }
        if (capacity < 1 || capacity > 20) {
            throw new BusinessException("Capacity must be between 1 and 20 guests");
        }
    }

    private void checkDuplicateName(String typeName, Integer excludeId) throws BusinessException {
        try {
            List<RoomType> all = roomTypeDAO.findAll();
            boolean duplicate = all.stream()
                    .filter(rt -> excludeId == null || !rt.getRoomTypeId().equals(excludeId))
                    .anyMatch(rt -> rt.getTypeName().equalsIgnoreCase(typeName.trim()));

            if (duplicate) {
                throw new BusinessException("A room type named '" + typeName.trim() + "' already exists");
            }
        } catch (DAOException e) {
            throw new BusinessException("Failed to validate room type name: " + e.getMessage());
        }
    }
}
