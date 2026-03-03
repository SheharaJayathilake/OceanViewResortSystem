package com.oceanview.controller;

import com.oceanview.model.User;
import com.oceanview.model.User.UserRole;
import com.oceanview.service.UserService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

/** Admin-only user management controller */
@WebServlet(name = "UserController", urlPatterns = { "/users", "/users/*" })
public class UserController extends BaseController {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!requireAuthentication(request, response)) {
            return;
        }

        if (!requireAdminRole(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            listUsers(request, response);
        } else if ("/new".equals(pathInfo)) {
            showNewUserForm(request, response);
        } else if (pathInfo.startsWith("/view/")) {
            viewUser(request, response);
        } else if (pathInfo.startsWith("/edit/")) {
            showEditUserForm(request, response);
        } else if (pathInfo.startsWith("/reset-password/")) {
            showResetPasswordForm(request, response);
        } else if ("/search".equals(pathInfo)) {
            searchUsers(request, response);
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

        if (!requireAdminRole(request, response)) {
            return;
        }

        String pathInfo = request.getPathInfo();

        if ("/create".equals(pathInfo)) {
            createUser(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/update/")) {
            updateUser(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/delete/")) {
            deleteUser(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/reset-password/")) {
            resetPassword(request, response);
        } else if (pathInfo != null && pathInfo.startsWith("/toggle-status/")) {
            toggleUserStatus(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    // -- GET Handlers --

    private void listUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            UserService userService = serviceFactory.getUserService();
            List<User> users = userService.getAllUsers();

            request.setAttribute("users", users);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/users/list.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error listing users", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/error/error.jsp");
        }
    }

    private void showNewUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("roles", UserRole.values());
        request.setAttribute("errorMessage", getErrorMessage(request));
        forwardToView(request, response, "/WEB-INF/views/users/new.jsp");
    }

    private void viewUser(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/view/");

            UserService userService = serviceFactory.getUserService();
            User user = userService.findUserById(userId);

            request.setAttribute("viewUser", user);
            request.setAttribute("currentUserId", getCurrentUser(request).getUserId());
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/users/view.jsp");

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "Invalid user ID format", e);
            setErrorMessage(request, "Invalid user ID");
            response.sendRedirect(request.getContextPath() + "/users");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "User not found", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void showEditUserForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/edit/");

            UserService userService = serviceFactory.getUserService();
            User user = userService.findUserById(userId);

            request.setAttribute("editUser", user);
            request.setAttribute("roles", UserRole.values());
            request.setAttribute("currentUserId", getCurrentUser(request).getUserId());
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/users/edit.jsp");

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Error loading user for edit", e);
            setErrorMessage(request, "Failed to load user information");
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void showResetPasswordForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/reset-password/");

            UserService userService = serviceFactory.getUserService();
            User user = userService.findUserById(userId);

            request.setAttribute("resetUser", user);
            request.setAttribute("errorMessage", getErrorMessage(request));

            forwardToView(request, response, "/WEB-INF/views/users/reset-password.jsp");

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Error loading user for password reset", e);
            setErrorMessage(request, "Failed to load user information");
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void searchUsers(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchTerm = request.getParameter("q");

        try {
            UserService userService = serviceFactory.getUserService();
            List<User> users = userService.searchUsers(searchTerm);

            request.setAttribute("users", users);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("searchResults", true);

            forwardToView(request, response, "/WEB-INF/views/users/list.jsp");

        } catch (BusinessException e) {
            LOGGER.log(Level.WARNING, "User search failed", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/users/list.jsp");
        }
    }

    // -- POST Handlers --

    private void createUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            String username = request.getParameter("username");
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String roleStr = request.getParameter("role");
            String activeStr = request.getParameter("active");

            // Validate password confirmation
            if (password == null || !password.equals(confirmPassword)) {
                throw new BusinessException("Passwords do not match");
            }

            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email != null && !email.trim().isEmpty() ? email : null);
            user.setRole(parseRole(roleStr));
            user.setActive(activeStr != null);

            UserService userService = serviceFactory.getUserService();
            User createdUser = userService.createUser(user, password);

            setSuccessMessage(request,
                    "User '" + createdUser.getUsername() + "' created successfully!");

            response.sendRedirect(request.getContextPath() + "/users/view/" + createdUser.getUserId());

        } catch (IllegalArgumentException | BusinessException e) {
            LOGGER.log(Level.WARNING, "User creation failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users/new");
        }
    }

    private void updateUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/update/");

            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String roleStr = request.getParameter("role");
            String activeStr = request.getParameter("active");

            User user = new User();
            user.setUserId(userId);
            user.setFullName(fullName);
            user.setEmail(email != null && !email.trim().isEmpty() ? email : null);
            user.setRole(parseRole(roleStr));
            user.setActive(activeStr != null);

            User currentUser = getCurrentUser(request);
            UserService userService = serviceFactory.getUserService();
            boolean updated = userService.updateUser(user, currentUser.getUserId());

            if (updated) {
                setSuccessMessage(request, "User updated successfully!");
            } else {
                setErrorMessage(request, "Failed to update user");
            }

            response.sendRedirect(request.getContextPath() + "/users/view/" + userId);

        } catch (IllegalArgumentException | BusinessException e) {
            LOGGER.log(Level.WARNING, "User update failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void deleteUser(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/delete/");

            User currentUser = getCurrentUser(request);
            UserService userService = serviceFactory.getUserService();
            boolean deleted = userService.deleteUser(userId, currentUser.getUserId());

            if (deleted) {
                setSuccessMessage(request, "User deleted successfully!");
            } else {
                setErrorMessage(request, "Failed to delete user");
            }

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "User deletion failed", e);
            setErrorMessage(request, e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/users");
    }

    private void resetPassword(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/reset-password/");

            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");

            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                throw new BusinessException("Passwords do not match");
            }

            UserService userService = serviceFactory.getUserService();
            userService.resetPassword(userId, newPassword);

            setSuccessMessage(request, "Password reset successfully!");
            response.sendRedirect(request.getContextPath() + "/users/view/" + userId);

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Password reset failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    private void toggleUserStatus(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            Integer userId = extractIdFromPath(request.getPathInfo(), "/toggle-status/");

            User currentUser = getCurrentUser(request);
            UserService userService = serviceFactory.getUserService();
            boolean newStatus = userService.toggleUserStatus(userId, currentUser.getUserId());

            String statusText = newStatus ? "activated" : "deactivated";
            setSuccessMessage(request, "User " + statusText + " successfully!");
            response.sendRedirect(request.getContextPath() + "/users/view/" + userId);

        } catch (NumberFormatException | BusinessException e) {
            LOGGER.log(Level.WARNING, "Status toggle failed", e);
            setErrorMessage(request, e.getMessage());
            response.sendRedirect(request.getContextPath() + "/users");
        }
    }

    // -- Helper Methods --

    private boolean requireAdminRole(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        User currentUser = getCurrentUser(request);

        if (currentUser == null || currentUser.getRole() != UserRole.ADMIN) {
            setErrorMessage(request, "Access denied. Administrator privileges required.");
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return false;
        }
        return true;
    }

    private Integer extractIdFromPath(String pathInfo, String prefix) {
        String idStr = pathInfo.substring(prefix.length());
        return Integer.parseInt(idStr);
    }

    private UserRole parseRole(String roleStr) throws BusinessException {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            throw new BusinessException("User role is required");
        }

        try {
            return UserRole.valueOf(roleStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid user role: " + roleStr);
        }
    }
}
