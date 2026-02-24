<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.User" %>
        <%@ page import="java.util.Map" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Dashboard - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <div class="page-header">
                                <h1 class="page-title">DASHBOARD</h1>
                            </div>

                            <% @SuppressWarnings("unchecked") Map<String, Object> stats = (Map<String, Object>)
                                    request.getAttribute("statistics");
                                    %>
                                    <% if (stats !=null) { %>
                                        <div class="dashboard-grid">
                                            <div class="stat-card">
                                                <div class="stat-value">
                                                    <%= stats.get("totalReservations") %>
                                                </div>
                                                <div class="stat-label">Total Reservations</div>
                                            </div>

                                            <div class="stat-card">
                                                <div class="stat-value">
                                                    <%= stats.get("activeReservations") %>
                                                </div>
                                                <div class="stat-label">Active Reservations</div>
                                            </div>

                                            <div class="stat-card">
                                                <div class="stat-value">
                                                    <%= stats.get("totalGuests") %>
                                                </div>
                                                <div class="stat-label">Total Guests</div>
                                            </div>

                                            <div class="stat-card">
                                                <div class="stat-value">
                                                    <%= stats.get("completedReservations") %>
                                                </div>
                                                <div class="stat-label">Completed</div>
                                            </div>
                                        </div>
                                        <% } %>

                                            <div class="card">
                                                <div class="card-header">Quick Actions</div>
                                                <div class="card-body">
                                                    <div class="d-flex gap-1" style="flex-wrap: wrap;">
                                                        <a href="${pageContext.request.contextPath}/reservations/new"
                                                            class="btn btn-primary">
                                                            New Reservation
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/reservations"
                                                            class="btn btn-secondary">
                                                            View All Reservations
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/guests"
                                                            class="btn btn-secondary">
                                                            Manage Guests
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/reports"
                                                            class="btn btn-secondary">
                                                            Generate Reports
                                                        </a>
                                                    </div>
                                                </div>
                                            </div>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
            </body>

            </html>