package com.oceanview.controller;

import com.oceanview.model.Guest;
import com.oceanview.service.GuestService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/**
 * Guest Controller - Complete guest management
 */
@WebServlet(name = "GuestController", urlPatterns = { "/guests", "/guests/*" })
public class GuestController extends BaseController {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAuthentication(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            listGuests(request, response);
        } else if ("/new".equals(pathInfo)) {
            showNewGuestForm(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            viewGuest(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditGuestForm(request, response);
        } else if ("/search".equals(pathInfo)) {
            searchGuests(request, response);
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
            createGuest(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update/")) {
            updateGuest(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            deleteGuest(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * List all guests
     */
    private void listGuests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            GuestService guestService = serviceFactory.getGuestService();
            List<Guest> guests = guestService.getAllGuests();

            request.setAttribute("guests", guests);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/guests/list.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error listing guests", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/error.jsp");
        }
    }

    /**
     * Show new guest registration form
     */
    private void showNewGuestForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("errorMessage", getErrorMessage(request));
        forwardToView(request, response, "/WEB-INF/views/guests/new.jsp");
    }

    /**
     * Create new guest
     */
    private void createGuest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            String guestName = request.getParameter("guestName");
            String address = request.getParameter("address");
            String contactNumber = request.getParameter("contactNumber");
            String email = request.getParameter("email");
            String identificationType = request.getParameter("identificationType");
            String identificationNumber = request.getParameter("identificationNumber");

            Guest guest = new Guest();
            guest.setGuestName(guestName);
            guest.setAddress(address);
            guest.setContactNumber(contactNumber);
            guest.setEmail(email);
            guest.setIdentificationType(identificationType);
            guest.setIdentificationNumber(identificationNumber);

            GuestService guestService = serviceFactory.getGuestService();
            Guest createdGuest = guestService.registerGuest(guest);

            setSuccessMessage(request,
                    "Guest registered successfully! Guest ID: " + createdGuest.getGuestId());

            response.sendRedirect(request.getContextPath() + "/guests/view/" + createdGuest.getGuestId());

        } catch (IllegalArgumentException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Guest creation failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/guests/new");
        }
    }

    /**
     * View guest details
     */
    private void viewGuest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String guestIdStr = request.getPathInfo().substring("/view/".length());
            Integer guestId = Integer.parseInt(guestIdStr);

            GuestService guestService = serviceFactory.getGuestService();
            Guest guest = guestService.findGuestById(guestId);

            request.setAttribute("guest", guest);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/guests/view.jsp");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid guest ID format", e);
            setErrorMessage(request, "Invalid guest ID");
            response.sendRedirect(request.getContextPath() + "/guests");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Guest not found", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/guests");
        }
    }

    /**
     * Show edit guest form
     */
    private void showEditGuestForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String guestIdStr = request.getPathInfo().substring("/edit/".length());
            Integer guestId = Integer.parseInt(guestIdStr);

            GuestService guestService = serviceFactory.getGuestService();
            Guest guest = guestService.findGuestById(guestId);

            request.setAttribute("guest", guest);
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/guests/edit.jsp");

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Error loading guest for edit", e);
            setErrorMessage(request, "Failed to load guest information");
            response.sendRedirect(request.getContextPath() + "/guests");
        }
    }

    /**
     * Update guest
     */
    private void updateGuest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            String guestIdStr = request.getPathInfo().substring("/update/".length());
            Integer guestId = Integer.parseInt(guestIdStr);

            String guestName = request.getParameter("guestName");
            String address = request.getParameter("address");
            String contactNumber = request.getParameter("contactNumber");
            String email = request.getParameter("email");
            String identificationType = request.getParameter("identificationType");
            String identificationNumber = request.getParameter("identificationNumber");

            Guest guest = new Guest();
            guest.setGuestId(guestId);
            guest.setGuestName(guestName);
            guest.setAddress(address);
            guest.setContactNumber(contactNumber);
            guest.setEmail(email);
            guest.setIdentificationType(identificationType);
            guest.setIdentificationNumber(identificationNumber);

            GuestService guestService = serviceFactory.getGuestService();
            boolean updated = guestService.updateGuest(guest);

            if (updated) {
                setSuccessMessage(request, "Guest information updated successfully!");
            } else {
                setErrorMessage(request, "Failed to update guest information");
            }

            response.sendRedirect(request.getContextPath() + "/guests/view/" + guestId);

        } catch (IllegalArgumentException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Guest update failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/guests");
        }
    }

    /**
     * Delete guest
     */
    private void deleteGuest(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            String guestIdStr = request.getPathInfo().substring("/delete/".length());
            Integer guestId = Integer.parseInt(guestIdStr);

            GuestService guestService = serviceFactory.getGuestService();
            boolean deleted = guestService.deleteGuest(guestId);

            if (deleted) {
                setSuccessMessage(request, "Guest deleted successfully!");
            } else {
                setErrorMessage(request, "Failed to delete guest");
            }

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Guest deletion failed", e);
            setErrorMessage(request, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/guests");
    }

    /**
     * Search guests
     */
    private void searchGuests(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = request.getParameter("q");

        try {
            GuestService guestService = serviceFactory.getGuestService();
            List<Guest> guests = guestService.searchGuests(searchTerm);

            request.setAttribute("guests", guests);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("searchResults", true);

            forwardToView(request, response, "/WEB-INF/views/guests/list.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "Guest search failed", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/guests/list.jsp");
        }
    }
}
