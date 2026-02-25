<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.RoomType" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>${formTitle} - Ocean View Resort</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                <main class="main-content">
                    <div class="container">
                        <div class="page-header">
                            <h1 class="page-title">${formTitle}</h1>
                            <div class="page-actions">
                                <a href="${pageContext.request.contextPath}/rooms" class="btn btn-secondary">
                                    ← Back to Room Types
                                </a>
                            </div>
                        </div>

                        <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                            <% if (errorMessage !=null) { %>
                                <div class="alert alert-error">
                                    <%= errorMessage %>
                                </div>
                                <% } %>

                                    <% RoomType roomType=(RoomType) request.getAttribute("roomType"); %>

                                        <div class="card">
                                            <div class="card-header">
                                                <span>${formTitle}</span>
                                            </div>
                                            <div class="card-body">
                                                <form action="${formAction}" method="post">

                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label class="form-label" for="typeName">Room Type Name
                                                                *</label>
                                                            <input type="text" id="typeName" name="typeName"
                                                                class="form-control" required maxlength="50"
                                                                placeholder="e.g. Deluxe, Suite, Family"
                                                                value="<%= roomType != null ? roomType.getTypeName() : "" %>">
                                                        </div>
                                                        <div class="form-group">
                                                            <label class="form-label" for="ratePerNight">Rate Per Night
                                                                (LKR) *</label>
                                                            <input type="number" id="ratePerNight" name="ratePerNight"
                                                                class="form-control" required step="0.01" min="1"
                                                                max="99999.99" placeholder="e.g. 250.00"
                                                                value="<%= roomType != null ? roomType.getRatePerNight().toPlainString() : "" %>">
                                                        </div>
                                                    </div>

                                                    <div class="form-row">
                                                        <div class="form-group">
                                                            <label class="form-label" for="capacity">Max Guests
                                                                *</label>
                                                            <input type="number" id="capacity" name="capacity"
                                                                class="form-control" required min="1" max="20"
                                                                placeholder="e.g. 4"
                                                                value="<%= roomType != null ? roomType.getCapacity() : "" %>">
                                                        </div>
                                                        <% if (roomType !=null) { %>
                                                            <div class="form-group">
                                                                <label class="form-label"
                                                                    for="isAvailable">Availability</label>
                                                                <div
                                                                    style="display: flex; align-items: center; gap: 8px; padding-top: 6px;">
                                                                    <input type="checkbox" id="isAvailable"
                                                                        name="isAvailable"
                                                                        style="width: 18px; height: 18px;"
                                                                        <%=roomType.isAvailable() ? "checked" : "" %>>
                                                                    <label for="isAvailable"
                                                                        style="margin: 0; font-weight: normal;">
                                                                        Room type is available for booking
                                                                    </label>
                                                                </div>
                                                            </div>
                                                            <% } %>
                                                    </div>

                                                    <div class="form-group">
                                                        <label class="form-label" for="description">Description</label>
                                                        <textarea id="description" name="description"
                                                            class="form-control" rows="3"
                                                            placeholder="Comfortable room with modern amenities..."
                                                            style="resize: vertical;"><%= roomType != null && roomType.getDescription() != null ? roomType.getDescription() : "" %></textarea>
                                                    </div>

                                                    <div style="margin-top: 20px; display: flex; gap: 10px;">
                                                        <button type="submit" class="btn btn-primary">
                                                            <%= roomType !=null ? "Update Room Type"
                                                                : "Create Room Type" %>
                                                        </button>
                                                        <a href="${pageContext.request.contextPath}/rooms"
                                                            class="btn btn-secondary">Cancel</a>
                                                    </div>
                                                </form>
                                            </div>
                                        </div>
                    </div>
                </main>

                <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
        </body>

        </html>