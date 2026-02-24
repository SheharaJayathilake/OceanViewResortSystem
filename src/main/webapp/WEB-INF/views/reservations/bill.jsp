<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.service.ReservationBill" %>
        <%@ page import="com.oceanview.model.Reservation" %>
            <!DOCTYPE html>
            <html lang="en">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Bill - Ocean View Resort</title>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                <style>
                    @media print {
                        .no-print {
                            display: none !important;
                        }

                        .card {
                            border: 1px solid #000;
                        }
                    }
                </style>
            </head>

            <body>
                <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                    <main class="main-content">
                        <div class="container">
                            <% ReservationBill bill=(ReservationBill) request.getAttribute("bill"); %>
                                <% if (bill !=null) { %>
                                    <% Reservation reservation=bill.getReservation(); %>

                                        <div class="card">
                                            <div class="card-header text-center">
                                                <h1>OCEAN VIEW RESORT</h1>
                                                <p>Galle, Sri Lanka</p>
                                                <p>Tel: +94 123 456 789</p>
                                            </div>

                                            <div class="card-body">
                                                <h2 class="text-center mb-2">INVOICE</h2>

                                                <div class="form-row">
                                                    <div>
                                                        <p><strong>Reservation Number:</strong>
                                                            <%= reservation.getReservationNumber() %>
                                                        </p>
                                                        <p><strong>Guest Name:</strong>
                                                            <%= reservation.getGuest().getGuestName() %>
                                                        </p>
                                                        <p><strong>Contact:</strong>
                                                            <%= reservation.getGuest().getContactNumber() %>
                                                        </p>
                                                    </div>
                                                    <div class="text-right">
                                                        <p><strong>Date:</strong>
                                                            <%= bill.getCalculatedDate() %>
                                                        </p>
                                                        <p><strong>Status:</strong> <span
                                                                class="badge badge-<%= reservation.getStatus().name().toLowerCase() %>">
                                                                <%= reservation.getStatus() %>
                                                            </span></p>
                                                    </div>
                                                </div>

                                                <table class="table mt-2">
                                                    <thead>
                                                        <tr>
                                                            <th>Description</th>
                                                            <th class="text-right">Quantity</th>
                                                            <th class="text-right">Rate</th>
                                                            <th class="text-right">Amount</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <tr>
                                                            <td>
                                                                <strong>
                                                                    <%= reservation.getRoomType().getTypeName() %> Room
                                                                </strong><br>
                                                                <small>Check-in: <%= reservation.getCheckInDate() %>
                                                                        </small><br>
                                                                <small>Check-out: <%= reservation.getCheckOutDate() %>
                                                                        </small>
                                                            </td>
                                                            <td class="text-right">
                                                                <%= bill.getNumberOfNights() %> nights
                                                            </td>
                                                            <td class="text-right">LKR <%= String.format("%.2f",
                                                                    bill.getRatePerNight()) %>
                                                            </td>
                                                            <td class="text-right">LKR <%= String.format("%.2f",
                                                                    bill.getTotalAmount()) %>
                                                            </td>
                                                        </tr>
                                                    </tbody>
                                                    <tfoot>
                                                        <tr>
                                                            <td colspan="3" class="text-right"><strong>TOTAL
                                                                    AMOUNT:</strong></td>
                                                            <td class="text-right"><strong>LKR <%= String.format("%.2f",
                                                                        bill.getTotalAmount()) %></strong></td>
                                                        </tr>
                                                    </tfoot>
                                                </table>

                                                <div class="mt-2 text-center" style="font-size: 13px;">
                                                    <p>Thank you for choosing Ocean View Resort!</p>
                                                </div>
                                            </div>

                                            <div class="card-footer no-print">
                                                <div class="d-flex justify-between">
                                                    <a href="${pageContext.request.contextPath}/reservations/view/<%= reservation.getReservationNumber() %>"
                                                        class="btn btn-secondary">
                                                        Back to Reservation
                                                    </a>
                                                    <button onclick="window.print()" class="btn btn-primary">
                                                        Print Bill
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                        <% } %>
                        </div>
                    </main>

                    <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
            </body>

            </html>