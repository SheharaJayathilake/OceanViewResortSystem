package com.oceanview.service;

import com.oceanview.dao.impl.*;
import com.oceanview.model.Reservation;
import com.oceanview.exception.BusinessException;
import com.oceanview.exception.DAOException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Report Service for generating business reports
 */
public class ReportService {
    private static final Logger LOGGER = Logger.getLogger(ReportService.class.getName());
    
    private final ReservationDAOImpl reservationDAO;
    private final GuestDAOImpl guestDAO;
    
    public ReportService(ReservationDAOImpl reservationDAO, GuestDAOImpl guestDAO) {
        this.reservationDAO = reservationDAO;
        this.guestDAO = guestDAO;
    }
    
    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStatistics() throws BusinessException {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            long totalReservations = reservationDAO.count();
            long totalGuests = guestDAO.count();
            
            List<Reservation> allReservations = reservationDAO.findAll();
            
            long activeReservations = allReservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED ||
                            r.getStatus() == Reservation.ReservationStatus.CHECKED_IN)
                .count();
            
            long completedReservations = allReservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CHECKED_OUT)
                .count();
            
            stats.put("totalReservations", totalReservations);
            stats.put("totalGuests", totalGuests);
            stats.put("activeReservations", activeReservations);
            stats.put("completedReservations", completedReservations);
            
            LOGGER.info("Dashboard statistics generated");
            return stats;
            
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error generating statistics", e);
            throw new BusinessException("Failed to generate statistics: " + e.getMessage());
        }
    }
    
    /**
     * Get reservations grouped by status
     */
    public Map<Reservation.ReservationStatus, List<Reservation>> getReservationsByStatus() 
            throws BusinessException {
        try {
            List<Reservation> allReservations = reservationDAO.findAll();
            
            return allReservations.stream()
                .collect(Collectors.groupingBy(Reservation::getStatus));
                
        } catch (DAOException e) {
            LOGGER.log(Level.SEVERE, "Error generating report", e);
            throw new BusinessException("Failed to generate report: " + e.getMessage());
        }
    }
}
