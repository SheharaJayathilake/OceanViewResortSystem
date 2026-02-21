package com.oceanview.controller;

import com.oceanview.service.ReportService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

/**
 * Dashboard Controller - Main landing page after login
 */
@WebServlet(name = "DashboardController", urlPatterns = {"/dashboard"})
public class DashboardController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!requireAuthentication(request, response)) {
            return;
        }
        
        try {
            ReportService reportService = serviceFactory.getReportService();
            Map<String, Object> statistics = reportService.getDashboardStatistics();
            
            request.setAttribute("statistics", statistics);
            request.setAttribute("successMessage", getSuccessMessage(request));
            request.setAttribute("errorMessage", getErrorMessage(request));
            
            forwardToView(request, response, "/WEB-INF/views/dashboard.jsp");
            
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error loading dashboard", e);
            request.setAttribute("errorMessage", "Failed to load dashboard: " + e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/dashboard.jsp");
        }
    }
}
