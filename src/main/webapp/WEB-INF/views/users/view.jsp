<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.User" %>
        <%@ page import="com.oceanview.model.User.UserRole" %>
            <%@ page import="java.time.format.DateTimeFormatter" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>View User - Ocean View Resort</title>
                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                </head>

                <body>
                    <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                        <main class="main-content">
                            <div class="container">
                                <% User viewUser=(User) request.getAttribute("viewUser"); %>
                                    <% Integer currentUserId=(Integer) request.getAttribute("currentUserId"); %>

                                        <% if (viewUser !=null) { %>
                                            <% DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd"); %>

                                                <div class="page-header">
                                                    <h1 class="page-title">USER DETAILS</h1>
                                                    <div class="page-actions">
                                                        <a href="${pageContext.request.contextPath}/users"
                                                            class="btn btn-secondary">&larr; Back to Users</a>
                                                    </div>
                                                </div>

                                                <%-- Success/Error Messages --%>
                                                    <% String successMsg=(String)
                                                        request.getAttribute("successMessage"); %>
                                                        <% if (successMsg !=null) { %>
                                                            <div class="alert alert-success">
                                                                <%= successMsg %>
                                                            </div>
                                                            <% } %>
                                                                <% String errorMsg=(String)
                                                                    request.getAttribute("errorMessage"); %>
                                                                    <% if (errorMsg !=null) { %>
                                                                        <div class="alert alert-error">
                                                                            <%= errorMsg %>
                                                                        </div>
                                                                        <% } %>

                                                                            <div class="card">
                                                                                <div
                                                                                    class="card-header d-flex justify-between align-center">
                                                                                    <span>User Profile: <%=
                                                                                            viewUser.getUsername() %>
                                                                                            </span>
                                                                                    <% if (viewUser.isActive()) { %>
                                                                                        <span
                                                                                            class="badge badge-success">Active</span>
                                                                                        <% } else { %>
                                                                                            <span
                                                                                                class="badge badge-danger">Inactive</span>
                                                                                            <% } %>
                                                                                </div>
                                                                                <div class="card-body">
                                                                                    <div class="form-row">
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>User
                                                                                                    ID</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <%= viewUser.getUserId()
                                                                                                    %>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>Username</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <%= viewUser.getUsername()
                                                                                                    %>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="form-row">
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>Full
                                                                                                    Name</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <%= viewUser.getFullName()
                                                                                                    %>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>Email</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <% if
                                                                                                    (viewUser.getEmail()
                                                                                                    !=null) { %>
                                                                                                    <%= viewUser.getEmail()
                                                                                                        %>
                                                                                                        <% } else { %>
                                                                                                            -
                                                                                                            <% } %>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>
                                                                                    <div class="form-row">
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>Role</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <% if
                                                                                                    (viewUser.getRole()==UserRole.ADMIN)
                                                                                                    { %>
                                                                                                    <span
                                                                                                        class="badge badge-primary">
                                                                                                        <%= viewUser.getRole()
                                                                                                            %>
                                                                                                    </span>
                                                                                                    <% } else if
                                                                                                        (viewUser.getRole()==UserRole.MANAGER)
                                                                                                        { %>
                                                                                                        <span
                                                                                                            class="badge badge-warning">
                                                                                                            <%= viewUser.getRole()
                                                                                                                %>
                                                                                                        </span>
                                                                                                        <% } else { %>
                                                                                                            <span
                                                                                                                class="badge badge-secondary">
                                                                                                                <%= viewUser.getRole()
                                                                                                                    %>
                                                                                                            </span>
                                                                                                            <% } %>
                                                                                            </div>
                                                                                        </div>
                                                                                        <div class="form-group">
                                                                                            <label
                                                                                                class="form-label"><strong>Account
                                                                                                    Created</strong></label>
                                                                                            <div
                                                                                                style="padding: 0.5rem 0;">
                                                                                                <% if
                                                                                                    (viewUser.getCreatedAt()
                                                                                                    !=null) { %>
                                                                                                    <%= viewUser.getCreatedAt().format(dtf)
                                                                                                        %>
                                                                                                        <% } else { %>
                                                                                                            -
                                                                                                            <% } %>
                                                                                            </div>
                                                                                        </div>
                                                                                    </div>
                                                                                </div>

                                                                                <%-- Action Buttons - JS handles
                                                                                    confirmations --%>
                                                                                    <div class="card-footer">
                                                                                        <div class="d-flex gap-1"
                                                                                            style="flex-wrap: wrap;">
                                                                                            <a href="${pageContext.request.contextPath}/users/edit/<%= viewUser.getUserId() %>"
                                                                                                class="btn btn-primary">Edit
                                                                                                User</a>
                                                                                            <a href="${pageContext.request.contextPath}/users/reset-password/<%= viewUser.getUserId() %>"
                                                                                                class="btn btn-secondary">Reset
                                                                                                Password</a>

                                                                                            <% if
                                                                                                (!viewUser.getUserId().equals(currentUserId))
                                                                                                { %>
                                                                                                <%-- Toggle Status - JS
                                                                                                    confirmation --%>
                                                                                                    <form
                                                                                                        id="toggleForm-<%= viewUser.getUserId() %>"
                                                                                                        action="${pageContext.request.contextPath}/users/toggle-status/<%= viewUser.getUserId() %>"
                                                                                                        method="post"
                                                                                                        style="display: inline;">
                                                                                                        <% if
                                                                                                            (viewUser.isActive())
                                                                                                            { %>
                                                                                                            <button
                                                                                                                type="button"
                                                                                                                class="btn btn-warning"
                                                                                                                onclick="confirmToggleStatus('<%= viewUser.getUsername() %>', true, <%= viewUser.getUserId() %>)">
                                                                                                                Deactivate
                                                                                                            </button>
                                                                                                            <% } else {
                                                                                                                %>
                                                                                                                <button
                                                                                                                    type="button"
                                                                                                                    class="btn btn-primary"
                                                                                                                    onclick="confirmToggleStatus('<%= viewUser.getUsername() %>', false, <%= viewUser.getUserId() %>)">
                                                                                                                    Activate
                                                                                                                </button>
                                                                                                                <% } %>
                                                                                                    </form>

                                                                                                    <%-- Delete - JS
                                                                                                        confirmation
                                                                                                        --%>
                                                                                                        <form
                                                                                                            id="deleteForm-<%= viewUser.getUserId() %>"
                                                                                                            action="${pageContext.request.contextPath}/users/delete/<%= viewUser.getUserId() %>"
                                                                                                            method="post"
                                                                                                            style="display: inline;">
                                                                                                            <button
                                                                                                                type="button"
                                                                                                                class="btn btn-danger"
                                                                                                                onclick="confirmDeleteUser('<%= viewUser.getUsername() %>', <%= viewUser.getUserId() %>)">
                                                                                                                Delete
                                                                                                                User
                                                                                                            </button>
                                                                                                        </form>
                                                                                                        <% } %>
                                                                                        </div>
                                                                                    </div>
                                                                            </div>
                                                                            <% } else { %>
                                                                                <div class="alert alert-error">User not
                                                                                    found.</div>
                                                                                <a href="${pageContext.request.contextPath}/users"
                                                                                    class="btn btn-secondary">Back to
                                                                                    Users</a>
                                                                                <% } %>
                            </div>
                        </main>

                        <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                            <%-- JavaScript for confirmation dialogs --%>
                                <script src="${pageContext.request.contextPath}/js/users.js"></script>
                </body>

                </html>