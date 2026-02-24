<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Guest" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Guest Details - Ocean View Resort</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                <main class="main-content">
                    <div class="container">
                        <% Guest guest=(Guest) request.getAttribute("guest"); %>
                            <% if (guest !=null) { %>
                                <div class="page-header">
                                    <h1 class="page-title">GUEST DETAILS</h1>
                                    <div class="page-actions">
                                        <a href="${pageContext.request.contextPath}/guests" class="btn btn-secondary">
                                            ← Back to List
                                        </a>
                                        <a href="${pageContext.request.contextPath}/guests/edit/<%= guest.getGuestId() %>"
                                            class="btn btn-primary">
                                            Edit Details
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
                                                                <h2>Guest ID: <%= guest.getGuestId() %>
                                                                </h2>
                                                            </div>

                                                            <div class="card-body">
                                                                <div class="form-row">
                                                                    <div>
                                                                        <h3>Personal Information</h3>
                                                                        <p><strong>Full Name:</strong>
                                                                            <%= guest.getGuestName() %>
                                                                        </p>
                                                                        <p><strong>Contact Number:</strong>
                                                                            <%= guest.getContactNumber() %>
                                                                        </p>
                                                                        <% if (guest.getEmail() !=null &&
                                                                            !guest.getEmail().isEmpty()) { %>
                                                                            <p><strong>Email:</strong>
                                                                                <%= guest.getEmail() %>
                                                                            </p>
                                                                            <% } else { %>
                                                                                <p><strong>Email:</strong> <em>Not
                                                                                        provided</em></p>
                                                                                <% } %>
                                                                    </div>

                                                                    <div>
                                                                        <h3>Identification</h3>
                                                                        <% if (guest.getIdentificationType() !=null &&
                                                                            !guest.getIdentificationType().isEmpty()) {
                                                                            %>
                                                                            <p><strong>Type:</strong>
                                                                                <%= guest.getIdentificationType() %>
                                                                            </p>
                                                                            <p><strong>Number:</strong>
                                                                                <%= guest.getIdentificationNumber() %>
                                                                            </p>
                                                                            <% } else { %>
                                                                                <p><em>No identification details
                                                                                        provided</em></p>
                                                                                <% } %>
                                                                    </div>
                                                                </div>

                                                                <hr
                                                                    style="margin: 1.5rem 0; border: 1px solid var(--color-gray-300);">

                                                                <h3>Address</h3>
                                                                <p>
                                                                    <%= guest.getAddress() %>
                                                                </p>

                                                                <hr
                                                                    style="margin: 1.5rem 0; border: 1px solid var(--color-gray-300);">

                                                                <div class="form-row">
                                                                    <div>
                                                                        <h3>Registration Details</h3>
                                                                        <p><strong>Registered On:</strong>
                                                                            <%= guest.getCreatedAt() %>
                                                                        </p>
                                                                        <p><strong>Last Updated:</strong>
                                                                            <%= guest.getUpdatedAt() %>
                                                                        </p>
                                                                    </div>
                                                                </div>
                                                            </div>

                                                            <div class="card-footer">
                                                                <div class="d-flex justify-between">
                                                                    <form
                                                                        action="${pageContext.request.contextPath}/guests/delete/<%= guest.getGuestId() %>"
                                                                        method="post"
                                                                        onsubmit="return confirm('Are you sure you want to delete this guest? This action cannot be undone.');">
                                                                        <button type="submit"
                                                                            class="btn btn-secondary">Delete
                                                                            Guest</button>
                                                                    </form>
                                                                </div>
                                                            </div>
                                                        </div>
                                                        <% } else { %>
                                                            <div class="alert alert-error">
                                                                Guest not found.
                                                            </div>
                                                            <% } %>
                    </div>
                </main>

                <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
        </body>

        </html>