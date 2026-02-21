package com.oceanview.factory;

import com.oceanview.dao.impl.*;

/**
 * Factory pattern for creating DAO instances
 * Implements Factory Design Pattern for loose coupling
 */
public class DAOFactory {
    private static DAOFactory instance;
    
    private DAOFactory() {}
    
    public static synchronized DAOFactory getInstance() {
        if (instance == null) {
            instance = new DAOFactory();
        }
        return instance;
    }
    
    public UserDAOImpl getUserDAO() {
        return new UserDAOImpl();
    }
    
    public GuestDAOImpl getGuestDAO() {
        return new GuestDAOImpl();
    }
    
    public ReservationDAOImpl getReservationDAO() {
        return new ReservationDAOImpl();
    }
    
    public RoomTypeDAOImpl getRoomTypeDAO() {
        return new RoomTypeDAOImpl();
    }
}
