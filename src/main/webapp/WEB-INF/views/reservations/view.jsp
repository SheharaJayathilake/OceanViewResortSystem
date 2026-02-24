<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Reservation" %>
        <%@ page import="com.oceanview.model.Guest" %>
            <%@ page import="com.oceanview.model.RoomType" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Reservation Details - Ocean View Resort</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                </head>

                <body>
                    <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                        <main class="main-content">
                            <div class="container">
                                <% Reservation reservation=(Reservation) request.getAttribute("reservation"); %>
                                    <% if (reservation !=null) { %>
                                        <% Guest guest=reservation.getGuest(); %>
                                            <% RoomType roomType=reservation.getRoomType(); %>

                                                <div class="page-header">
                                                    <h1 class="page-title">RESERVATION DETAILS</h1>
                                                    <div class="page-actions">
                                                        <a href="${pageContext.request.contextPath}/reservations"
                                                            class="btn btn-secondary">
                                                            ← Back to List
                                                        </a>
                                                        <a href="${pageContext.request.contextPath}/reservations/bill/<%= reservation.getReservationNumber() %>"
                                                            class="btn btn-primary">
                                                            View Bill
                                                        </a>
                                                    </div>
                                                </div>

                                                <% String successMessage=(String)
                                                    request.getAttribute("successMessage"); %>
                                                    <% if (successMessage !=null) { %>
                                                        <div class="alert alert-success">
                                                            <%= successMessage %>
                                                        </div>
                                                        <% } %>

                                                            <% String errorMessage=(String)
                                                                request.getAttribute("errorMessage"); %>
                                                                <% if (errorMessage !=null) { %>
                                                                    <div class="alert alert-error">
                                                                        <%= errorMessage %>
                                                                    </div>
                                                                    <% } %>

                                                                        <div class="card">
                                                                            <div
                                                                                class="card-header d-flex justify-between align-center">
                                                                                <div>
                                                                                    <h2>Reservation: <%=
                                                                                            reservation.getReservationNumber()
                                                                                            %>
                                                                                    </h2>
                                                                                </div>
                                                                                <div>
                                                                                    <span
                                                                                        class="badge badge-<%= reservation.getStatus().name().toLowerCase() %>">
                                                                                        <%= reservation.getStatus() %>
                                                                                    </span>
                                                                                </div>
                                                                            </div>

                                                                            <div class="card-body">
                                                                                <div class="form-row">
                                                                                    <div>
                                                                                        <h3>Guest Information</h3>
                                                                                        <% if (guest !=null) { %>
                                                                                            <p><strong>Name:</strong>
                                                                                                <%= guest.getGuestName()
                                                                                                    %>
                                                                                            </p>
                                                                                            <p><strong>Contact:</strong>
                                                                                                <%= guest.getContactNumber()
                                                                                                    %>
                                                                                            </p>
                                                                                            <% if (guest.getEmail()
                                                                                                !=null &&
                                                                                                !guest.getEmail().isEmpty())
                                                                                                { %>
                                                                                                <p><strong>Email:</strong>
                                                                                                    <%= guest.getEmail()
                                                                                                        %>
                                                                                                </p>
                                                                                                <% } %>
                                                                                                    <p><strong>Address:</strong>
                                                                                                        <%= guest.getAddress()
                                                                                                            %>
                                                                                                    </p>
                                                                                                    <% } %>
                                                                                    </div>

                                                                                    <div>
                                                                                        <h3>Room Information</h3>
                                                                                        <% if (roomType !=null) { %>
                                                                                            <p><strong>Type:</strong>
                                                                                                <%= roomType.getTypeName()
                                                                                                    %>
                                                                                            </p>
                                                                                            <p><strong>Rate:</strong>
                                                                                                LKR <%=
                                                                                                    String.format("%.2f",
                                                                                                    roomType.getRatePerNight())
                                                                                                    %>/night</p>
                                                                                            <p><strong>Capacity:</strong>
                                                                                                <%= roomType.getCapacity()
                                                                                                    %> guests
                                                                                            </p>
                                                                                            <% } %>
                                                                                    </div>
                                                                                </div>

                                                                                <hr
                                                                                    style="margin: 1.5rem 0; border: 1px solid var(--color-gray-300);">

                                                                                <div class="form-row">
                                                                                    <div>
                                                                                        <h3>Booking Details</h3>
                                                                                        <p><strong>Check-in
                                                                                                Date:</strong>
                                                                                            <%= reservation.getCheckInDate()
                                                                                                %>
                                                                                        </p>
                                                                                        <p><strong>Check-out
                                                                                                Date:</strong>
                                                                                            <%= reservation.getCheckOutDate()
                                                                                                %>
                                                                                        </p>
                                                                                        <p><strong>Number of
                                                                                                Nights:</strong>
                                                                                            <%= reservation.getNumberOfNights()
                                                                                                %>
                                                                                        </p>
                                                                                        <p><strong>Number of
                                                                                                Guests:</strong>
                                                                                            <%= reservation.getNumberOfGuests()
                                                                                                %>
                                                                                        </p>
                                                                                    </div>

                                                                                    <div>
                                                                                        <h3>Payment Information</h3>
                                                                                        <p><strong>Total
                                                                                                Amount:</strong> LKR <%=
                                                                                                String.format("%.2f",
                                                                                                reservation.getTotalAmount())
                                                                                                %>
                                                                                        </p>
                                                                                        <p><strong>Payment
                                                                                                Status:</strong>
                                                                                            <span
                                                                                                class="badge badge-<%= reservation.getPaymentStatus().name().toLowerCase() %>">
                                                                                                <%= reservation.getPaymentStatus()
                                                                                                    %>
                                                                                            </span>
                                                                                        </p>
                                                                                    </div>
                                                                                </div>

                                                                                <% if (reservation.getSpecialRequests()
                                                                                    !=null &&
                                                                                    !reservation.getSpecialRequests().isEmpty())
                                                                                    { %>
                                                                                    <hr
                                                                                        style="margin: 1.5rem 0; border: 1px solid var(--color-gray-300);">
                                                                                    <h3>Special Requests</h3>
                                                                                    <p>
                                                                                        <%= reservation.getSpecialRequests()
                                                                                            %>
                                                                                    </p>
                                                                                    <% } %>
                                                                            </div>

                                                                            <div class="card-footer">
                                                                                <div class="d-flex justify-between">
                                                                                    <div class="d-flex gap-1">
                                                                                        <% if
                                                                                            (reservation.getStatus()==Reservation.ReservationStatus.PENDING)
                                                                                            { %>
                                                                                            <form
                                                                                                action="${pageContext.request.contextPath}/reservations/update-status/<%= reservation.getReservationNumber() %>"
                                                                                                method="post"
                                                                                                style="display: inline;">
                                                                                                <input type="hidden"
                                                                                                    name="action"
                                                                                                    value="confirm">
                                                                                                <button type="submit"
                                                                                                    class="btn btn-primary">Confirm
                                                                                                    Reservation</button>
                                                                                            </form>
                                                                                            <% } %>

                                                                                                <% if
                                                                                                    (reservation.getStatus()==Reservation.ReservationStatus.CONFIRMED)
                                                                                                    { %>
                                                                                                    <form
                                                                                                        action="${pageContext.request.contextPath}/reservations/update-status/<%= reservation.getReservationNumber() %>"
                                                                                                        method="post"
                                                                                                        style="display: inline;">
                                                                                                        <input
                                                                                                            type="hidden"
                                                                                                            name="action"
                                                                                                            value="checkin">
                                                                                                        <button
                                                                                                            type="submit"
                                                                                                            class="btn btn-primary">Check-in
                                                                                                            Guest</button>
                                                                                                    </form>
                                                                                                    <% } %>

                                                                                                        <% if
                                                                                                            (reservation.getStatus()==Reservation.ReservationStatus.CHECKED_IN)
                                                                                                            { %>

                                                                                                            <form
                                                                                                                action="${pageContext.request.contextPath}/reservations/update-status/<%= reservation.getReservationNumber() %>"
                                                                                                                method="post"
                                                                                                                style="display: inline;">
                                                                                                                <input
                                                                                                                    type="hidden"
                                                                                                                    name="action"
                                                                                                                    value="checkout">
                                                                                                                <button
                                                                                                                    type="submit"
                                                                                                                    class="btn btn-primary">Check-out
                                                                                                                    Guest</button>
                                                                                                            </form>

                                                                                                            <% if
                                                                                                                (reservation.getPaymentStatus()
                                                                                                                !=Reservation.PaymentStatus.PAID)
                                                                                                                { %>
                                                                                                                <form
                                                                                                                    action="${pageContext.request.contextPath}/reservations/update-status/<%= reservation.getReservationNumber() %>"
                                                                                                                    method="post"
                                                                                                                    style="display: inline;">
                                                                                                                    <input
                                                                                                                        type="hidden"
                                                                                                                        name="action"
                                                                                                                        value="pay">
                                                                                                                    <button
                                                                                                                        type="submit"
                                                                                                                        class="btn btn-primary"
                                                                                                                        style="background-color: #28a745; margin-left:10px;">Process
                                                                                                                        Payment</button>
                                                                                                                </form>
                                                                                                                <% } %>

                                                                                                                    <% }
                                                                                                                        %>

                                                                                                                        <% if
                                                                                                                            (reservation.isModifiable())
                                                                                                                            {
                                                                                                                            %>
                                                                                                                            <form
                                                                                                                                action="${pageContext.request.contextPath}/reservations/update-status/<%= reservation.getReservationNumber() %>"
                                                                                                                                method="post"
                                                                                                                                style="display: inline;"
                                                                                                                                onsubmit="return confirm('Are you sure you want to cancel this reservation?');">
                                                                                                                                <input
                                                                                                                                    type="hidden"
                                                                                                                                    name="action"
                                                                                                                                    value="cancel">
                                                                                                                                <button
                                                                                                                                    type="submit"
                                                                                                                                    class="btn btn-secondary">Cancel
                                                                                                                                    Reservation</button>
                                                                                                                            </form>
                                                                                                                            <% }
                                                                                                                                %>
                                                                                    </div>
                                                                                </div>
                                                                            </div>
                                                                        </div>
                                                                        <% } else { %>
                                                                            <div class="alert alert-error">
                                                                                Reservation not found.
                                                                            </div>
                                                                            <% } %>
                            </div>
                        </main>

                        <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
                </body>

                </html>