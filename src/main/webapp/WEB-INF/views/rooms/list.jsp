<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.RoomType" %>
        <%@ page import="com.oceanview.model.Room" %>
            <%@ page import="java.util.List" %>
                <%@ page import="java.util.Map" %>
                    <!DOCTYPE html>
                    <html lang="en">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Room Management - Ocean View Resort</title>
                        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                        <style>
                            .room-grid {
                                display: flex;
                                flex-wrap: wrap;
                                gap: 8px;
                                margin-top: 10px
                            }

                            .room-chip {
                                display: inline-flex;
                                align-items: center;
                                gap: 8px;
                                padding: 8px 14px;
                                background: #e8f0fe;
                                border: 1px solid #c6d9f1;
                                border-radius: 20px;
                                font-size: 13px;
                                color: #1a3a5c
                            }

                            .room-chip .room-num {
                                font-weight: 700;
                                font-size: 14px
                            }

                            .room-chip .floor-tag {
                                font-size: 11px;
                                color: #666
                            }

                            .room-chip .room-actions {
                                display: flex;
                                gap: 4px;
                                margin-left: 4px
                            }

                            .room-chip .room-actions a,
                            .room-chip .room-actions button {
                                background: none;
                                border: none;
                                cursor: pointer;
                                font-size: 12px;
                                padding: 2px 5px;
                                border-radius: 3px;
                                text-decoration: none;
                                color: #555;
                                line-height: 1
                            }

                            .room-chip .room-actions a:hover {
                                background: #d0dff2;
                                color: #1a3a5c
                            }

                            .room-chip .room-actions button:hover {
                                background: #f8d7da;
                                color: #c0392b
                            }

                            .room-chip.inactive {
                                background: #f5f5f5;
                                border-color: #ddd;
                                color: #999
                            }

                            .type-section {
                                padding: 20px 24px;
                                border-bottom: 1px solid #eee
                            }

                            .type-section:last-child {
                                border-bottom: none
                            }

                            .type-header {
                                display: flex;
                                justify-content: space-between;
                                align-items: center;
                                margin-bottom: 4px
                            }

                            .type-name {
                                font-size: 18px;
                                font-weight: 700;
                                color: #1a1a2e
                            }

                            .type-meta {
                                display: flex;
                                gap: 16px;
                                font-size: 13px;
                                color: #555;
                                margin-bottom: 6px
                            }

                            .type-actions {
                                display: flex;
                                gap: 6px;
                                align-items: center
                            }
                        </style>
                    </head>

                    <body>
                        <%@ include file="/WEB-INF/views/includes/header.jsp" %>
                            <main class="main-content">
                                <div class="container">
                                    <div class="page-header">
                                        <h1 class="page-title">ROOM MANAGEMENT</h1>
                                        <div class="page-actions">
                                            <a href="${pageContext.request.contextPath}/rooms/room/new"
                                                class="btn btn-primary">+ Add Room</a>
                                            <a href="${pageContext.request.contextPath}/rooms/new"
                                                class="btn btn-secondary">+ Add Room Type</a>
                                        </div>
                                    </div>
                                    <% String successMessage=(String) request.getAttribute("successMessage"); if
                                        (successMessage !=null) { %>
                                        <div class="alert alert-success">
                                            <%= successMessage %>
                                        </div>
                                        <% } String errorMessage=(String) request.getAttribute("errorMessage"); if
                                            (errorMessage !=null) { %>
                                            <div class="alert alert-error">
                                                <%= errorMessage %>
                                            </div>
                                            <% } @SuppressWarnings("unchecked") List<RoomType> roomTypes = (List
                                                <RoomType>) request.getAttribute("roomTypes");
                                                    @SuppressWarnings("unchecked")
                                                    Map<Integer, List<Room>> roomsByType = (Map<Integer, List<Room>>)
                                                            request.getAttribute("roomsByType");

                                                            if (roomTypes != null && !roomTypes.isEmpty()) {
                                                            %>
                                                            <div class="card">
                                                                <div class="card-header"><span>Room Directory</span>
                                                                </div>
                                                                <div class="card-body p-0">
                                                                    <% for (RoomType rt : roomTypes) { List<Room>
                                                                        typeRooms = (roomsByType != null) ?
                                                                        roomsByType.get(rt.getRoomTypeId()) : null;
                                                                        int roomCount = (typeRooms != null) ?
                                                                        typeRooms.size() : 0;
                                                                        String rateFormatted = String.format("%,.2f",
                                                                        rt.getRatePerNight());
                                                                        String roomCountLabel = roomCount + " room" +
                                                                        (roomCount != 1 ? "s" : "");
                                                                        int rtId = rt.getRoomTypeId();
                                                                        String rtName = rt.getTypeName();
                                                                        %>
                                                                        <div class="type-section">
                                                                            <div class="type-header">
                                                                                <span class="type-name">
                                                                                    <%= rtName %>
                                                                                </span>
                                                                                <div class="type-actions">
                                                                                    <% if (rt.isAvailable()) { %>
                                                                                        <span
                                                                                            class="badge badge-confirmed">Available</span>
                                                                                        <% } else { %>
                                                                                            <span
                                                                                                class="badge badge-cancelled">Unavailable</span>
                                                                                            <% } %>
                                                                                                <a href="${pageContext.request.contextPath}/rooms/edit/<%= rtId %>"
                                                                                                    class="btn btn-sm btn-secondary">Edit
                                                                                                    Type</a>
                                                                                                <form
                                                                                                    action="${pageContext.request.contextPath}/rooms/delete/<%= rtId %>"
                                                                                                    method="post"
                                                                                                    style="display:inline;"
                                                                                                    onsubmit="return confirm('Delete this room type?');">
                                                                                                    <button
                                                                                                        type="submit"
                                                                                                        class="btn btn-sm btn-secondary"
                                                                                                        style="color:#c0392b;">Delete
                                                                                                        Type</button>
                                                                                                </form>
                                                                                </div>
                                                                            </div>
                                                                            <div class="type-meta">
                                                                                <span>LKR <%= rateFormatted %> /
                                                                                        night</span>
                                                                                <span>
                                                                                    <%= rt.getCapacity() %> guests max
                                                                                </span>
                                                                                <span>
                                                                                    <%= roomCountLabel %>
                                                                                </span>
                                                                            </div>
                                                                            <% if (roomCount> 0) { %>
                                                                                <div class="room-grid">
                                                                                    <% for (Room room : typeRooms) { int
                                                                                        rid=room.getRoomId(); String
                                                                                        rnum=room.getRoomNumber(); int
                                                                                        rfloor=room.getFloor(); boolean
                                                                                        active=room.isActive(); String
                                                                                        chipClass=active ? "room-chip"
                                                                                        : "room-chip inactive" ; %>
                                                                                        <div class="<%= chipClass %>">
                                                                                            <span class="room-num">
                                                                                                <%= rnum %>
                                                                                            </span>
                                                                                            <span
                                                                                                class="floor-tag">Floor
                                                                                                <%= rfloor %></span>
                                                                                            <% if (!active) { %>
                                                                                                <span
                                                                                                    class="badge badge-cancelled"
                                                                                                    style="font-size:10px;padding:2px 6px;">Inactive</span>
                                                                                                <% } %>
                                                                                                    <span
                                                                                                        class="room-actions">
                                                                                                        <a href="${pageContext.request.contextPath}/rooms/room/edit/<%= rid %>"
                                                                                                            title="Edit">&#9998;</a>
                                                                                                        <form
                                                                                                            action="${pageContext.request.contextPath}/rooms/room/delete/<%= rid %>"
                                                                                                            method="post"
                                                                                                            style="display:inline;"
                                                                                                            onsubmit="return confirm('Delete this room?');">
                                                                                                            <button
                                                                                                                type="submit"
                                                                                                                title="Delete">&#10005;</button>
                                                                                                        </form>
                                                                                                    </span>
                                                                                        </div>
                                                                                        <% } %>
                                                                                </div>
                                                                                <% } else { %>
                                                                                    <div
                                                                                        style="color:#999;font-size:13px;">
                                                                                        No rooms assigned to this type
                                                                                    </div>
                                                                                    <% } %>
                                                                        </div>
                                                                        <% } %>
                                                                </div>
                                                            </div>
                                                            <% } else { %>
                                                                <div class="card">
                                                                    <div class="card-body"
                                                                        style="padding:40px;text-align:center;color:#888;">
                                                                        <p>No room types found. Create a room type
                                                                            first, then add rooms.</p>
                                                                        <a href="${pageContext.request.contextPath}/rooms/new"
                                                                            class="btn btn-primary"
                                                                            style="margin-top:10px;">+ Add Room Type</a>
                                                                    </div>
                                                                </div>
                                                                <% } %>
                                </div>
                            </main>
                            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
                    </body>

                    </html>