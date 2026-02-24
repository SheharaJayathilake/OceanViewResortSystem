<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Reservation" %>
        <%@ page import="java.util.List" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Reservations - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <div class="page-header">
                                <h1 class="page-title">RESERVATIONS</h1>
                                <div class="page-actions">
                                    <a href="${pageContext.request.contextPath}/reservations/new"
                                        class="btn btn-primary">
                                        + New Reservation
                                    </a>
                                </div>
                            </div>

                            <% String successMessage=(String) request.getAttribute("successMessage"); %>
                                <% if (successMessage !=null) { %>
                                    <div class="alert alert-success">
                                        <%= successMessage %>
                                    </div>
                                    <% } %>

                                        <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                                            <% if (errorMessage !=null) { %>
                                                <div class="alert alert-error">
                                                    <%= errorMessage %>
                                                </div>
                                                <% } %>

                                                    <div class="card">
                                                        <div class="card-header">All Reservations</div>
                                                        <div class="card-body p-0">
                                                            <% @SuppressWarnings("unchecked") List<Reservation>
                                                                reservations = (List<Reservation>)
                                                                    request.getAttribute("reservations");
                                                                    %>
                                                                    <% if (reservations !=null &&
                                                                        !reservations.isEmpty()) { %>
                                                                        <table class="table">
                                                                            <thead>
                                                                                <tr>
                                                                                    <th>Reservation #</th>
                                                                                    <th>Guest Name</th>
                                                                                    <th>Room Type</th>
                                                                                    <th>Check-in</th>
                                                                                    <th>Check-out</th>
                                                                                    <th>Status</th>
                                                                                    <th>Amount</th>
                                                                                    <th>Actions</th>
                                                                                </tr>
                                                                            </thead>
                                                                            <tbody>
                                                                                <% for (Reservation reservation :
                                                                                    reservations) { %>
                                                                                    <tr>
                                                                                        <td><strong>
                                                                                                <%= reservation.getReservationNumber()
                                                                                                    %>
                                                                                            </strong></td>
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
                                                                                        <td>
                                                                                            <span
                                                                                                class="badge badge-<%= reservation.getStatus().name().toLowerCase() %>">
                                                                                                <%= reservation.getStatus()
                                                                                                    %>
                                                                                            </span>
                                                                                        </td>
                                                                                        <td>LKR <%=
                                                                                                String.format("%.2f",
                                                                                                reservation.getTotalAmount())
                                                                                                %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <a href="${pageContext.request.contextPath}/reservations/view/<%= reservation.getReservationNumber() %>"
                                                                                                class="btn btn-sm btn-secondary">
                                                                                                View
                                                                                            </a>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <% } %>
                                                                            </tbody>
                                                                        </table>
                                                                        <% } else { %>
                                                                            <div
                                                                                style="padding: 2rem; text-align: center;">
                                                                                <p>No reservations found.</p>
                                                                                <a href="${pageContext.request.contextPath}/reservations/new"
                                                                                    class="btn btn-primary mt-1">
                                                                                    Create First Reservation
                                                                                </a>
                                                                            </div>
                                                                            <% } %>
                                                        </div>
                                                    </div>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
            </body>

            </html>