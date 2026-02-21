package com.oceanview.filter;

import com.oceanview.model.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Authentication Filter to protect secured resources
 * Implements Chain of Responsibility pattern
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/*"})
public class AuthenticationFilter implements Filter {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    // URLs that don't require authentication
    private static final List<String> PUBLIC_URLS = Arrays.asList(
        "/login",
        "/css/",
        "/js/",
        "/images/",
        "/help"
    );
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.info("AuthenticationFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Check if URL is public
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Check authentication
        HttpSession session = httpRequest.getSession(false);
        User currentUser = null;
        
        if (session != null) {
            currentUser = (User) session.getAttribute("currentUser");
        }
        
        if (currentUser == null) {
            LOGGER.warning("Unauthorized access attempt to: " + path);
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        
        // User is authenticated, proceed
        LOGGER.fine("Authenticated user accessing: " + path);
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.info("AuthenticationFilter destroyed");
    }
    
    /**
     * Check if resource is public (doesn't require authentication)
     */
    private boolean isPublicResource(String path) {
        return PUBLIC_URLS.stream().anyMatch(path::startsWith);
    }
}
