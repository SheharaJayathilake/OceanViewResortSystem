package com.oceanview.controller.api;

import com.google.gson.Gson;
import com.oceanview.model.Guest;
import com.oceanview.service.GuestService;
import com.oceanview.factory.ServiceFactory;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * REST API Controller for Guest Data
 * Satisfies "Distributed application with web services" requirement
 * Returns JSON data
 */
@WebServlet(name = "GuestApiController", urlPatterns = { "/api/guests/*" })
public class GuestApiController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private GuestService guestService;
    private final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        super.init();
        guestService = ServiceFactory.getInstance().getGuestService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set response type to JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || "/".equals(pathInfo)) {
                // Return all guests
                List<Guest> guests = guestService.getAllGuests();
                String json = gson.toJson(guests);
                out.print(json);
            } else {
                // Return specific guest by ID
                try {
                    String idStr = pathInfo.substring(1); // remove leading slash
                    int guestId = Integer.parseInt(idStr);

                    Guest guest = guestService.findGuestById(guestId);
                    if (guest != null) {
                        String json = gson.toJson(guest);
                        out.print(json);
                    } else {
                        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        out.print("{\"error\": \"Guest not found\"}");
                    }
                } catch (NumberFormatException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.print("{\"error\": \"Invalid ID format\"}");
                }
            }
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"error\": \"" + e.getMessage() + "\"}");
        }

        out.flush();
    }
}
