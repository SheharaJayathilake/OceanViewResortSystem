<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>500 - Server Error</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-container">
        <div class="login-box">
            <div class="text-center">
                <h1 style="font-size: 6rem; margin: 0;">500</h1>
                <h2>INTERNAL SERVER ERROR</h2>
                <p style="margin: 2rem 0;">Something went wrong on our end. We're working to fix the issue.</p>
                
                <% if (exception != null) { %>
                    <div class="alert alert-error" style="text-align: left; margin: 1rem 0;">
                        <strong>Error Details:</strong><br>
                        <%= exception.getMessage() %>
                    </div>
                <% } %>
                
                <div class="d-flex gap-1" style="justify-content: center; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                        Go to Dashboard
                    </a>
                    <a href="javascript:location.reload()" class="btn btn-secondary">
                        Retry
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
