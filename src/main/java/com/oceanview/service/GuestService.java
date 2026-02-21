package com.oceanview.service;

import com.oceanview.dao.impl.GuestDAOImpl;
import com.oceanview.model.Guest;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;
import com.oceanview.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Guest Service implementing business logic for guest management
 */
public class GuestService {
    private static final Logger LOGGER = Logger.getLogger(GuestService.class.getName());
    private final GuestDAOImpl guestDAO;
    
    public GuestService(GuestDAOImpl guestDAO) {
        this.guestDAO = guestDAO;
    }
    
    /**
     * Register new guest with validation
     * @param guest Guest object
     * @return Created guest with ID
     * @throws BusinessException if validation fails
     */
    public Guest registerGuest(Guest guest) throws BusinessException {
        validateGuest(guest);
        
        try {
            // Check for duplicate contact number
            Optional<Guest> existingGuest = guestDAO.findByContact(guest.getContactNumber());
            
            if (existingGuest.isPresent()) {
                LOGGER.info("Guest already exists with contact: " + guest.getContactNumber());
                return existingGuest.get();
            }
            
            Guest createdGuest = guestDAO.create(guest);
            LOGGER.info("New guest registered: " + createdGuest.getGuestId());
            return createdGuest;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error registering guest", e);
            throw new BusinessException("Failed to register guest: " + e.getMessage());
        }
    }
    
    /**
     * Update guest information
     * @param guest Guest object with updates
     * @return true if update successful
     * @throws BusinessException if validation or update fails
     */
    public boolean updateGuest(Guest guest) throws BusinessException {
        if (guest.getGuestId() == null) {
            throw new BusinessException("Guest ID is required for update");
        }
        
        validateGuest(guest);
        
        try {
            // Verify guest exists
            Optional<Guest> existingGuest = guestDAO.findById(guest.getGuestId());
            if (!existingGuest.isPresent()) {
                throw new BusinessException("Guest not found with ID: " + guest.getGuestId());
            }
            
            boolean updated = guestDAO.update(guest);
            
            if (updated) {
                LOGGER.info("Guest updated: " + guest.getGuestId());
            }
            
            return updated;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error updating guest", e);
            throw new BusinessException("Failed to update guest: " + e.getMessage());
        }
    }
    
    /**
     * Find guest by ID
     * @param guestId Guest ID
     * @return Guest object
     * @throws BusinessException if guest not found
     */
    public Guest findGuestById(Integer guestId) throws BusinessException {
        try {
            Optional<Guest> guest = guestDAO.findById(guestId);
            
            if (!guest.isPresent()) {
                throw new BusinessException("Guest not found with ID: " + guestId);
            }
            
            return guest.get();
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error finding guest", e);
            throw new BusinessException("Failed to retrieve guest: " + e.getMessage());
        }
    }
    
    /**
     * Search guests by name or contact
     * @param searchTerm Search term
     * @return List of matching guests
     * @throws BusinessException if search fails
     */
    public List<Guest> searchGuests(String searchTerm) throws BusinessException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new BusinessException("Search term is required");
        }
        
        try {
            List<Guest> guests = guestDAO.searchGuests(searchTerm.trim());
            LOGGER.info("Found " + guests.size() + " guests matching: " + searchTerm);
            return guests;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error searching guests", e);
            throw new BusinessException("Failed to search guests: " + e.getMessage());
        }
    }
    
    /**
     * Get all guests
     * @return List of all guests
     * @throws BusinessException if retrieval fails
     */
    public List<Guest> getAllGuests() throws BusinessException {
        try {
            return guestDAO.findAll();
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving all guests", e);
            throw new BusinessException("Failed to retrieve guests: " + e.getMessage());
        }
    }
    
    /**
     * Delete guest
     * @param guestId Guest ID
     * @return true if deletion successful
     * @throws BusinessException if deletion fails
     */
    public boolean deleteGuest(Integer guestId) throws BusinessException {
        try {
            boolean deleted = guestDAO.delete(guestId);
            
            if (deleted) {
                LOGGER.info("Guest deleted: " + guestId);
            }
            
            return deleted;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting guest", e);
            throw new BusinessException("Failed to delete guest. May have active reservations.");
        }
    }
    
    /**
     * Validate guest data
     * @param guest Guest to validate
     * @throws BusinessException if validation fails
     */
    private void validateGuest(Guest guest) throws BusinessException {
        if (guest == null) {
            throw new BusinessException("Guest information is required");
        }
        
        if (!ValidationUtil.isValidName(guest.getGuestName())) {
            throw new BusinessException("Invalid guest name format");
        }
        
        if (!ValidationUtil.isValidPhone(guest.getContactNumber())) {
            throw new BusinessException("Invalid contact number format. Use 10-15 digits.");
        }
        
        if (guest.getEmail() != null && !guest.getEmail().isEmpty() && 
            !ValidationUtil.isValidEmail(guest.getEmail())) {
            throw new BusinessException("Invalid email format");
        }
        
        if (!ValidationUtil.isNotEmpty(guest.getAddress())) {
            throw new BusinessException("Address is required");
        }
    }
}
