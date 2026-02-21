package com.oceanview.service;

import com.oceanview.model.Reservation;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Data Transfer Object for Bill information
 */
public class ReservationBill {
    private Reservation reservation;
    private BigDecimal totalAmount;
    private BigDecimal ratePerNight;
    private long numberOfNights;
    private LocalDate calculatedDate;
    
    // Getters and Setters
    public Reservation getReservation() { return reservation; }
    public void setReservation(Reservation reservation) { this.reservation = reservation; }
    
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    
    public BigDecimal getRatePerNight() { return ratePerNight; }
    public void setRatePerNight(BigDecimal ratePerNight) { this.ratePerNight = ratePerNight; }
    
    public long getNumberOfNights() { return numberOfNights; }
    public void setNumberOfNights(long numberOfNights) { this.numberOfNights = numberOfNights; }
    
    public LocalDate getCalculatedDate() { return calculatedDate; }
    public void setCalculatedDate(LocalDate calculatedDate) { this.calculatedDate = calculatedDate; }
}
