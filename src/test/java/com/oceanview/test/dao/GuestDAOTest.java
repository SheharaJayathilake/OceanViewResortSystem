package com.oceanview.test.dao;

import com.oceanview.dao.impl.GuestDAOImpl;
import com.oceanview.model.Guest;
import com.oceanview.exception.DAOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.List;
import java.util.Optional;

/**
 * Unit tests for GuestDAOImpl
 */
public class GuestDAOTest {
    
    private GuestDAOImpl guestDAO;
    private Guest testGuest;
    
    @Before
    public void setUp() {
        guestDAO = new GuestDAOImpl();
        
        testGuest = new Guest();
        testGuest.setGuestName("Test Guest " + System.currentTimeMillis());
        testGuest.setAddress("123 Test Street, Test City");
        testGuest.setContactNumber("0771234567");
        testGuest.setEmail("testguest@example.com");
        testGuest.setIdentificationType("NIC");
        testGuest.setIdentificationNumber("123456789V");
    }
    
    @After
    public void tearDown() {
        if (testGuest != null && testGuest.getGuestId() != null) {
            try {
                guestDAO.delete(testGuest.getGuestId());
            } catch (DAOException e) {
                // Ignore cleanup errors
            }
        }
    }
    
    @Test
    public void testCreateGuest() throws DAOException {
        Guest createdGuest = guestDAO.create(testGuest);
        
        assertNotNull("Created guest should not be null", createdGuest);
        assertNotNull("Guest ID should be generated", createdGuest.getGuestId());
        assertEquals("Name should match", testGuest.getGuestName(), createdGuest.getGuestName());
        assertEquals("Contact should match", testGuest.getContactNumber(), createdGuest.getContactNumber());
        
        System.out.println("✓ testCreateGuest passed");
    }
    
    @Test
    public void testFindGuestById() throws DAOException {
        Guest createdGuest = guestDAO.create(testGuest);
        
        Optional<Guest> foundGuest = guestDAO.findById(createdGuest.getGuestId());
        
        assertTrue("Guest should be found", foundGuest.isPresent());
        assertEquals("Name should match", testGuest.getGuestName(), foundGuest.get().getGuestName());
        
        System.out.println("✓ testFindGuestById passed");
    }
    
    @Test
    public void testFindGuestByContact() throws DAOException {
        Guest createdGuest = guestDAO.create(testGuest);
        
        Optional<Guest> foundGuest = guestDAO.findByContact(testGuest.getContactNumber());
        
        assertTrue("Guest should be found by contact", foundGuest.isPresent());
        assertEquals("Guest ID should match", createdGuest.getGuestId(), foundGuest.get().getGuestId());
        
        System.out.println("✓ testFindGuestByContact passed");
    }
    
    @Test
    public void testUpdateGuest() throws DAOException {
        Guest createdGuest = guestDAO.create(testGuest);
        
        createdGuest.setGuestName("Updated Name");
        createdGuest.setEmail("updated@example.com");
        
        boolean updated = guestDAO.update(createdGuest);
        
        assertTrue("Update should succeed", updated);
        
        Optional<Guest> updatedGuest = guestDAO.findById(createdGuest.getGuestId());
        assertTrue("Guest should exist", updatedGuest.isPresent());
        assertEquals("Name should be updated", "Updated Name", updatedGuest.get().getGuestName());
        
        System.out.println("✓ testUpdateGuest passed");
    }
    
    @Test
    public void testSearchGuests() throws DAOException {
        guestDAO.create(testGuest);
        
        List<Guest> results = guestDAO.searchGuests("Test Guest");
        
        assertNotNull("Results should not be null", results);
        assertTrue("Should find at least one guest", results.size() > 0);
        
        System.out.println("✓ testSearchGuests passed - Found " + results.size() + " guests");
    }
    
    @Test
    public void testDeleteGuest() throws DAOException {
        Guest createdGuest = guestDAO.create(testGuest);
        Integer guestId = createdGuest.getGuestId();
        
        boolean deleted = guestDAO.delete(guestId);
        
        assertTrue("Delete should succeed", deleted);
        
        Optional<Guest> deletedGuest = guestDAO.findById(guestId);
        assertFalse("Guest should not exist after deletion", deletedGuest.isPresent());
        
        testGuest = null; // Prevent cleanup attempt
        System.out.println("✓ testDeleteGuest passed");
    }
    
    @Test(expected = DAOException.class)
    public void testCreateGuestWithNullData() throws DAOException {
        guestDAO.create(null);
        System.out.println("✓ testCreateGuestWithNullData passed");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGuestWithInvalidName() {
        Guest invalidGuest = new Guest();
        invalidGuest.setGuestName("A"); // Too short
        System.out.println("✓ testCreateGuestWithInvalidName passed");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateGuestWithInvalidContact() {
        Guest invalidGuest = new Guest();
        invalidGuest.setContactNumber("123"); // Too short
        System.out.println("✓ testCreateGuestWithInvalidContact passed");
    }
}
