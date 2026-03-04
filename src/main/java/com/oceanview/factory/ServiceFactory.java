package com.oceanview.factory;

import com.oceanview.service.*;

/**
 * Factory for Service layer objects
 */
public class ServiceFactory {
    private static ServiceFactory instance;
    private final DAOFactory daoFactory;

    private ServiceFactory() {
        this.daoFactory = DAOFactory.getInstance();
    }

    public static synchronized ServiceFactory getInstance() {
        if (instance == null) {
            instance = new ServiceFactory();
        }
        return instance;
    }

    public AuthenticationService getAuthenticationService() {
        return new AuthenticationService(daoFactory.getUserDAO());
    }

    public ReservationService getReservationService() {
        return new ReservationService(
                daoFactory.getReservationDAO(),
                daoFactory.getGuestDAO(),
                daoFactory.getRoomTypeDAO(),
                daoFactory.getPaymentDAO(),
                daoFactory.getRoomDAO());
    }

    public RoomService getRoomService() {
        return new RoomService(
                daoFactory.getRoomTypeDAO(),
                daoFactory.getRoomDAO(),
                daoFactory.getReservationDAO());
    }

    public GuestService getGuestService() {
        return new GuestService(daoFactory.getGuestDAO());
    }

    public ReportService getReportService() {
        return new ReportService(
                daoFactory.getReservationDAO(),
                daoFactory.getGuestDAO());
    }

    public UserService getUserService() {
        return new UserService(daoFactory.getUserDAO());
    }
}
