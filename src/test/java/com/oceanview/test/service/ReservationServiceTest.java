package com.oceanview.test.service;

import com.oceanview.service.ReservationService;
import com.oceanview.dao.impl.*;
import com.oceanview.model.*;
import com.oceanview.exception.BusinessException;
import com.oceanview.util.ReservationNumberGenerator;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;

/**
 * Unit tests for ReservationService
 */
public class ReservationServiceTest {

    private ReservationService reservationService;
    private Reservation testReservation;

    @Before
    public void setUp() {
        ReservationDAOImpl reservationDAO = new ReservationDAOImpl();
        GuestDAOImpl guestDAO = new GuestDAOImpl();
        RoomTypeDAOImpl roomTypeDAO = new RoomTypeDAOImpl();
        com.oceanview.dao.impl.PaymentDAOImpl paymentDAO = new com.oceanview.dao.impl.PaymentDAOImpl();
        RoomDAOImpl roomDAO = new RoomDAOImpl();

        reservationService = new ReservationService(reservationDAO, guestDAO, roomTypeDAO, paymentDAO, roomDAO);

        testReservation = new Reservation();

        try {
            // Create a unique guest for the test
            Guest newGuest = new Guest();
            newGuest.setGuestName("Test Guest " + System.currentTimeMillis());
            newGuest.setContactNumber("07" + (System.currentTimeMillis() % 100000000));
            newGuest.setAddress("123 Test Street");
            newGuest.setEmail("testguest" + System.currentTimeMillis() + "@example.com");
            newGuest.setIdentificationType("NIC");
            newGuest.setIdentificationNumber("ID" + System.currentTimeMillis());
            Guest savedGuest = guestDAO.create(newGuest);
            testReservation.setGuestId(savedGuest.getGuestId());

            // Get or create a room type to assign
            java.util.List<RoomType> types = roomTypeDAO.findAll();
            Integer typeId = types.isEmpty() ? 1 : types.get(0).getRoomTypeId();

            // Create a unique room for the test
            Room newRoom = new Room();
            newRoom.setRoomNumber("T" + (System.currentTimeMillis() % 10000));
            newRoom.setRoomTypeId(typeId);
            newRoom.setFloor(1);
            newRoom.setActive(true);
            Room savedRoom = roomDAO.create(newRoom);

            testReservation.setRoomId(savedRoom.getRoomId());
        } catch (Exception e) {
            testReservation.setGuestId(1);
            testReservation.setRoomId(101);
        }

        // Use dates far in the future to avoid conflicting with existing DB records
        testReservation.setCheckInDate(LocalDate.now().plusDays(400));
        testReservation.setCheckOutDate(LocalDate.now().plusDays(402));
        testReservation.setNumberOfGuests(2);
        testReservation.setCreatedBy(1);
    }

    @Test
    public void testCreateReservation() throws BusinessException {
        Reservation created = reservationService.createReservation(testReservation);

        assertNotNull("Reservation should be created", created);
        assertNotNull("Reservation ID should be generated", created.getReservationId());
        assertNotNull("Reservation number should be generated", created.getReservationNumber());
        assertNotNull("Total amount should be calculated", created.getTotalAmount());

        System.out.println("✓ testCreateReservation passed - Number: " + created.getReservationNumber());
    }

    @Test(expected = BusinessException.class)
    public void testCreateReservationWithInvalidDates() throws BusinessException {
        testReservation.setCheckInDate(LocalDate.now().minusDays(1)); // Past date

        reservationService.createReservation(testReservation);

        System.out.println("✓ testCreateReservationWithInvalidDates passed");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateReservationWithCheckOutBeforeCheckIn() throws BusinessException {
        testReservation.setCheckInDate(LocalDate.now().plusDays(5));
        testReservation.setCheckOutDate(LocalDate.now().plusDays(3));

        reservationService.createReservation(testReservation);

        System.out.println("✓ testCreateReservationWithCheckOutBeforeCheckIn passed");
    }

    @Test
    public void testCalculateNumberOfNights() {
        testReservation.setCheckInDate(LocalDate.of(2026, 3, 1));
        testReservation.setCheckOutDate(LocalDate.of(2026, 3, 4));

        long nights = testReservation.getNumberOfNights();

        assertEquals("Should calculate 3 nights", 3, nights);

        System.out.println("✓ testCalculateNumberOfNights passed");
    }

    @Test
    public void testReservationNumberGeneration() {
        String number1 = ReservationNumberGenerator.generate();
        String number2 = ReservationNumberGenerator.generate();

        assertNotNull("Number 1 should not be null", number1);
        assertNotNull("Number 2 should not be null", number2);
        assertNotEquals("Numbers should be unique", number1, number2);
        assertTrue("Number should match pattern", number1.matches("RES-\\d{8}-\\d{4}"));

        System.out.println("✓ testReservationNumberGeneration passed - " + number1);
    }
}
