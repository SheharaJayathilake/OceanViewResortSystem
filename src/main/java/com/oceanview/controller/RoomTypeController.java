package com.oceanview.controller;

import com.oceanview.model.RoomType;
import com.oceanview.service.RoomService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;

/**
 * Controller for room type management (CRUD).
 * Handles listing, creating, editing, and deleting room types.
 */
@WebServlet(name = "RoomTypeController", urlPatterns = { "/rooms/*" })
public class RoomTypeController extends BaseController {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireAuthentication(request, response))
            return;

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            listRoomTypes(request, response);
        } else if (pathInfo.equals("/new")) {
            showForm(request, response, null);
        } else if (pathInfo.startsWith("/edit/")) {
            String idStr = pathInfo.substring("/edit/".length());
            showForm(request, response, idStr);
        } else {
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!requireAuthentication(request, response))
            return;

        String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.equals("/create")) {
            createRoomType(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update/")) {
            updateRoomType(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            deleteRoomType(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    private void listRoomTypes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();
            List<RoomType> roomTypes = roomService.getAllRoomTypes();

            request.setAttribute("roomTypes", roomTypes);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/rooms/list.jsp");
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error listing room types", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/rooms/list.jsp");
        }
    }

    private void showForm(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        try {
            if (idStr != null) {
                RoomService roomService = serviceFactory.getRoomService();
                int id = Integer.parseInt(idStr);
                RoomType roomType = roomService.findById(id);
                request.setAttribute("roomType", roomType);
                request.setAttribute("formTitle", "EDIT ROOM TYPE");
                request.setAttribute("formAction",
                        request.getContextPath() + "/rooms/update/" + id);
            } else {
                request.setAttribute("formTitle", "ADD NEW ROOM TYPE");
                request.setAttribute("formAction",
                        request.getContextPath() + "/rooms/create");
            }
            forwardToView(request, response, "/WEB-INF/views/rooms/form.jsp");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms");
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room type ID");
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    private void createRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();

            String typeName = request.getParameter("typeName");
            BigDecimal rate = new BigDecimal(request.getParameter("ratePerNight"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String description = request.getParameter("description");

            roomService.createRoomType(typeName, rate, capacity, description);

            setSuccessMessage(request, "Room type '" + typeName + "' created successfully");
            response.sendRedirect(request.getContextPath() + "/rooms");

        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms/new");
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid number format for rate or capacity");
            response.sendRedirect(request.getContextPath() + "/rooms/new");
        }
    }

    private void updateRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getPathInfo().substring("/update/".length());
        try {
            RoomService roomService = serviceFactory.getRoomService();
            int id = Integer.parseInt(idStr);

            String typeName = request.getParameter("typeName");
            BigDecimal rate = new BigDecimal(request.getParameter("ratePerNight"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));
            String description = request.getParameter("description");
            boolean isAvailable = "on".equals(request.getParameter("isAvailable"));

            roomService.updateRoomType(id, typeName, rate, capacity, description, isAvailable);

            setSuccessMessage(request, "Room type '" + typeName + "' updated successfully");
            response.sendRedirect(request.getContextPath() + "/rooms");

        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms/edit/" + idStr);
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid number format");
            response.sendRedirect(request.getContextPath() + "/rooms/edit/" + idStr);
        }
    }

    private void deleteRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String idStr = request.getPathInfo().substring("/delete/".length());
        try {
            RoomService roomService = serviceFactory.getRoomService();
            int id = Integer.parseInt(idStr);

            roomService.deleteRoomType(id);

            setSuccessMessage(request, "Room type deleted successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room type ID");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }
}
