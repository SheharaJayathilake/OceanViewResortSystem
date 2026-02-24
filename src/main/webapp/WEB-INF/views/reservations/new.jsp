<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Guest" %>
        <%@ page import="com.oceanview.model.RoomType" %>
            <%@ page import="java.util.List" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>New Reservation - Ocean View Resort</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                </head>

                <body>
                    <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                        <main class="main-content">
                            <div class="container">
                                <div class="page-header">
                                    <h1 class="page-title">NEW RESERVATION</h1>
                                </div>

                                <form action="${pageContext.request.contextPath}/reservations/create" method="post">
                                    <div class="card">
                                        <div class="card-header">Guest Information</div>
                                        <div class="card-body">
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="guestId" class="form-label">Select Guest</label>
                                                    <select id="guestId" name="guestId" class="form-control" required>
                                                        <option value="">-- Select Guest --</option>
                                                        <% @SuppressWarnings("unchecked") List<Guest> guests = (List
                                                            <Guest>) request.getAttribute("guests"); %>
                                                                <% if (guests !=null) { %>
                                                                    <% for (Guest guest : guests) { %>
                                                                        <option value="<%= guest.getGuestId() %>">
                                                                            <%= guest.getGuestName() %> - <%=
                                                                                    guest.getContactNumber() %>
                                                                        </option>
                                                                        <% } %>
                                                                            <% } %>
                                                    </select>
                                                </div>

                                                <div class="form-group">
                                                    <label class="form-label">&nbsp;</label>
                                                    <a href="${pageContext.request.contextPath}/guests/new"
                                                        class="btn btn-secondary"
                                                        style="display: inline-block; margin-top: 0;">
                                                        Register New Guest
                                                    </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="card">
                                        <div class="card-header">Reservation Details</div>
                                        <div class="card-body">
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="roomTypeId" class="form-label">Room Type</label>
                                                    <select id="roomTypeId" name="roomTypeId" class="form-control"
                                                        required>
                                                        <option value="">-- Select Room Type --</option>
                                                        <% @SuppressWarnings("unchecked") List<RoomType> roomTypes =
                                                            (List<RoomType>)
                                                                request.getAttribute("roomTypes"); %>
                                                                <% if (roomTypes !=null) { %>
                                                                    <% for (RoomType rt : roomTypes) { %>
                                                                        <option value="<%= rt.getRoomTypeId() %>">
                                                                            <%= rt.getTypeName() %> - LKR <%=
                                                                                    String.format("%.2f",
                                                                                    rt.getRatePerNight()) %>/night
                                                                        </option>
                                                                        <% } %>
                                                                            <% } %>
                                                    </select>
                                                </div>

                                                <div class="form-group">
                                                    <label for="numberOfGuests" class="form-label">Number of
                                                        Guests</label>
                                                    <input type="number" id="numberOfGuests" name="numberOfGuests"
                                                        class="form-control" min="1" value="1" required>
                                                </div>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="checkInDate" class="form-label">Check-in Date</label>
                                                    <input type="date" id="checkInDate" name="checkInDate"
                                                        class="form-control" required>
                                                </div>

                                                <div class="form-group">
                                                    <label for="checkOutDate" class="form-label">Check-out Date</label>
                                                    <input type="date" id="checkOutDate" name="checkOutDate"
                                                        class="form-control" required>
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="specialRequests" class="form-label">Special Requests
                                                    (Optional)</label>
                                                <textarea id="specialRequests" name="specialRequests"
                                                    class="form-control"
                                                    placeholder="Any special requirements or requests..."></textarea>
                                            </div>
                                        </div>

                                        <div class="card-footer">
                                            <div class="d-flex justify-between">
                                                <a href="${pageContext.request.contextPath}/reservations"
                                                    class="btn btn-secondary">
                                                    Cancel
                                                </a>
                                                <button type="submit" class="btn btn-primary">
                                                    Create Reservation
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </main>

                        <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                            <script>
                                // Set minimum date to today
                                document.getElementById('checkInDate').min = new Date().toISOString().split('T')[0];

                                // Update checkout date minimum when checkin changes
                                document.getElementById('checkInDate').addEventListener('change', function () {
                                    const checkInDate = new Date(this.value);
                                    checkInDate.setDate(checkInDate.getDate() + 1);
                                    document.getElementById('checkOutDate').min = checkInDate.toISOString().split('T')[0];
                                });
                            </script>
                </body>

                </html>