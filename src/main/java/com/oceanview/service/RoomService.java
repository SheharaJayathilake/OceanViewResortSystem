package com.oceanview.service;

import com.oceanview.dao.impl.RoomTypeDAOImpl;
import com.oceanview.dao.impl.RoomDAOImpl;
import com.oceanview.dao.impl.ReservationDAOImpl;
import com.oceanview.model.Room;
import com.oceanview.model.RoomType;
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
    private final RoomDAOImpl roomDAO;
    private final ReservationDAOImpl reservationDAO;

    public RoomService(RoomTypeDAOImpl roomTypeDAO, RoomDAOImpl roomDAO, ReservationDAOImpl reservationDAO) {
        this.roomTypeDAO = roomTypeDAO;
        this.roomDAO = roomDAO;
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

    /** Retrieve all active rooms with their room types populated. */
    public List<Room> getAllRooms() throws BusinessException {
        try {
            return roomDAO.findAll();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching rooms", e);
            throw new BusinessException("Failed to retrieve rooms: " + e.getMessage());
        }
    }

    /** Retrieve rooms belonging to a specific room type. */
    public List<Room> getRoomsByType(int roomTypeId) throws BusinessException {
        try {
            return roomDAO.findByRoomType(roomTypeId);
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching rooms for type: " + roomTypeId, e);
            throw new BusinessException("Failed to retrieve rooms: " + e.getMessage());
        }
    }

    /** Find a single room by ID. */
    public Room findRoomById(int roomId) throws BusinessException {
        try {
            return roomDAO.findById(roomId)
                    .orElseThrow(() -> new BusinessException("Room not found with ID: " + roomId));
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error finding room", e);
            throw new BusinessException("Failed to find room: " + e.getMessage());
        }
    }

    /** Get only active rooms (for reservation dropdown). */
    public List<Room> getAllActiveRooms() throws BusinessException {
        try {
            return roomDAO.findAllActive();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error fetching active rooms", e);
            throw new BusinessException("Failed to retrieve active rooms: " + e.getMessage());
        }
    }

    /** Create a new room with validation. */
    public Room createRoom(String roomNumber, int roomTypeId, int floor, boolean isActive) throws BusinessException {
        validateRoomInput(roomNumber, roomTypeId, floor);
        checkDuplicateRoomNumber(roomNumber, null);

        try {
            Room room = new Room();
            room.setRoomNumber(roomNumber.trim());
            room.setRoomTypeId(roomTypeId);
            room.setFloor(floor);
            room.setActive(isActive);

            Room created = roomDAO.create(room);
            LOGGER.info("Room created: " + created.getRoomNumber());
            return created;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error creating room", e);
            throw new BusinessException("Failed to create room: " + e.getMessage());
        }
    }

    /** Update an existing room. */
    public boolean updateRoom(int roomId, String roomNumber, int roomTypeId, int floor, boolean isActive)
            throws BusinessException {
        validateRoomInput(roomNumber, roomTypeId, floor);
        checkDuplicateRoomNumber(roomNumber, roomId);

        try {
            Room room = roomDAO.findById(roomId)
                    .orElseThrow(() -> new BusinessException("Room not found"));
            room.setRoomNumber(roomNumber.trim());
            room.setRoomTypeId(roomTypeId);
            room.setFloor(floor);
            room.setActive(isActive);

            boolean updated = roomDAO.update(room);
            if (updated) {
                LOGGER.info("Room updated: " + roomNumber);
            }
            return updated;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error updating room", e);
            throw new BusinessException("Failed to update room: " + e.getMessage());
        }
    }

    /** Delete a room only if it has no active reservations. */
    public boolean deleteRoom(int roomId) throws BusinessException {
        try {
            Room room = roomDAO.findById(roomId)
                    .orElseThrow(() -> new BusinessException("Room not found"));

            int activeCount = roomDAO.countActiveReservations(roomId);
            if (activeCount > 0) {
                throw new BusinessException(
                        "Cannot delete room '" + room.getRoomNumber() + "' — it has " +
                                activeCount + " active reservation(s). Cancel or check out all reservations first.");
            }

            boolean deleted = roomDAO.delete(roomId);
            if (deleted) {
                LOGGER.info("Room deleted: " + room.getRoomNumber());
            }
            return deleted;
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting room", e);
            throw new BusinessException("Failed to delete room: " + e.getMessage());
        }
    }

    private void validateRoomInput(String roomNumber, int roomTypeId, int floor) throws BusinessException {
        if (!ValidationUtil.isNotEmpty(roomNumber)) {
            throw new BusinessException("Room number is required");
        }
        if (roomNumber.trim().length() > 10) {
            throw new BusinessException("Room number cannot exceed 10 characters");
        }
        if (floor < 0 || floor > 100) {
            throw new BusinessException("Floor must be between 0 and 100");
        }
        try {
            roomTypeDAO.findById(roomTypeId)
                    .orElseThrow(() -> new BusinessException("Selected room type does not exist"));
        } catch (DAOException e) {
            throw new BusinessException("Failed to validate room type: " + e.getMessage());
        }
    }

    private void checkDuplicateRoomNumber(String roomNumber, Integer excludeId) throws BusinessException {
        try {
            List<Room> all = roomDAO.findAll();
            boolean duplicate = all.stream()
                    .filter(r -> excludeId == null || !r.getRoomId().equals(excludeId))
                    .anyMatch(r -> r.getRoomNumber().equalsIgnoreCase(roomNumber.trim()));
            if (duplicate) {
                throw new BusinessException("A room with number '" + roomNumber.trim() + "' already exists");
            }
        } catch (DAOException e) {
            throw new BusinessException("Failed to validate room number: " + e.getMessage());
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

            // Use a targeted SQL COUNT instead of loading all reservations into memory
            long activeCount = reservationDAO.countActiveByRoomType(roomTypeId);

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
