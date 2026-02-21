package com.oceanview.controller;

import com.oceanview.model.User;
import com.oceanview.factory.ServiceFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Base Controller with common functionality
 * Implements Template Method pattern for request handling
 */
public abstract class BaseController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    protected static final Logger LOGGER = Logger.getLogger(BaseController.class.getName());
    protected final ServiceFactory serviceFactory;
    
    public BaseController() {
        this.serviceFactory = ServiceFactory.getInstance();
    }
    
    /**
     * Get current user from session
     */
    protected User getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (User) session.getAttribute("currentUser");
        }
        return null;
    }
    
    /**
     * Check if user is authenticated
     */
    protected boolean isAuthenticated(HttpServletRequest request) {
        return getCurrentUser(request) != null;
    }
    
    /**
     * Redirect to login if not authenticated
     */
    protected boolean requireAuthentication(HttpServletRequest request, 
                                           HttpServletResponse response) throws IOException {
        if (!isAuthenticated(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }
    
    /**
     * Set success message in session
     */
    protected void setSuccessMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("successMessage", message);
    }
    
    /**
     * Set error message in session
     */
    protected void setErrorMessage(HttpServletRequest request, String message) {
        request.getSession().setAttribute("errorMessage", message);
    }
    
    /**
     * Get and clear success message
     */
    protected String getSuccessMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute("successMessage");
            session.removeAttribute("successMessage");
            return message;
        }
        return null;
    }
    
    /**
     * Get and clear error message
     */
    protected String getErrorMessage(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute("errorMessage");
            session.removeAttribute("errorMessage");
            return message;
        }
        return null;
    }
    
    /**
     * Forward to view
     */
    protected void forwardToView(HttpServletRequest request, 
                                 HttpServletResponse response, 
                                 String viewPath) throws javax.servlet.ServletException, IOException {
        request.getRequestDispatcher(viewPath).forward(request, response);
    }
}
