<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Add New User - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <style>
            /* Password strength bar */
            .password-strength-container {
                background: #e9ecef;
                border-radius: 4px;
                height: 6px;
                margin-top: 0.5rem;
                overflow: hidden;
            }

            .password-strength-bar {
                height: 100%;
                width: 0%;
                border-radius: 4px;
                transition: width 0.3s, background-color 0.3s;
                font-size: 0;
                line-height: 6px;
                text-align: center;
                color: white;
            }

            .password-match-msg {
                font-size: 0.85rem;
                margin-top: 0.25rem;
                display: none;
            }
        </style>
    </head>

    <body>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

            <main class="main-content">
                <div class="container">
                    <div class="page-header">
                        <h1 class="page-title">ADD NEW USER</h1>
                    </div>

                    <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                        <% if (errorMessage !=null) { %>
                            <div class="alert alert-error">
                                <%= errorMessage %>
                            </div>
                            <% } %>

                                <%-- Form validated entirely by JavaScript before submission --%>
                                    <form action="${pageContext.request.contextPath}/users/create" method="post"
                                        onsubmit="return validateCreateUserForm()">
                                        <div class="card">
                                            <div class="card-header">User Account Details</div>
                                            <div class="card-body">
                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="username" class="form-label">Username *</label>
                                                        <input type="text" id="username" name="username"
                                                            class="form-control" required autofocus
                                                            pattern="[a-zA-Z0-9_]{3,50}"
                                                            title="3-50 characters: letters, numbers, underscores only"
                                                            placeholder="Enter username">
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="fullName" class="form-label">Full Name *</label>
                                                        <input type="text" id="fullName" name="fullName"
                                                            class="form-control" required placeholder="Enter full name">
                                                    </div>
                                                </div>

                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="email" class="form-label">Email Address</label>
                                                        <input type="email" id="email" name="email" class="form-control"
                                                            placeholder="user@oceanview.com">
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="role" class="form-label">Role *</label>
                                                        <select id="role" name="role" class="form-control" required>
                                                            <option value="">-- Select Role --</option>
                                                            <option value="RECEPTIONIST">Receptionist</option>
                                                            <option value="MANAGER">Manager</option>
                                                            <option value="ADMIN">Administrator</option>
                                                        </select>
                                                    </div>
                                                </div>

                                                <div class="form-row">
                                                    <div class="form-group">
                                                        <label for="password" class="form-label">Password *</label>
                                                        <input type="password" id="password" name="password"
                                                            class="form-control" required
                                                            placeholder="Minimum 8 characters">
                                                        <%-- Real-time password strength bar powered by JS --%>
                                                            <div class="password-strength-container">
                                                                <div id="passwordStrengthBar"
                                                                    class="password-strength-bar"></div>
                                                            </div>
                                                            <small
                                                                style="color: #888; display: block; margin-top: 0.25rem;">
                                                                Must contain uppercase, lowercase, digit, and special
                                                                character
                                                            </small>
                                                    </div>
                                                    <div class="form-group">
                                                        <label for="confirmPassword" class="form-label">Confirm Password
                                                            *</label>
                                                        <input type="password" id="confirmPassword"
                                                            name="confirmPassword" class="form-control" required
                                                            placeholder="Re-enter password">
                                                        <%-- Real-time match indicator powered by JS --%>
                                                            <div id="passwordMatchMsg" class="password-match-msg"></div>
                                                    </div>
                                                </div>

                                                <div class="form-group">
                                                    <label class="form-label">
                                                        <input type="checkbox" name="active" checked
                                                            style="margin-right: 0.5rem;">
                                                        Account Active
                                                    </label>
                                                </div>
                                            </div>

                                            <div class="card-footer">
                                                <div class="d-flex justify-between">
                                                    <a href="${pageContext.request.contextPath}/users"
                                                        class="btn btn-secondary">Cancel</a>
                                                    <button type="submit" class="btn btn-primary">Create User</button>
                                                </div>
                                            </div>
                                        </div>
                                    </form>
                </div>
            </main>

            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                <%-- JavaScript for password validation, strength bar, and match checking --%>
                    <script src="${pageContext.request.contextPath}/js/users.js"></script>
    </body>

    </html>