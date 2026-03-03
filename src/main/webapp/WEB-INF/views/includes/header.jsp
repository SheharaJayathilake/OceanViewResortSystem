<%-- File: src/main/webapp/WEB-INF/includes/header.jsp --%>
    <%@ page import="com.oceanview.model.User" %>
        <% User currentUser=(User) session.getAttribute("currentUser"); %>
            <header class="header">
                <div class="container">
                    <div class="header-content">
                        <a href="${pageContext.request.contextPath}/dashboard" class="logo">
                            OCEAN VIEW RESORT
                        </a>

                        <% if (currentUser !=null) { %>
                            <nav>
                                <ul class="nav">
                                    <li><a href="${pageContext.request.contextPath}/dashboard">Dashboard</a></li>
                                    <li><a href="${pageContext.request.contextPath}/reservations">Reservations</a></li>
                                    <li><a href="${pageContext.request.contextPath}/guests">Guests</a></li>
                                    <li><a href="${pageContext.request.contextPath}/rooms">Rooms</a></li>
                                    <li><a href="${pageContext.request.contextPath}/reports">Reports</a></li>
                                    <% if (currentUser !=null && "ADMIN" .equals(currentUser.getRole().name())) { %>
                                        <li><a href="${pageContext.request.contextPath}/users">Users</a></li>
                                        <% } %>
                                            <li><a href="${pageContext.request.contextPath}/help">Help</a></li>
                                </ul>
                            </nav>

                            <div class="user-info">
                                <span>
                                    <%= currentUser.getFullName() %> (<%= currentUser.getRole() %>)
                                </span>
                                <a href="${pageContext.request.contextPath}/logout" class="btn btn-sm btn-secondary">
                                    Logout
                                </a>
                            </div>
                            <% } %>
                    </div>
                </div>
            </header>