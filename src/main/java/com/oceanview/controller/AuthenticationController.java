package com.oceanview.controller;

import com.oceanview.model.User;
import com.oceanview.service.AuthenticationService;
import com.oceanview.exception.AuthenticationException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Authentication Controller handling login/logout
 */
@WebServlet(name = "AuthController", urlPatterns = {"/login", "/logout"})
public class AuthenticationController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();
        
        if ("/logout".equals(path)) {
            handleLogout(request, response);
        } else {
            // Show login page
            if (isAuthenticated(request)) {
                response.sendRedirect(request.getContextPath() + "/dashboard");
                return;
            }
            forwardToView(request, response, "/WEB-INF/views/login.jsp");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        handleLogin(request, response);
    }
    
    /**
     * Handle user login
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            AuthenticationService authService = serviceFactory.getAuthenticationService();
            User user = authService.authenticate(username, password);
            
            // Create session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes
            
            LOGGER.info("User logged in: " + username);
            
            // Redirect to dashboard
            response.sendRedirect(request.getContextPath() + "/dashboard");
            
        } catch (AuthenticationException e) {
            LOGGER.log(Level.WARNING, "Login failed for user: " + username, e);
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("username", username);
            forwardToView(request, response, "/WEB-INF/views/login.jsp");
        }
    }
    
    /**
     * Handle user logout
     */
    private void handleLogout(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                LOGGER.info("User logged out: " + user.getUsername());
            }
            session.invalidate();
        }
        
        response.sendRedirect(request.getContextPath() + "/login");
    }
}
