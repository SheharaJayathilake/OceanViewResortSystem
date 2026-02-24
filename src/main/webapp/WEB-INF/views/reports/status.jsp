<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Reservation" %>
        <%@ page import="java.util.List" %>
            <%@ page import="java.util.Map" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Status Report - Ocean View Resort</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                    <style>
                        @media print {
                            .no-print {
                                display: none !important;
                            }
                        }
                    </style>
                </head>

                <body>
                    <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                        <main class="main-content">
                            <div class="container">
                                <div class="page-header no-print">
                                    <h1 class="page-title">RESERVATIONS BY STATUS REPORT</h1>
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
                                        <p>Reservations by Status Report</p>
                                        <p>Generated: <%= new java.util.Date() %>
                                        </p>
                                    </div>

                                    <div class="card-body">
                                        <% @SuppressWarnings("unchecked") Map<Reservation.ReservationStatus,
                                            List<Reservation>> reportData =
                                            (Map<Reservation.ReservationStatus, List<Reservation>>)
                                                request.getAttribute("reportData");
                                                %>

                                                <% if (reportData !=null && !reportData.isEmpty()) { %>
                                                    <% for (Map.Entry<Reservation.ReservationStatus, List<Reservation>>
                                                        entry : reportData.entrySet()) { %>
                                                        <% Reservation.ReservationStatus status=entry.getKey(); %>
                                                            <% List<Reservation> reservations = entry.getValue(); %>

                                                                <div style="margin-bottom: 2rem;">
                                                                    <h3 style="display: inline-block;">
                                                                        <%= status %>
                                                                    </h3>
                                                                    <span
                                                                        class="badge badge-<%= status.name().toLowerCase() %>"
                                                                        style="margin-left: 1rem;">
                                                                        <%= reservations.size() %> Reservation<%=
                                                                                reservations.size() !=1 ? "s" : "" %>
                                                                    </span>

                                                                    <% if (!reservations.isEmpty()) { %>
                                                                        <table class="table mt-1">
                                                                            <thead>
                                                                                <tr>
                                                                                    <th>Reservation #</th>
                                                                                    <th>Guest Name</th>
                                                                                    <th>Room Type</th>
                                                                                    <th>Check-in</th>
                                                                                    <th>Check-out</th>
                                                                                    <th>Amount</th>
                                                                                </tr>
                                                                            </thead>
                                                                            <tbody>
                                                                                <% for (Reservation reservation :
                                                                                    reservations) { %>
                                                                                    <tr>
                                                                                        <td>
                                                                                            <%= reservation.getReservationNumber()
                                                                                                %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= reservation.getGuest()
                                                                                                !=null ?
                                                                                                reservation.getGuest().getGuestName()
                                                                                                : "N/A" %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= reservation.getRoomType()
                                                                                                !=null ?
                                                                                                reservation.getRoomType().getTypeName()
                                                                                                : "N/A" %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= reservation.getCheckInDate()
                                                                                                %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= reservation.getCheckOutDate()
                                                                                                %>
                                                                                        </td>
                                                                                        <td>LKR <%=
                                                                                                String.format("%.2f",
                                                                                                reservation.getTotalAmount())
                                                                                                %>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <% } %>
                                                                            </tbody>
                                                                        </table>
                                                                        <% } %>
                                                                </div>
                                                                <% } %>

                                                                    <hr
                                                                        style="margin: 2rem 0; border: 2px solid var(--color-black);">

                                                                    <h3>Summary</h3>
                                                                    <div class="dashboard-grid"
                                                                        style="grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));">
                                                                        <% int totalReservations=0; %>
                                                                            <% for (List<Reservation> list :
                                                                                reportData.values()) { %>
                                                                                <% totalReservations +=list.size(); %>
                                                                                    <% } %>

                                                                                        <div class="stat-card">
                                                                                            <div class="stat-value">
                                                                                                <%= totalReservations %>
                                                                                            </div>
                                                                                            <div class="stat-label">
                                                                                                Total Reservations</div>
                                                                                        </div>

                                                                                        <% for
                                                                                            (Map.Entry<Reservation.ReservationStatus,
                                                                                            List<Reservation>> entry :
                                                                                            reportData.entrySet()) { %>
                                                                                            <div class="stat-card">
                                                                                                <div class="stat-value">
                                                                                                    <%= entry.getValue().size()
                                                                                                        %>
                                                                                                </div>
                                                                                                <div class="stat-label">
                                                                                                    <%= entry.getKey()
                                                                                                        %>
                                                                                                </div>
                                                                                            </div>
                                                                                            <% } %>
                                                                    </div>
                                                                    <% } else { %>
                                                                        <p class="text-center">No reservation data
                                                                            available.</p>
                                                                        <% } %>
                                    </div>
                                </div>
                            </div>
                        </main>

                        <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
                </body>

                </html>