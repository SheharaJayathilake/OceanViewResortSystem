package com.oceanview.controller;

import com.oceanview.model.*;
import com.oceanview.service.*;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;

/**
 * Front Controller for all reservation operations
 * Implements Command pattern for action handling
 */
@WebServlet(name = "ReservationController", urlPatterns = { "/reservations/*" })
public class ReservationController extends BaseController {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAuthentication(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            listReservations(request, response);
        } else if ("/new".equals(pathInfo)) {
            showNewReservationForm(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            viewReservation(request, response);
        } else if (pathInfo.startsWith("/bill/")) {
            generateBill(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAuthentication(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if ("/create".equals(pathInfo)) {
            createReservation(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update-status/")) {
            updateReservationStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * List all reservations
     */
    private void listReservations(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            ReservationService reservationService = serviceFactory.getReservationService();
            List<Reservation> reservations = reservationService.getAllReservations();

            request.setAttribute("reservations", reservations);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/reservations/list.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error listing reservations", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/error.jsp");
        }
    }

    /**
     * Show new reservation form
     */
    private void showNewReservationForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            GuestService guestService = serviceFactory.getGuestService();
            List<Guest> guests = guestService.getAllGuests();

            request.setAttribute("guests", guests);

            // Load room types through the service layer (not direct DAO)
            RoomService roomService = serviceFactory.getRoomService();
            List<RoomType> roomTypes = roomService.getAllRoomTypes();
            request.setAttribute("roomTypes", roomTypes);

            forwardToView(request, response, "/WEB-INF/views/reservations/new.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error loading reservation form", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reservations");
        }
    }

    /**
     * Create new reservation
     */
    private void createReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Parse form data
            Integer guestId = Integer.parseInt(request.getParameter("guestId"));
            Integer roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
            LocalDate checkInDate = LocalDate.parse(request.getParameter("checkInDate"));
            LocalDate checkOutDate = LocalDate.parse(request.getParameter("checkOutDate"));
            Integer numberOfGuests = Integer.parseInt(request.getParameter("numberOfGuests"));
            String specialRequests = request.getParameter("specialRequests");

            // Create reservation object
            Reservation reservation = new Reservation();
            reservation.setGuestId(guestId);
            reservation.setRoomTypeId(roomTypeId);
            reservation.setCheckInDate(checkInDate);
            reservation.setCheckOutDate(checkOutDate);
            reservation.setNumberOfGuests(numberOfGuests);
            reservation.setSpecialRequests(specialRequests);
            reservation.setCreatedBy(getCurrentUser(request).getUserId());

            // Create reservation
            ReservationService reservationService = serviceFactory.getReservationService();
            Reservation createdReservation = reservationService.createReservation(reservation);

            setSuccessMessage(request,
                    "Reservation created successfully! Reservation Number: " +
                            createdReservation.getReservationNumber());

            response.sendRedirect(request.getContextPath() +
                    "/reservations/view/" + createdReservation.getReservationNumber());

        } catch (NumberFormatException | DateTimeParseException e) {
            LOGGER.log(Level.WARNING, "Invalid form data", e);
            setErrorMessage(request, "Invalid input data. Please check all fields.");
            response.sendRedirect(request.getContextPath() + "/reservations/new");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Business validation failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reservations/new");
        }
    }

    /**
     * View reservation details
     */
    private void viewReservation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationNumber = request.getPathInfo().substring("/view/".length());

        try {
            ReservationService reservationService = serviceFactory.getReservationService();
            Reservation reservation = reservationService.findReservationByNumber(reservationNumber);

            request.setAttribute("reservation", reservation);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/reservations/view.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Reservation not found: " + reservationNumber, e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reservations");
        }
    }

    /**
     * Generate and display bill
     */
    private void generateBill(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String reservationNumber = request.getPathInfo().substring("/bill/".length());

        try {
            ReservationService reservationService = serviceFactory.getReservationService();
            ReservationBill bill = reservationService.calculateBill(reservationNumber);

            request.setAttribute("bill", bill);
            forwardToView(request, response, "/WEB-INF/views/reservations/bill.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Error generating bill", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reservations");
        }
    }

    /**
     * Update reservation status (confirm, check-in, check-out, cancel)
     */
    private void updateReservationStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String reservationNumber = request.getPathInfo().substring("/update-status/".length());
        String action = request.getParameter("action");

        try {
            ReservationService reservationService = serviceFactory.getReservationService();
            boolean updated = false;

            switch (action) {
                case "confirm":
                    updated = reservationService.confirmReservation(reservationNumber);
                    setSuccessMessage(request, "Reservation confirmed successfully");
                    break;

                case "checkin":
                    updated = reservationService.checkInReservation(reservationNumber);
                    setSuccessMessage(request, "Guest checked-in successfully");
                    break;

                case "checkout":
                    updated = reservationService.checkOutReservation(reservationNumber);
                    setSuccessMessage(request, "Guest checked-out successfully");
                    break;

                case "cancel":
                    updated = reservationService.cancelReservation(reservationNumber);
                    setSuccessMessage(request, "Reservation cancelled successfully");
                    break;

                case "pay":
                    String paymentMethod = request.getParameter("paymentMethod");
                    String transactionRef = request.getParameter("transactionRef");
                    com.oceanview.model.User currentUser = getCurrentUser(request);
                    int processedBy = (currentUser != null) ? currentUser.getUserId() : 0;
                    updated = reservationService.payReservation(
                            reservationNumber, paymentMethod, transactionRef, processedBy);
                    setSuccessMessage(request, "Payment processed successfully via " + paymentMethod);
                    break;

                default:
                    setErrorMessage(request, "Invalid action");
            }

            if (!updated) {
                setErrorMessage(request, "Failed to update reservation status");
            }

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Error updating status", e);
            setErrorMessage(request, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/reservations/view/" + reservationNumber);
    }
}
