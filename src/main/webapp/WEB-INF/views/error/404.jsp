<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>404 - Page Not Found</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <div class="login-container">
        <div class="login-box">
            <div class="text-center">
                <h1 style="font-size: 6rem; margin: 0;">404</h1>
                <h2>PAGE NOT FOUND</h2>
                <p style="margin: 2rem 0;">The page you are looking for does not exist or has been moved.</p>
                
                <div class="d-flex gap-1" style="justify-content: center; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                        Go to Dashboard
                    </a>
                    <a href="javascript:history.back()" class="btn btn-secondary">
                        Go Back
                    </a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
