<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Guest" %>
        <%@ page import="java.util.List" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Guests - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <div class="page-header">
                                <h1 class="page-title">GUESTS</h1>
                                <div class="page-actions">
                                    <a href="${pageContext.request.contextPath}/guests/new" class="btn btn-primary">
                                        + Register New Guest
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
                                                        <div class="card-header d-flex justify-between align-center">
                                                            <span>Guest Directory</span>
                                                            <form
                                                                action="${pageContext.request.contextPath}/guests/search"
                                                                method="get" class="d-flex gap-1">
                                                                <input type="text" name="q"
                                                                    placeholder="Search by name or contact..."
                                                                    class="form-control" style="width: 300px;"
                                                                    value="<%= request.getAttribute(" searchTerm")
                                                                    !=null ? request.getAttribute("searchTerm") : ""
                                                                    %>">
                                                                <button type="submit"
                                                                    class="btn btn-secondary">Search</button>
                                                            </form>
                                                        </div>

                                                        <div class="card-body p-0">
                                                            <% Boolean searchResults=(Boolean)
                                                                request.getAttribute("searchResults"); %>
                                                                <% if (searchResults !=null && searchResults) { %>
                                                                    <div class="alert alert-info" style="margin: 1rem;">
                                                                        Search results for: "<%=
                                                                            request.getAttribute("searchTerm") %>"
                                                                    </div>
                                                                    <% } %>

                                                                        <% @SuppressWarnings("unchecked") List<Guest>
                                                                            guests = (List<Guest>)
                                                                                request.getAttribute("guests"); %>
                                                                                <% if (guests !=null &&
                                                                                    !guests.isEmpty()) { %>
                                                                                    <table class="table">
                                                                                        <thead>
                                                                                            <tr>
                                                                                                <th>Guest ID</th>
                                                                                                <th>Name</th>
                                                                                                <th>Contact Number</th>
                                                                                                <th>Email</th>
                                                                                                <th>Address</th>
                                                                                                <th>Actions</th>
                                                                                            </tr>
                                                                                        </thead>
                                                                                        <tbody>
                                                                                            <% for (Guest guest :
                                                                                                guests) { %>
                                                                                                <tr>
                                                                                                    <td><strong>
                                                                                                            <%= guest.getGuestId()
                                                                                                                %>
                                                                                                        </strong></td>
                                                                                                    <td>
                                                                                                        <%= guest.getGuestName()
                                                                                                            %>
                                                                                                    </td>
                                                                                                    <td>
                                                                                                        <%= guest.getContactNumber()
                                                                                                            %>
                                                                                                    </td>
                                                                                                    <td>
                                                                                                        <%= guest.getEmail()
                                                                                                            !=null ?
                                                                                                            guest.getEmail()
                                                                                                            : "-" %>
                                                                                                    </td>
                                                                                                    <td>
                                                                                                        <%= guest.getAddress()
                                                                                                            %>
                                                                                                    </td>
                                                                                                    <td>
                                                                                                        <a href="${pageContext.request.contextPath}/guests/view/<%= guest.getGuestId() %>"
                                                                                                            class="btn btn-sm btn-secondary">
                                                                                                            View
                                                                                                        </a>
                                                                                                        <a href="${pageContext.request.contextPath}/guests/edit/<%= guest.getGuestId() %>"
                                                                                                            class="btn btn-sm btn-secondary">
                                                                                                            Edit
                                                                                                        </a>
                                                                                                    </td>
                                                                                                </tr>
                                                                                                <% } %>
                                                                                        </tbody>
                                                                                    </table>
                                                                                    <% } else { %>
                                                                                        <div
                                                                                            style="padding: 2rem; text-align: center;">
                                                                                            <p>No guests found.</p>
                                                                                            <a href="${pageContext.request.contextPath}/guests/new"
                                                                                                class="btn btn-primary mt-1">
                                                                                                Register First Guest
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