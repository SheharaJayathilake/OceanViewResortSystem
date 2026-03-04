package com.oceanview.controller;

import com.oceanview.model.RoomType;
import com.oceanview.model.Room;
import com.oceanview.service.RoomService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Controller for room management (room types + individual rooms).
 * Routes: /rooms/* handles both room types and individual rooms.
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
        } else if (pathInfo.equals("/room/new")) {
            showRoomForm(request, response, null);
        } else if (pathInfo.startsWith("/room/edit/")) {
            String idStr = pathInfo.substring("/room/edit/".length());
            showRoomForm(request, response, idStr);
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
        } else if (pathInfo != null && pathInfo.equals("/room/create")) {
            createRoom(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/room/update/")) {
            updateRoom(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/room/delete/")) {
            deleteRoom(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    // Room Types

    private void listRoomTypes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();
            List<RoomType> roomTypes = roomService.getAllRoomTypes();
            request.setAttribute("roomTypes", roomTypes);

            Map<Integer, List<Room>> roomsByType = new HashMap<>();
            for (RoomType rt : roomTypes) {
                List<Room> rooms = roomService.getRoomsByType(rt.getRoomTypeId());
                roomsByType.put(rt.getRoomTypeId(), rooms);
            }
            request.setAttribute("roomsByType", roomsByType);

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
            }
            forwardToView(request, response, "/WEB-INF/views/rooms/form.jsp");
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room type ID");
            response.sendRedirect(request.getContextPath() + "/rooms");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    private void createRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();
            roomService.createRoomType(
                    request.getParameter("typeName"),
                    new BigDecimal(request.getParameter("ratePerNight")),
                    Integer.parseInt(request.getParameter("capacity")),
                    request.getParameter("description"));
            setSuccessMessage(request, "Room type created successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid numeric input");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void updateRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring("/update/".length()));
            boolean isAvailable = "on".equals(request.getParameter("isAvailable"))
                    || "true".equals(request.getParameter("isAvailable"));

            RoomService roomService = serviceFactory.getRoomService();
            roomService.updateRoomType(id,
                    request.getParameter("typeName"),
                    new BigDecimal(request.getParameter("ratePerNight")),
                    Integer.parseInt(request.getParameter("capacity")),
                    request.getParameter("description"),
                    isAvailable);
            setSuccessMessage(request, "Room type updated successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid input");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void deleteRoomType(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            int id = Integer.parseInt(pathInfo.substring("/delete/".length()));
            RoomService roomService = serviceFactory.getRoomService();
            roomService.deleteRoomType(id);
            setSuccessMessage(request, "Room type deleted successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room type ID");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    // Individual Rooms

    private void showRoomForm(HttpServletRequest request, HttpServletResponse response, String idStr)
            throws ServletException, IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();
            List<RoomType> roomTypes = roomService.getAllRoomTypes();
            request.setAttribute("roomTypes", roomTypes);

            if (idStr != null) {
                int id = Integer.parseInt(idStr);
                Room room = roomService.findRoomById(id);
                request.setAttribute("room", room);
            }
            forwardToView(request, response, "/WEB-INF/views/rooms/room-form.jsp");
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room ID");
            response.sendRedirect(request.getContextPath() + "/rooms");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/rooms");
        }
    }

    private void createRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            RoomService roomService = serviceFactory.getRoomService();
            String roomNumber = request.getParameter("roomNumber");
            int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
            int floor = Integer.parseInt(request.getParameter("floor"));
            boolean isActive = "on".equals(request.getParameter("isActive"))
                    || "true".equals(request.getParameter("isActive"));

            roomService.createRoom(roomNumber, roomTypeId, floor, isActive);
            setSuccessMessage(request, "Room '" + roomNumber + "' created successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid numeric input");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void updateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            int roomId = Integer.parseInt(pathInfo.substring("/room/update/".length()));
            String roomNumber = request.getParameter("roomNumber");
            int roomTypeId = Integer.parseInt(request.getParameter("roomTypeId"));
            int floor = Integer.parseInt(request.getParameter("floor"));
            boolean isActive = "on".equals(request.getParameter("isActive"))
                    || "true".equals(request.getParameter("isActive"));

            RoomService roomService = serviceFactory.getRoomService();
            roomService.updateRoom(roomId, roomNumber, roomTypeId, floor, isActive);
            setSuccessMessage(request, "Room '" + roomNumber + "' updated successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid input");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }

    private void deleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String pathInfo = request.getPathInfo();
            int roomId = Integer.parseInt(pathInfo.substring("/room/delete/".length()));
            RoomService roomService = serviceFactory.getRoomService();
            roomService.deleteRoom(roomId);
            setSuccessMessage(request, "Room deleted successfully");
        } catch (BusinessException e) {
            setErrorMessage(request, e.getMessage());
        } catch (NumberFormatException e) {
            setErrorMessage(request, "Invalid room ID");
        }
        response.sendRedirect(request.getContextPath() + "/rooms");
    }
}
