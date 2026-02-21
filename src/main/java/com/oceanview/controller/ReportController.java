package com.oceanview.controller;

import com.oceanview.model.Reservation;
import com.oceanview.service.ReportService;
import com.oceanview.exception.BusinessException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Report Controller - Generate various business reports
 */
@WebServlet(name = "ReportController", urlPatterns = {"/reports", "/reports/*"})
public class ReportController extends BaseController {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (!requireAuthentication(request, response)) {
            return;
        }
        
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || "/".equals(pathInfo)) {
            showReportMenu(request, response);
        } else if ("/status".equals(pathInfo)) {
            generateStatusReport(request, response);
        } else if ("/statistics".equals(pathInfo)) {
            generateStatisticsReport(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    /**
     * Show report menu
     */
    private void showReportMenu(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        forwardToView(request, response, "/WEB-INF/views/reports/menu.jsp");
    }
    
    /**
     * Generate reservations by status report
     */
    private void generateStatusReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            ReportService reportService = serviceFactory.getReportService();
            Map<Reservation.ReservationStatus, List<Reservation>> reportData = 
                reportService.getReservationsByStatus();
            
            request.setAttribute("reportData", reportData);
            request.setAttribute("reportTitle", "Reservations by Status");
            
            forwardToView(request, response, "/WEB-INF/views/reports/status.jsp");
            
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error generating status report", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/error.jsp");
        }
    }
    
    /**
     * Generate statistics report
     */
    private void generateStatisticsReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            ReportService reportService = serviceFactory.getReportService();
            Map<String, Object> statistics = reportService.getDashboardStatistics();
            
            request.setAttribute("statistics", statistics);
            request.setAttribute("reportTitle", "System Statistics");
            
            forwardToView(request, response, "/WEB-INF/views/reports/statistics.jsp");
            
        } catch (BusinessException e) {
            LOGGER.log(Level.SEVERE, "Error generating statistics report", e);
            request.setAttribute("errorMessage", e.getMessage());
            forwardToView(request, response, "/WEB-INF/views/error.jsp");
        }
    }
}
