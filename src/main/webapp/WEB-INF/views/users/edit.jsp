<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.User" %>
        <%@ page import="com.oceanview.model.User.UserRole" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Edit User - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <% User editUser=(User) request.getAttribute("editUser"); %>
                                <% Integer currentUserId=(Integer) request.getAttribute("currentUserId"); %>

                                    <% if (editUser !=null) { %>
                                        <% boolean isSelf=editUser.getUserId().equals(currentUserId); %>

                                            <div class="page-header">
                                                <h1 class="page-title">EDIT USER</h1>
                                            </div>

                                            <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                                                <% if (errorMessage !=null) { %>
                                                    <div class="alert alert-error">
                                                        <%= errorMessage %>
                                                    </div>
                                                    <% } %>

                                                        <%-- Form validated by JS before submission --%>
                                                            <form
                                                                action="${pageContext.request.contextPath}/users/update/<%= editUser.getUserId() %>"
                                                                method="post" onsubmit="return validateEditUserForm()">
                                                                <div class="card">
                                                                    <div class="card-header">Update User Information
                                                                    </div>
                                                                    <div class="card-body">
                                                                        <div class="form-row">
                                                                            <div class="form-group">
                                                                                <label for="username"
                                                                                    class="form-label">Username</label>
                                                                                <input type="text" id="username"
                                                                                    class="form-control"
                                                                                    value="<%= editUser.getUsername() %>"
                                                                                    readonly
                                                                                    style="background-color: #f0f0f0; cursor: not-allowed;">
                                                                                <small
                                                                                    style="color: #888; display: block; margin-top: 0.25rem;">
                                                                                    Username cannot be changed
                                                                                </small>
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label for="fullName"
                                                                                    class="form-label">Full Name
                                                                                    *</label>
                                                                                <input type="text" id="fullName"
                                                                                    name="fullName" class="form-control"
                                                                                    value="<%= editUser.getFullName() %>"
                                                                                    required autofocus>
                                                                            </div>
                                                                        </div>

                                                                        <div class="form-row">
                                                                            <div class="form-group">
                                                                                <label for="email"
                                                                                    class="form-label">Email
                                                                                    Address</label>
                                                                                <% if (editUser.getEmail() !=null) { %>
                                                                                    <input type="email" id="email"
                                                                                        name="email"
                                                                                        class="form-control"
                                                                                        value="<%= editUser.getEmail() %>">
                                                                                    <% } else { %>
                                                                                        <input type="email" id="email"
                                                                                            name="email"
                                                                                            class="form-control"
                                                                                            value="">
                                                                                        <% } %>
                                                                            </div>
                                                                            <div class="form-group">
                                                                                <label for="role"
                                                                                    class="form-label">Role *</label>
                                                                                <% if (isSelf) { %>
                                                                                    <select id="role"
                                                                                        class="form-control" disabled>
                                                                                        <% } else { %>
                                                                                            <select id="role"
                                                                                                name="role"
                                                                                                class="form-control"
                                                                                                required>
                                                                                                <% } %>
                                                                                                    <option
                                                                                                        value="RECEPTIONIST"
                                                                                                        <% if
                                                                                                        (editUser.getRole()==UserRole.RECEPTIONIST)
                                                                                                        { %>selected<% }
                                                                                                            %>>
                                                                                                            Receptionist
                                                                                                    </option>
                                                                                                    <option
                                                                                                        value="MANAGER"
                                                                                                        <% if
                                                                                                        (editUser.getRole()==UserRole.MANAGER)
                                                                                                        { %>selected<% }
                                                                                                            %>>
                                                                                                            Manager
                                                                                                    </option>
                                                                                                    <option
                                                                                                        value="ADMIN" <%
                                                                                                        if
                                                                                                        (editUser.getRole()==UserRole.ADMIN)
                                                                                                        { %>selected<% }
                                                                                                            %>>
                                                                                                            Administrator
                                                                                                    </option>
                                                                                            </select>
                                                                                            <% if (isSelf) { %>
                                                                                                <input type="hidden"
                                                                                                    name="role"
                                                                                                    value="<%= editUser.getRole().name() %>">
                                                                                                <small
                                                                                                    style="color: #888; display: block; margin-top: 0.25rem;">
                                                                                                    You cannot change
                                                                                                    your own role
                                                                                                </small>
                                                                                                <% } %>
                                                                            </div>
                                                                        </div>

                                                                        <div class="form-group">
                                                                            <label class="form-label">
                                                                                <% if (isSelf) { %>
                                                                                    <input type="checkbox" name="active"
                                                                                        checked disabled
                                                                                        style="margin-right: 0.5rem;">
                                                                                    <% } else if (editUser.isActive()) {
                                                                                        %>
                                                                                        <input type="checkbox"
                                                                                            name="active" checked
                                                                                            style="margin-right: 0.5rem;">
                                                                                        <% } else { %>
                                                                                            <input type="checkbox"
                                                                                                name="active"
                                                                                                style="margin-right: 0.5rem;">
                                                                                            <% } %>
                                                                                                Account Active
                                                                            </label>
                                                                            <% if (isSelf && editUser.isActive()) { %>
                                                                                <input type="hidden" name="active"
                                                                                    value="on">
                                                                                <small
                                                                                    style="color: #888; display: block; margin-top: 0.25rem;">
                                                                                    You cannot deactivate your own
                                                                                    account
                                                                                </small>
                                                                                <% } %>
                                                                        </div>
                                                                    </div>

                                                                    <div class="card-footer">
                                                                        <div class="d-flex justify-between">
                                                                            <a href="${pageContext.request.contextPath}/users/view/<%= editUser.getUserId() %>"
                                                                                class="btn btn-secondary">Cancel</a>
                                                                            <button type="submit"
                                                                                class="btn btn-primary">Update
                                                                                User</button>
                                                                        </div>
                                                                    </div>
                                                                </div>
                                                            </form>
                                                            <% } else { %>
                                                                <div class="alert alert-error">User not found.</div>
                                                                <% } %>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                        <%-- JavaScript for form validation --%>
                            <script src="${pageContext.request.contextPath}/js/users.js"></script>
            </body>

            </html>