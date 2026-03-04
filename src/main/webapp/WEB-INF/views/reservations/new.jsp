<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Guest" %>
        <%@ page import="com.oceanview.model.Room" %>
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

                                    <%-- Display error/success flash messages --%>
                                        <% String err=(String) session.getAttribute("errorMessage"); if (err !=null) {
                                            session.removeAttribute("errorMessage"); %>
                                            <div class="alert alert-error">
                                                <%= err %>
                                            </div>
                                            <% } %>
                                                <% String suc=(String) session.getAttribute("successMessage"); if (suc
                                                    !=null) { session.removeAttribute("successMessage"); %>
                                                    <div class="alert alert-success">
                                                        <%= suc %>
                                                    </div>
                                                    <% } %>

                                                        <form
                                                            action="${pageContext.request.contextPath}/reservations/create"
                                                            method="post">
                                                            <div class="card">
                                                                <div class="card-header">Guest Information</div>
                                                                <div class="card-body">
                                                                    <div class="form-row">
                                                                        <div class="form-group">
                                                                            <label for="guestId"
                                                                                class="form-label">Select Guest</label>
                                                                            <select id="guestId" name="guestId"
                                                                                class="form-control" required>
                                                                                <option value="">-- Select Guest --
                                                                                </option>
                                                                                <% @SuppressWarnings("unchecked")
                                                                                    List<Guest> guests = (List<Guest>)
                                                                                        request.getAttribute("guests");
                                                                                        if (guests != null) {
                                                                                        for (Guest guest : guests) { %>
                                                                                        <option
                                                                                            value="<%= guest.getGuestId() %>">
                                                                                            <%= guest.getGuestName() %>
                                                                                                - <%=
                                                                                                    guest.getContactNumber()
                                                                                                    %>
                                                                                        </option>
                                                                                        <% } } %>
                                                                            </select>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label class="form-label">&nbsp;</label>
                                                                            <a href="${pageContext.request.contextPath}/guests/new"
                                                                                class="btn btn-secondary"
                                                                                style="display:inline-block;margin-top:0;">Register
                                                                                New Guest</a>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </div>

                                                            <div class="card">
                                                                <div class="card-header">Room and Dates</div>
                                                                <div class="card-body">
                                                                    <div class="form-row">
                                                                        <div class="form-group">
                                                                            <label for="roomId"
                                                                                class="form-label">Select Room</label>
                                                                            <select id="roomId" name="roomId"
                                                                                class="form-control" required>
                                                                                <option value="">-- Select Room --
                                                                                </option>
                                                                                <% @SuppressWarnings("unchecked")
                                                                                    List<Room> rooms = (List<Room>)
                                                                                        request.getAttribute("rooms");
                                                                                        if (rooms != null) {
                                                                                        for (Room room : rooms) {
                                                                                        RoomType rt =
                                                                                        room.getRoomType();
                                                                                        String typeName = (rt != null) ?
                                                                                        rt.getTypeName() : "N/A";
                                                                                        String rate = (rt != null) ?
                                                                                        rt.getRatePerNight().toPlainString()
                                                                                        : "0";
                                                                                        String cap = (rt != null) ?
                                                                                        String.valueOf(rt.getCapacity())
                                                                                        : "0";
                                                                                        %>
                                                                                        <option
                                                                                            value="<%= room.getRoomId() %>"
                                                                                            data-type="<%= typeName %>"
                                                                                            data-rate="<%= rate %>"
                                                                                            data-capacity="<%= cap %>"
                                                                                            data-type-id="<%= room.getRoomTypeId() %>">
                                                                                            Room <%=
                                                                                                room.getRoomNumber() %>
                                                                                                &#8212; <%= typeName %>
                                                                                        </option>
                                                                                        <% } } %>
                                                                            </select>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="numberOfGuests"
                                                                                class="form-label">Number of
                                                                                Guests</label>
                                                                            <input type="number" id="numberOfGuests"
                                                                                name="numberOfGuests"
                                                                                class="form-control" min="1" value="1"
                                                                                required>
                                                                        </div>
                                                                    </div>

                                                                    <div id="roomInfoPanel"
                                                                        style="display:none;margin-bottom:1rem;padding:12px 16px;background:#f0f4f8;border-radius:6px;border-left:4px solid #2c3e50;">
                                                                        <div
                                                                            style="display:flex;gap:2rem;flex-wrap:wrap;">
                                                                            <div>
                                                                                <span
                                                                                    style="font-size:12px;color:#666;text-transform:uppercase;letter-spacing:0.05em;">Room
                                                                                    Type</span>
                                                                                <div id="infoType"
                                                                                    style="font-weight:600;font-size:15px;">
                                                                                    &#8212;</div>
                                                                            </div>
                                                                            <div>
                                                                                <span
                                                                                    style="font-size:12px;color:#666;text-transform:uppercase;letter-spacing:0.05em;">Rate
                                                                                    / Night</span>
                                                                                <div id="infoRate"
                                                                                    style="font-weight:600;font-size:15px;">
                                                                                    &#8212;</div>
                                                                            </div>
                                                                            <div>
                                                                                <span
                                                                                    style="font-size:12px;color:#666;text-transform:uppercase;letter-spacing:0.05em;">Capacity</span>
                                                                                <div id="infoCapacity"
                                                                                    style="font-weight:600;font-size:15px;">
                                                                                    &#8212;</div>
                                                                            </div>
                                                                        </div>
                                                                    </div>

                                                                    <div class="form-row">
                                                                        <div class="form-group">
                                                                            <label for="checkInDate"
                                                                                class="form-label">Check-in Date</label>
                                                                            <input type="date" id="checkInDate"
                                                                                name="checkInDate" class="form-control"
                                                                                required>
                                                                        </div>
                                                                        <div class="form-group">
                                                                            <label for="checkOutDate"
                                                                                class="form-label">Check-out
                                                                                Date</label>
                                                                            <input type="date" id="checkOutDate"
                                                                                name="checkOutDate" class="form-control"
                                                                                required>
                                                                        </div>
                                                                    </div>

                                                                    <div class="form-group">
                                                                        <label for="specialRequests"
                                                                            class="form-label">Special Requests
                                                                            (Optional)</label>
                                                                        <textarea id="specialRequests"
                                                                            name="specialRequests" class="form-control"
                                                                            placeholder="Any special requirements or requests..."></textarea>
                                                                    </div>
                                                                </div>
                                                                <div class="card-footer">
                                                                    <div class="d-flex justify-between">
                                                                        <a href="${pageContext.request.contextPath}/reservations"
                                                                            class="btn btn-secondary">Cancel</a>
                                                                        <button type="submit"
                                                                            class="btn btn-primary">Create
                                                                            Reservation</button>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </form>
                                </div>
                            </main>
                            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
                                <script>
                                    document.getElementById('checkInDate').min = new Date().toISOString().split('T')[0];
                                    document.getElementById('checkInDate').addEventListener('change', function () {
                                        var d = new Date(this.value);
                                        d.setDate(d.getDate() + 1);
                                        document.getElementById('checkOutDate').min = d.toISOString().split('T')[0];
                                    });
                                    document.getElementById('roomId').addEventListener('change', function () {
                                        var opt = this.options[this.selectedIndex];
                                        var panel = document.getElementById('roomInfoPanel');
                                        if (this.value === '') { panel.style.display = 'none'; return; }
                                        document.getElementById('infoType').textContent = opt.getAttribute('data-type');
                                        document.getElementById('infoRate').textContent = 'LKR ' + parseFloat(opt.getAttribute('data-rate')).toFixed(2) + ' / night';
                                        document.getElementById('infoCapacity').textContent = opt.getAttribute('data-capacity') + ' guests';
                                        panel.style.display = 'block';
                                        document.getElementById('numberOfGuests').max = parseInt(opt.getAttribute('data-capacity'));
                                    });
                                </script>
                    </body>

                    </html>