<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>

    <body>
        <div class="login-container">
            <div class="login-box">
                <div class="login-header">
                    <h1 class="login-title">OCEAN VIEW RESORT</h1>
                    <p>Reservation Management System</p>
                </div>

                <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                    <% if (errorMessage !=null) { %>
                        <div class="alert alert-error">
                            <strong>Error:</strong>
                            <%= errorMessage %>
                        </div>
                        <% } %>

                            <form action="${pageContext.request.contextPath}/login" method="post">
                                <div class="form-group">
                                    <label for="username" class="form-label">Username</label>
                                    <input type="text" id="username" name="username" class="form-control"
                                        value="<%= request.getAttribute(" username") !=null ?
                                        request.getAttribute("username") : "" %>"
                                    required
                                    autofocus>
                                </div>

                                <div class="form-group">
                                    <label for="password" class="form-label">Password</label>
                                    <input type="password" id="password" name="password" class="form-control" required>
                                </div>

                                <button type="submit" class="btn btn-primary" style="width: 100%;">
                                    LOGIN
                                </button>
                            </form>

                            <div class="mt-2 text-center">
                                <a href="${pageContext.request.contextPath}/help" class="btn btn-secondary btn-sm">
                                    Help &amp; Instructions
                                </a>
                            </div>

                            <div class="mt-2 text-center" style="font-size: 13px; color: var(--color-gray-600);">
                                <p>Default credentials: admin / 123456</p>
                            </div>
            </div>
        </div>
    </body>

    </html>
