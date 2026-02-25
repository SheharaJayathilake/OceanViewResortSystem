package com.oceanview.test.service;

import com.oceanview.service.GuestService;
import com.oceanview.dao.impl.GuestDAOImpl;
import com.oceanview.model.Guest;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Optional;

public class GuestServiceTest {

    private GuestService guestService;
    private Guest testGuest;

    private class GuestDAOStub extends GuestDAOImpl {
        @Override
        public Optional<Guest> findByContact(String contactNumber) throws DAOException {
            return Optional.empty();
        }

        @Override
        public Guest create(Guest guest) throws DAOException {
            guest.setGuestId(1);
            return guest;
        }
    }

    @Before
    public void setUp() {
        GuestDAOStub stubDAO = new GuestDAOStub();
        guestService = new GuestService(stubDAO);

        testGuest = new Guest();
        // Set valid defaults
        testGuest.setGuestName("Test User");
        testGuest.setContactNumber("0771234567");
        testGuest.setEmail("test@example.com");
        testGuest.setAddress("123 Test Lane");
    }

    @Test
    public void testRegisterValidGuest() {
        try {
            Guest result = guestService.registerGuest(testGuest);
            assertNotNull("Registered guest should not be null", result);
            assertEquals("Test User", result.getGuestName());
        } catch (BusinessException e) {
            fail("Valid guest should not throw exception: " + e.getMessage());
        }
    }

    @Test(expected = BusinessException.class)
    public void testRegisterGuestWithEmptyName() throws BusinessException {
        testGuest.setGuestName("");
        guestService.registerGuest(testGuest);
    }

    @Test(expected = BusinessException.class)
    public void testRegisterGuestWithInvalidContact() throws BusinessException {
        testGuest.setContactNumber("123"); // Too short
        guestService.registerGuest(testGuest);
    }

    @Test(expected = BusinessException.class)
    public void testRegisterGuestWithMissingAddress() throws BusinessException {
        testGuest.setAddress(null);
        guestService.registerGuest(testGuest);
    }
}
