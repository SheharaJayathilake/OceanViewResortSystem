<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.User" %>
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Password - Ocean View Resort</title>
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            <style>
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
                        <% User resetUser=(User) request.getAttribute("resetUser"); %>

                            <% if (resetUser !=null) { %>
                                <div class="page-header">
                                    <h1 class="page-title">RESET PASSWORD</h1>
                                </div>

                                <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                                    <% if (errorMessage !=null) { %>
                                        <div class="alert alert-error">
                                            <%= errorMessage %>
                                        </div>
                                        <% } %>

                                            <div class="alert alert-info">
                                                Resetting password for user:
                                                <strong>
                                                    <%= resetUser.getUsername() %>
                                                </strong>
                                                (<%= resetUser.getFullName() %>)
                                            </div>

                                            <%-- Form validated by JavaScript before submission --%>
                                                <form
                                                    action="${pageContext.request.contextPath}/users/reset-password/<%= resetUser.getUserId() %>"
                                                    method="post" onsubmit="return validateResetPasswordForm()">
                                                    <div class="card">
                                                        <div class="card-header">Set New Password</div>
                                                        <div class="card-body">
                                                            <div class="form-row">
                                                                <div class="form-group">
                                                                    <label for="newPassword" class="form-label">New
                                                                        Password *</label>
                                                                    <input type="password" id="newPassword"
                                                                        name="newPassword" class="form-control" required
                                                                        placeholder="Enter new password" autofocus>
                                                                    <%-- Real-time password strength bar --%>
                                                                        <div class="password-strength-container">
                                                                            <div id="passwordStrengthBar"
                                                                                class="password-strength-bar"></div>
                                                                        </div>
                                                                        <small
                                                                            style="color: #888; display: block; margin-top: 0.25rem;">
                                                                            Must contain uppercase, lowercase, digit,
                                                                            and special character
                                                                        </small>
                                                                </div>
                                                                <div class="form-group">
                                                                    <label for="confirmPassword"
                                                                        class="form-label">Confirm Password *</label>
                                                                    <input type="password" id="confirmPassword"
                                                                        name="confirmPassword" class="form-control"
                                                                        required placeholder="Re-enter password">
                                                                    <%-- Real-time match indicator --%>
                                                                        <div id="passwordMatchMsg"
                                                                            class="password-match-msg"></div>
                                                                </div>
                                                            </div>
                                                        </div>

                                                        <div class="card-footer">
                                                            <div class="d-flex justify-between">
                                                                <a href="${pageContext.request.contextPath}/users/view/<%= resetUser.getUserId() %>"
                                                                    class="btn btn-secondary">Cancel</a>
                                                                <button type="submit" class="btn btn-primary">Reset
                                                                    Password</button>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </form>
                                                <% } else { %>
                                                    <div class="alert alert-error">User not found.</div>
                                                    <a href="${pageContext.request.contextPath}/users"
                                                        class="btn btn-secondary">Back to Users</a>
                                                    <% } %>
                    </div>
                </main>

                <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                    <%-- JavaScript for password validation and strength checking --%>
                        <script src="${pageContext.request.contextPath}/js/users.js"></script>
        </body>

        </html>