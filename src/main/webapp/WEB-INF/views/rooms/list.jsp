<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.RoomType" %>
        <%@ page import="java.util.List" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Room Types - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <div class="page-header">
                                <h1 class="page-title">ROOM TYPES</h1>
                                <div class="page-actions">
                                    <a href="${pageContext.request.contextPath}/rooms/new" class="btn btn-primary">
                                        + Add Room Type
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
                                                        <div class="card-header">
                                                            <span>Room Type Directory</span>
                                                        </div>
                                                        <div class="card-body p-0">
                                                            <% @SuppressWarnings("unchecked") List<RoomType> roomTypes =
                                                                (List<RoomType>) request.getAttribute("roomTypes"); %>
                                                                    <% if (roomTypes !=null && !roomTypes.isEmpty()) {
                                                                        %>
                                                                        <table class="table">
                                                                            <thead>
                                                                                <tr>
                                                                                    <th>ID</th>
                                                                                    <th>Type Name</th>
                                                                                    <th>Rate / Night (LKR)</th>
                                                                                    <th>Capacity</th>
                                                                                    <th>Description</th>
                                                                                    <th>Status</th>
                                                                                    <th>Actions</th>
                                                                                </tr>
                                                                            </thead>
                                                                            <tbody>
                                                                                <% for (RoomType rt : roomTypes) { %>
                                                                                    <tr>
                                                                                        <td>
                                                                                            <%= rt.getRoomTypeId() %>
                                                                                        </td>
                                                                                        <td><strong>
                                                                                                <%= rt.getTypeName() %>
                                                                                            </strong></td>
                                                                                        <td>
                                                                                            <%= String.format("%,.2f",
                                                                                                rt.getRatePerNight()) %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= rt.getCapacity() %>
                                                                                                guests
                                                                                        </td>
                                                                                        <td>
                                                                                            <%= rt.getDescription()
                                                                                                !=null ?
                                                                                                rt.getDescription()
                                                                                                : "-" %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <% if (rt.isAvailable()) {
                                                                                                %>
                                                                                                <span
                                                                                                    class="badge badge-confirmed">Available</span>
                                                                                                <% } else { %>
                                                                                                    <span
                                                                                                        class="badge badge-cancelled">Unavailable</span>
                                                                                                    <% } %>
                                                                                        </td>
                                                                                        <td>
                                                                                            <div
                                                                                                style="display: flex; gap: 6px;">
                                                                                                <a href="${pageContext.request.contextPath}/rooms/edit/<%= rt.getRoomTypeId() %>"
                                                                                                    class="btn btn-sm btn-secondary">Edit</a>
                                                                                                <form
                                                                                                    action="${pageContext.request.contextPath}/rooms/delete/<%= rt.getRoomTypeId() %>"
                                                                                                    method="post"
                                                                                                    style="display: inline;"
                                                                                                    onsubmit="return confirm('Are you sure you want to delete the room type: <%= rt.getTypeName() %>?');">
                                                                                                    <button
                                                                                                        type="submit"
                                                                                                        class="btn btn-sm btn-secondary"
                                                                                                        style="color: #c0392b;">Delete</button>
                                                                                                </form>
                                                                                            </div>
                                                                                        </td>
                                                                                    </tr>
                                                                                    <% } %>
                                                                            </tbody>
                                                                        </table>
                                                                        <% } else { %>
                                                                            <div
                                                                                style="padding: 40px; text-align: center; color: #888;">
                                                                                <p>No room types found.</p>
                                                                                <a href="${pageContext.request.contextPath}/rooms/new"
                                                                                    class="btn btn-primary"
                                                                                    style="margin-top: 10px;">+ Add Room
                                                                                    Type</a>
                                                                            </div>
                                                                            <% } %>
                                                        </div>
                                                    </div>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
            </body>

            </html>