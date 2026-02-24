<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Error - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>

    <body>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

            <main class="main-content">
                <div class="container">
                    <div class="page-header">
                        <h1 class="page-title">ERROR</h1>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <h2>Something went wrong</h2>
                        </div>
                        <div class="card-body">
                            <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                                <% if (errorMessage !=null) { %>
                                    <div class="alert alert-error">
                                        <%= errorMessage %>
                                    </div>
                                    <% } else { %>
                                        <p>An unexpected error occurred. Please try again or contact support if the
                                            problem persists.</p>
                                        <% } %>

                                            <% if (exception !=null) { %>
                                                <details style="margin-top: 1rem;">
                                                    <summary style="cursor: pointer; font-weight: bold;">Technical
                                                        Details</summary>
                                                    <pre
                                                        style="background: var(--color-gray-100); padding: 1rem; margin-top: 1rem; overflow: auto;">
<%= exception.getClass().getName() %>: <%= exception.getMessage() %>

Stack Trace:
<% for (StackTraceElement element : exception.getStackTrace()) { %>
    at <%= element.toString() %>
<% } %>
                            </pre>
                                                </details>
                                                <% } %>
                        </div>
                        <div class="card-footer">
                            <div class="d-flex gap-1">
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
            </main>

            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
    </body>

    </html>