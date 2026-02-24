<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Reports - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>

    <body>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

            <main class="main-content">
                <div class="container">
                    <div class="page-header">
                        <h1 class="page-title">REPORTS & ANALYTICS</h1>
                    </div>

                    <div class="dashboard-grid">
                        <div class="card">
                            <div class="card-header">System Statistics</div>
                            <div class="card-body">
                                <p>View comprehensive system statistics including total reservations, guests, revenue,
                                    and occupancy rates.</p>
                            </div>
                            <div class="card-footer">
                                <a href="${pageContext.request.contextPath}/reports/statistics" class="btn btn-primary">
                                    View Statistics
                                </a>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">Reservations by Status</div>
                            <div class="card-body">
                                <p>Analyze reservations grouped by their current status (Pending, Confirmed, Checked-in,
                                    etc.).</p>
                            </div>
                            <div class="card-footer">
                                <a href="${pageContext.request.contextPath}/reports/status" class="btn btn-primary">
                                    View Status Report
                                </a>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">Guest Analytics</div>
                            <div class="card-body">
                                <p>Comprehensive guest information including total guests registered and guest history.
                                </p>
                            </div>
                            <div class="card-footer">
                                <a href="${pageContext.request.contextPath}/guests" class="btn btn-primary">
                                    View Guests
                                </a>
                            </div>
                        </div>

                        <div class="card">
                            <div class="card-header">Export Data</div>
                            <div class="card-body">
                                <p>Export reservation and guest data for external analysis and record keeping.</p>
                            </div>
                            <div class="card-footer">
                                <button class="btn btn-secondary"
                                    onclick="alert('Export functionality - Feature coming soon!')">
                                    Export Data
                                </button>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">Quick Actions</div>
                        <div class="card-body">
                            <div class="d-flex gap-1" style="flex-wrap: wrap;">
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">
                                    ← Back to Dashboard
                                </a>
                                <a href="${pageContext.request.contextPath}/reservations" class="btn btn-secondary">
                                    View All Reservations
                                </a>
                                <button onclick="window.print()" class="btn btn-secondary">
                                    Print Current Page
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </main>

            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
    </body>

    </html>