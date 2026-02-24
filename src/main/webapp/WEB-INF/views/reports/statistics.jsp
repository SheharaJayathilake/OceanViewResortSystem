<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="java.util.Map" %>
        <%@ page import="java.util.Date" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>System Statistics - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                <style>
                    @media print {
                        .no-print {
                            display: none !important;
                        }

                        .card {
                            border: none !important;
                            shadow: none !important;
                        }
                    }
                </style>
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <div class="page-header no-print">
                                <h1 class="page-title">SYSTEM STATISTICS REPORT</h1>
                                <div class="page-actions">
                                    <a href="${pageContext.request.contextPath}/reports" class="btn btn-secondary">
                                        ← Back to Reports
                                    </a>
                                    <button onclick="window.print()" class="btn btn-primary">
                                        Print Report
                                    </button>
                                </div>
                            </div>

                            <div class="card">
                                <div class="card-header text-center">
                                    <h2>OCEAN VIEW RESORT</h2>
                                    <p>System Statistics Report</p>
                                    <p>Generated: <%= new java.util.Date() %>
                                    </p>
                                </div>

                                <div class="card-body">
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
                                                        <div class="stat-label">Completed Stays</div>
                                                    </div>
                                                </div>

                                                <div class="mt-4">
                                                    <h3>Analysis</h3>
                                                    <p>
                                                        The system is currently managing <strong>
                                                            <%= stats.get("totalGuests") %>
                                                        </strong> registered guests
                                                        and <strong>
                                                            <%= stats.get("totalReservations") %>
                                                        </strong> total reservations.
                                                        There are <strong>
                                                            <%= stats.get("activeReservations") %>
                                                        </strong> reservations currently active (Confirmed or Checked
                                                        In).
                                                    </p>
                                                </div>
                                                <% } else { %>
                                                    <p class="text-center">No statistics available.</p>
                                                    <% } %>
                                </div>

                                <div class="card-footer text-center">
                                    <p>Ocean View Resort Management System</p>
                                </div>
                            </div>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
            </body>

            </html>