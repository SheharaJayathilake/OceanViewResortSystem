<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.User" %>
        <%@ page import="com.oceanview.model.User.UserRole" %>
            <%@ page import="java.util.List" %>
                <%@ page import="java.time.format.DateTimeFormatter" %>
                    <!DOCTYPE html>
                    <html lang="en">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <title>Manage Users - Ocean View Resort</title>
                        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                    </head>

                    <body>
                        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                            <main class="main-content">
                                <div class="container">
                                    <div class="page-header">
                                        <h1 class="page-title">MANAGE USERS</h1>
                                        <div class="page-actions">
                                            <a href="${pageContext.request.contextPath}/users/new"
                                                class="btn btn-primary">+ Add New User</a>
                                        </div>
                                    </div>

                                    <%-- Success/Error Messages --%>
                                        <% String successMsg=(String) request.getAttribute("successMessage"); %>
                                            <% if (successMsg !=null) { %>
                                                <div class="alert alert-success">
                                                    <%= successMsg %>
                                                </div>
                                                <% } %>
                                                    <% String errorMsg=(String) request.getAttribute("errorMessage"); %>
                                                        <% if (errorMsg !=null) { %>
                                                            <div class="alert alert-error">
                                                                <%= errorMsg %>
                                                            </div>
                                                            <% } %>

                                                                <div class="card">
                                                                    <div
                                                                        class="card-header d-flex justify-between align-center">
                                                                        <span>User Directory</span>
                                                                        <%-- Client-side JS search (instant filtering)
                                                                            --%>
                                                                            <div class="d-flex gap-1">
                                                                                <input type="text" id="userSearchInput"
                                                                                    placeholder="Type to filter users..."
                                                                                    class="form-control"
                                                                                    style="width: 300px;">
                                                                            </div>
                                                                    </div>

                                                                    <div class="card-body p-0">
                                                                        <% @SuppressWarnings("unchecked") List<User>
                                                                            users = (List<User>)
                                                                                request.getAttribute("users"); %>
                                                                                <% if (users !=null && !users.isEmpty())
                                                                                    { %>
                                                                                    <% DateTimeFormatter
                                                                                        dtf=DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                                                                        %>
                                                                                        <table class="table"
                                                                                            id="usersTable">
                                                                                            <thead>
                                                                                                <tr>
                                                                                                    <th>ID</th>
                                                                                                    <th>Username</th>
                                                                                                    <th>Full Name</th>
                                                                                                    <th>Email</th>
                                                                                                    <th>Role</th>
                                                                                                    <th>Status</th>
                                                                                                    <th>Created</th>
                                                                                                    <th>Actions</th>
                                                                                                </tr>
                                                                                            </thead>
                                                                                            <tbody>
                                                                                                <% for (User u : users)
                                                                                                    { %>
                                                                                                    <tr>
                                                                                                        <td><strong>
                                                                                                                <%= u.getUserId()
                                                                                                                    %>
                                                                                                            </strong>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <%= u.getUsername()
                                                                                                                %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <%= u.getFullName()
                                                                                                                %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <% if
                                                                                                                (u.getEmail()
                                                                                                                !=null)
                                                                                                                { %>
                                                                                                                <%= u.getEmail()
                                                                                                                    %>
                                                                                                                    <% } else
                                                                                                                        {
                                                                                                                        %>
                                                                                                                        -
                                                                                                                        <% }
                                                                                                                            %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <% if
                                                                                                                (u.getRole()==UserRole.ADMIN)
                                                                                                                { %>
                                                                                                                <span
                                                                                                                    class="badge badge-primary">
                                                                                                                    <%= u.getRole()
                                                                                                                        %>
                                                                                                                </span>
                                                                                                                <% } else
                                                                                                                    if
                                                                                                                    (u.getRole()==UserRole.MANAGER)
                                                                                                                    { %>
                                                                                                                    <span
                                                                                                                        class="badge badge-warning">
                                                                                                                        <%= u.getRole()
                                                                                                                            %>
                                                                                                                    </span>
                                                                                                                    <% } else
                                                                                                                        {
                                                                                                                        %>
                                                                                                                        <span
                                                                                                                            class="badge badge-secondary">
                                                                                                                            <%= u.getRole()
                                                                                                                                %>
                                                                                                                        </span>
                                                                                                                        <% }
                                                                                                                            %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <% if
                                                                                                                (u.isActive())
                                                                                                                { %>
                                                                                                                <span
                                                                                                                    class="badge badge-success">Active</span>
                                                                                                                <% } else
                                                                                                                    { %>
                                                                                                                    <span
                                                                                                                        class="badge badge-danger">Inactive</span>
                                                                                                                    <% }
                                                                                                                        %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <% if
                                                                                                                (u.getCreatedAt()
                                                                                                                !=null)
                                                                                                                { %>
                                                                                                                <%= u.getCreatedAt().format(dtf)
                                                                                                                    %>
                                                                                                                    <% } else
                                                                                                                        {
                                                                                                                        %>
                                                                                                                        -
                                                                                                                        <% }
                                                                                                                            %>
                                                                                                        </td>
                                                                                                        <td>
                                                                                                            <a href="${pageContext.request.contextPath}/users/view/<%= u.getUserId() %>"
                                                                                                                class="btn btn-sm btn-secondary">View</a>
                                                                                                            <a href="${pageContext.request.contextPath}/users/edit/<%= u.getUserId() %>"
                                                                                                                class="btn btn-sm btn-secondary">Edit</a>
                                                                                                        </td>
                                                                                                    </tr>
                                                                                                    <% } %>
                                                                                            </tbody>
                                                                                        </table>
                                                                                        <%-- Hidden message shown by JS
                                                                                            when search finds no matches
                                                                                            --%>
                                                                                            <div id="noSearchResults"
                                                                                                style="display: none; padding: 2rem; text-align: center;">
                                                                                                <p>No users match your
                                                                                                    search.</p>
                                                                                            </div>
                                                                                            <% } else { %>
                                                                                                <div
                                                                                                    style="padding: 2rem; text-align: center;">
                                                                                                    <p>No users found.
                                                                                                    </p>
                                                                                                    <a href="${pageContext.request.contextPath}/users/new"
                                                                                                        class="btn btn-primary mt-1">Add
                                                                                                        First User</a>
                                                                                                </div>
                                                                                                <% } %>
                                                                    </div>
                                                                </div>
                                </div>
                            </main>

                            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                                <%-- JavaScript for client-side table filtering --%>
                                    <script src="${pageContext.request.contextPath}/js/users.js"></script>
                    </body>

                    </html>