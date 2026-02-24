<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register Guest - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>

    <body>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

            <main class="main-content">
                <div class="container">
                    <div class="page-header">
                        <h1 class="page-title">REGISTER NEW GUEST</h1>
                    </div>

                    <% String errorMessage=(String) request.getAttribute("errorMessage"); %>
                        <% if (errorMessage !=null) { %>
                            <div class="alert alert-error">
                                <%= errorMessage %>
                            </div>
                            <% } %>

                                <form action="${pageContext.request.contextPath}/guests/create" method="post">
                                    <div class="card">
                                        <div class="card-header">Guest Information</div>
                                        <div class="card-body">
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="guestName" class="form-label">Full Name *</label>
                                                    <input type="text" id="guestName" name="guestName"
                                                        class="form-control" required autofocus
                                                        placeholder="Enter full name">
                                                </div>

                                                <div class="form-group">
                                                    <label for="contactNumber" class="form-label">Contact Number
                                                        *</label>
                                                    <input type="tel" id="contactNumber" name="contactNumber"
                                                        class="form-control" required pattern="[0-9+]{10,15}"
                                                        placeholder="e.g., 0771234567">
                                                </div>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="email" class="form-label">Email Address</label>
                                                    <input type="email" id="email" name="email" class="form-control"
                                                        placeholder="guest@example.com">
                                                </div>

                                                <div class="form-group">
                                                    <label for="identificationType" class="form-label">Identification
                                                        Type</label>
                                                    <select id="identificationType" name="identificationType"
                                                        class="form-control">
                                                        <option value="">-- Select Type --</option>
                                                        <option value="NIC">National ID Card (NIC)</option>
                                                        <option value="PASSPORT">Passport</option>
                                                        <option value="DRIVING_LICENSE">Driving License</option>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="identificationNumber" class="form-label">Identification
                                                        Number</label>
                                                    <input type="text" id="identificationNumber"
                                                        name="identificationNumber" class="form-control"
                                                        placeholder="Enter ID number">
                                                </div>
                                            </div>

                                            <div class="form-group">
                                                <label for="address" class="form-label">Address *</label>
                                                <textarea id="address" name="address" class="form-control" required
                                                    placeholder="Enter complete address"></textarea>
                                            </div>
                                        </div>

                                        <div class="card-footer">
                                            <div class="d-flex justify-between">
                                                <a href="${pageContext.request.contextPath}/guests"
                                                    class="btn btn-secondary">
                                                    Cancel
                                                </a>
                                                <button type="submit" class="btn btn-primary">
                                                    Register Guest
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                </div>
            </main>

            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
    </body>

    </html>