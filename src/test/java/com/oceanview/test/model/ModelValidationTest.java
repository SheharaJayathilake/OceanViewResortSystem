package com.oceanview.test.model;

import com.oceanview.model.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Unit tests for Model validation
 */
public class ModelValidationTest {
    
    @Test
    public void testUserValidation() {
        User user = new User();
        user.setUsername("testuser");
        user.setFullName("Test User");
        user.setEmail("test@example.com");
        user.setRole(User.UserRole.RECEPTIONIST);
        
        assertEquals("Username should be set", "testuser", user.getUsername());
        assertTrue("Should have permission", user.hasPermission("VIEW_RESERVATION"));
        
        System.out.println("✓ testUserValidation passed");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUserInvalidEmail() {
        User user = new User();
        user.setEmail("invalid-email");
        
        System.out.println("✓ testUserInvalidEmail passed");
    }
    
    @Test
    public void testGuestValidation() {
        Guest guest = new Guest();
        guest.setGuestName("John Doe");
        guest.setContactNumber("0771234567");
        guest.setAddress("123 Main St");
        
        assertEquals("Guest name should be set", "John Doe", guest.getGuestName());
        
        System.out.println("✓ testGuestValidation passed");
    }
    
    @Test
    public void testRoomTypeValidation() {
        RoomType roomType = new RoomType();
        roomType.setTypeName("Deluxe");
        roomType.setRatePerNight(new BigDecimal("250.00"));
        roomType.setCapacity(2);
        
        assertEquals("Room type name should be set", "Deluxe", roomType.getTypeName());
        assertEquals("Rate should match", 0, new BigDecimal("250.00").compareTo(roomType.getRatePerNight()));
        
        System.out.println("✓ testRoomTypeValidation passed");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRoomTypeInvalidRate() {
        RoomType roomType = new RoomType();
        roomType.setRatePerNight(new BigDecimal("-100.00"));
        
        System.out.println("✓ testRoomTypeInvalidRate passed");
    }
    
    @Test
    public void testReservationCalculation() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.of(2026, 3, 1));
        reservation.setCheckOutDate(LocalDate.of(2026, 3, 5));
        
        long nights = reservation.getNumberOfNights();
        assertEquals("Should calculate 4 nights", 4, nights);
        
        BigDecimal rate = new BigDecimal("200.00");
        BigDecimal total = reservation.calculateTotal(rate);
        assertEquals("Total should be 800.00", 0, new BigDecimal("800.00").compareTo(total));
        
        System.out.println("✓ testReservationCalculation passed");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testReservationInvalidDates() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.of(2026, 3, 5));
        reservation.setCheckOutDate(LocalDate.of(2026, 3, 1)); // Before check-in
        
        System.out.println("✓ testReservationInvalidDates passed");
    }
    
    @Test
    public void testReservationStatusTransitions() {
        Reservation reservation = new Reservation();
        reservation.setCheckInDate(LocalDate.now().plusDays(1));
        reservation.setCheckOutDate(LocalDate.now().plusDays(3));
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        
        assertTrue("Pending reservation should be modifiable", reservation.isModifiable());
        assertFalse("Pending reservation cannot check-in yet", reservation.canCheckIn());
        
        reservation.setStatus(Reservation.ReservationStatus.CHECKED_IN);
        assertFalse("Checked-in reservation not modifiable", reservation.isModifiable());
        assertTrue("Checked-in reservation can check-out", reservation.canCheckOut());
        
        System.out.println("✓ testReservationStatusTransitions passed");
    }
}
