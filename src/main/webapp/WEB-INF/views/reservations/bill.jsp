<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.service.ReservationBill" %>
        <%@ page import="com.oceanview.model.Reservation" %>
            <%@ page import="com.oceanview.model.Guest" %>
                <%@ page import="com.oceanview.model.RoomType" %>
                    <!DOCTYPE html>
                    <html lang="en">

                    <head>
                        <meta charset="UTF-8">
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <% ReservationBill bill=(ReservationBill) request.getAttribute("bill"); %>
                            <% String resNum="" ; %>
                                <% if (bill !=null && bill.getReservation() !=null) {
                                    resNum=bill.getReservation().getReservationNumber(); } %>
                                    <title>
                                        <%= resNum %> - Invoice
                                    </title>
                                    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                                    <!-- PDF generation libraries (frontend JS — allowed per assignment rules) -->
                                    <script
                                        src="https://cdnjs.cloudflare.com/ajax/libs/html2canvas/1.4.1/html2canvas.min.js"></script>
                                    <script
                                        src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js"></script>
                                    <style>
                                        /* ---------- Invoice Layout ---------- */
                                        .invoice-wrapper {
                                            max-width: 210mm;
                                            margin: 30px auto;
                                            padding: 0 20px;
                                        }

                                        .invoice-page {
                                            background: #fff;
                                            padding: 40px 45px;
                                            border: 1px solid #ddd;
                                            font-family: 'Segoe UI', Arial, sans-serif;
                                            color: #222;
                                            font-size: 13px;
                                            line-height: 1.5;
                                        }

                                        .inv-header {
                                            text-align: center;
                                            border-bottom: 2px solid #222;
                                            padding-bottom: 18px;
                                            margin-bottom: 25px;
                                        }

                                        .inv-header h1 {
                                            margin: 0;
                                            font-size: 26px;
                                            letter-spacing: 0.15em;
                                            font-weight: 700;
                                        }

                                        .inv-header p {
                                            margin: 2px 0;
                                            font-size: 12px;
                                            color: #555;
                                        }

                                        .inv-header .inv-title {
                                            margin-top: 14px;
                                            font-size: 18px;
                                            letter-spacing: 0.2em;
                                            font-weight: 600;
                                        }

                                        .inv-info {
                                            display: flex;
                                            justify-content: space-between;
                                            margin-bottom: 25px;
                                        }

                                        .inv-info p {
                                            margin: 3px 0;
                                            font-size: 13px;
                                        }

                                        .inv-info strong {
                                            display: inline-block;
                                            min-width: 140px;
                                        }

                                        .inv-info .right-col {
                                            text-align: right;
                                        }

                                        .inv-info .right-col strong {
                                            min-width: auto;
                                            margin-right: 6px;
                                        }

                                        .inv-table {
                                            width: 100%;
                                            border-collapse: collapse;
                                            margin-bottom: 20px;
                                        }

                                        .inv-table th {
                                            background: #222;
                                            color: #fff;
                                            padding: 9px 12px;
                                            font-size: 12px;
                                            letter-spacing: 0.05em;
                                            text-transform: uppercase;
                                            text-align: left;
                                        }

                                        .inv-table th.num {
                                            text-align: right;
                                        }

                                        .inv-table td {
                                            padding: 10px 12px;
                                            border-bottom: 1px solid #e0e0e0;
                                            vertical-align: top;
                                        }

                                        .inv-table td.num {
                                            text-align: right;
                                            font-variant-numeric: tabular-nums;
                                        }

                                        .inv-table td small {
                                            color: #666;
                                            font-size: 11px;
                                        }

                                        .inv-totals {
                                            display: flex;
                                            flex-direction: column;
                                            align-items: flex-end;
                                            margin-bottom: 30px;
                                        }

                                        .inv-totals .total-row {
                                            display: flex;
                                            justify-content: space-between;
                                            width: 260px;
                                            padding: 5px 0;
                                            font-size: 13px;
                                        }

                                        .inv-totals .total-row.grand {
                                            border-top: 2px solid #222;
                                            margin-top: 4px;
                                            padding-top: 8px;
                                            font-size: 16px;
                                            font-weight: 700;
                                        }

                                        .pay-badge {
                                            display: inline-block;
                                            padding: 3px 12px;
                                            border-radius: 3px;
                                            font-size: 11px;
                                            font-weight: 700;
                                            letter-spacing: 0.05em;
                                        }

                                        .pay-badge.paid {
                                            background: #d4edda;
                                            color: #155724;
                                        }

                                        .pay-badge.pending {
                                            background: #fff3cd;
                                            color: #856404;
                                        }

                                        .pay-badge.partial {
                                            background: #cce5ff;
                                            color: #004085;
                                        }

                                        .inv-footer {
                                            text-align: center;
                                            border-top: 1px solid #ccc;
                                            padding-top: 15px;
                                            font-size: 11px;
                                            color: #888;
                                        }

                                        .inv-footer p {
                                            margin: 2px 0;
                                        }

                                        /* Action bar */
                                        .action-bar {
                                            max-width: 210mm;
                                            margin: 0 auto 20px auto;
                                            padding: 0 20px;
                                            display: flex;
                                            justify-content: space-between;
                                            align-items: center;
                                        }

                                        .action-bar .download-hint {
                                            font-size: 12px;
                                            color: #888;
                                        }

                                        /* Download button pulse */
                                        .btn-download {
                                            background-color: #28a745;
                                            color: #fff;
                                            border: none;
                                            padding: 10px 22px;
                                            font-size: 14px;
                                            cursor: pointer;
                                            border-radius: 4px;
                                            letter-spacing: 0.03em;
                                            transition: opacity 0.2s;
                                        }

                                        .btn-download:hover {
                                            opacity: 0.85;
                                        }

                                        .btn-download:disabled {
                                            opacity: 0.5;
                                            cursor: wait;
                                        }

                                        /* Hide during PDF capture */
                                        @media print {

                                            .no-print,
                                            .action-bar,
                                            header,
                                            nav,
                                            footer {
                                                display: none !important;
                                            }
                                        }
                                    </style>
                    </head>

                    <body>
                        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

                            <main class="main-content">

                                <% if (bill !=null) { %>
                                    <% Reservation reservation=bill.getReservation(); %>
                                        <% Guest guest=reservation.getGuest(); %>
                                            <% RoomType roomType=reservation.getRoomType(); %>

                                                <!-- Action bar -->
                                                <div class="action-bar no-print">
                                                    <a href="${pageContext.request.contextPath}/reservations/view/<%= reservation.getReservationNumber() %>"
                                                        class="btn btn-secondary">&#8592; Back to Reservation</a>
                                                    <div>
                                                        <button id="downloadBtn" onclick="downloadPDF()"
                                                            class="btn-download">
                                                            &#11015; Download Bill as PDF
                                                        </button>
                                                        <div class="download-hint"
                                                            style="margin-top:4px; text-align:right;">
                                                            Saves as <strong>
                                                                <%= resNum %>.pdf
                                                            </strong>
                                                        </div>
                                                    </div>
                                                </div>

                                                <div class="invoice-wrapper">
                                                    <div class="invoice-page" id="invoiceContent">

                                                        <!-- Hotel Header -->
                                                        <div class="inv-header">
                                                            <h1>OCEAN VIEW RESORT</h1>
                                                            <p>Beachside Road, Galle 80000, Sri Lanka</p>
                                                            <p>Tel: +94 91 234 5678 &nbsp;|&nbsp; Email:
                                                                info@oceanviewresort.lk</p>
                                                            <div class="inv-title">INVOICE</div>
                                                        </div>

                                                        <!-- Guest & Reservation Info -->
                                                        <div class="inv-info">
                                                            <div>
                                                                <p><strong>Invoice No:</strong>
                                                                    <%= reservation.getReservationNumber() %>
                                                                </p>
                                                                <p><strong>Guest Name:</strong>
                                                                    <%= guest.getGuestName() %>
                                                                </p>
                                                                <p><strong>Contact:</strong>
                                                                    <%= guest.getContactNumber() %>
                                                                </p>
                                                                <% if (guest.getEmail() !=null &&
                                                                    !guest.getEmail().isEmpty()) { %>
                                                                    <p><strong>Email:</strong>
                                                                        <%= guest.getEmail() %>
                                                                    </p>
                                                                    <% } %>
                                                            </div>
                                                            <div class="right-col">
                                                                <p><strong>Date:</strong>
                                                                    <%= bill.getCalculatedDate() %>
                                                                </p>
                                                                <p><strong>Status:</strong>
                                                                    <span
                                                                        class="pay-badge <%= reservation.getPaymentStatus().name().toLowerCase() %>">
                                                                        <%= reservation.getPaymentStatus() %>
                                                                    </span>
                                                                </p>
                                                                <p><strong>Booking:</strong>
                                                                    <%= reservation.getStatus() %>
                                                                </p>
                                                            </div>
                                                        </div>

                                                        <!-- Line Items Table -->
                                                        <table class="inv-table">
                                                            <thead>
                                                                <tr>
                                                                    <th style="width:44%;">Description</th>
                                                                    <th class="num" style="width:18%;">Nights</th>
                                                                    <th class="num" style="width:19%;">Rate (LKR)</th>
                                                                    <th class="num" style="width:19%;">Amount (LKR)</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <tr>
                                                                    <td>
                                                                        <strong>
                                                                            <%= roomType.getTypeName() %> Room
                                                                                <% if (reservation.getRoomNumber()
                                                                                    !=null) { %>
                                                                                    (<%= reservation.getRoomNumber() %>)
                                                                                        <% } %>
                                                                        </strong><br>
                                                                        <small>Check-in: <%=
                                                                                reservation.getCheckInDate() %>
                                                                        </small><br>
                                                                        <small>Check-out: <%=
                                                                                reservation.getCheckOutDate() %></small>
                                                                    </td>
                                                                    <td class="num">
                                                                        <%= bill.getNumberOfNights() %>
                                                                    </td>
                                                                    <td class="num">
                                                                        <%= String.format("%,.2f",
                                                                            bill.getRatePerNight()) %>
                                                                    </td>
                                                                    <td class="num">
                                                                        <%= String.format("%,.2f",
                                                                            bill.getTotalAmount()) %>
                                                                    </td>
                                                                </tr>
                                                            </tbody>
                                                        </table>

                                                        <!-- Totals -->
                                                        <div class="inv-totals">
                                                            <div class="total-row">
                                                                <span>Subtotal:</span>
                                                                <span>LKR <%= String.format("%,.2f",
                                                                        bill.getTotalAmount()) %></span>
                                                            </div>
                                                            <div class="total-row">
                                                                <span>Tax (0%):</span>
                                                                <span>LKR 0.00</span>
                                                            </div>
                                                            <div class="total-row grand">
                                                                <span>TOTAL:</span>
                                                                <span>LKR <%= String.format("%,.2f",
                                                                        bill.getTotalAmount()) %></span>
                                                            </div>
                                                        </div>

                                                        <!-- Footer -->
                                                        <div class="inv-footer">
                                                            <p>Thank you for choosing Ocean View Resort!</p>
                                                            <p>This is a computer-generated invoice and does not require
                                                                a signature.</p>
                                                        </div>

                                                    </div>
                                                </div>

                                                <% } else { %>
                                                    <div class="container">
                                                        <div class="alert alert-error">Bill data not available.</div>
                                                    </div>
                                                    <% } %>

                            </main>

                            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>

                                <script>
                                    /**
                                     * Captures the invoice element as a high-resolution canvas image,
                                     * converts it to a single-page A4 PDF, and triggers an automatic
                                     * download named with the reservation number.
                                     */
                                    function downloadPDF() {
                                        var btn = document.getElementById('downloadBtn');
                                        btn.disabled = true;
                                        btn.textContent = 'Generating PDF...';

                                        var invoice = document.getElementById('invoiceContent');
                                        var fileName = '<%= resNum %>' + '.pdf';

                                        // A4 dimensions in mm
                                        var a4Width = 210;
                                        var a4Height = 297;

                                        html2canvas(invoice, {
                                            scale: 2,
                                            useCORS: true,
                                            backgroundColor: '#ffffff',
                                            logging: false
                                        }).then(function (canvas) {
                                            var jsPDF = window.jspdf.jsPDF;
                                            var pdf = new jsPDF('p', 'mm', 'a4');

                                            var imgData = canvas.toDataURL('image/png');
                                            var canvasWidth = canvas.width;
                                            var canvasHeight = canvas.height;

                                            // Scale image to fit A4 width with proportional height
                                            var pdfWidth = a4Width - 20;   // 10mm margin each side
                                            var pdfHeight = (canvasHeight * pdfWidth) / canvasWidth;

                                            // If content exceeds one page, scale down to fit
                                            var maxHeight = a4Height - 20;  // 10mm margin top/bottom
                                            if (pdfHeight > maxHeight) {
                                                var ratio = maxHeight / pdfHeight;
                                                pdfWidth = pdfWidth * ratio;
                                                pdfHeight = maxHeight;
                                            }

                                            // Centre horizontally
                                            var xOffset = (a4Width - pdfWidth) / 2;

                                            pdf.addImage(imgData, 'PNG', xOffset, 10, pdfWidth, pdfHeight);
                                            pdf.save(fileName);

                                            btn.disabled = false;
                                            btn.innerHTML = '&#11015; Download Bill as PDF';
                                        }).catch(function (err) {
                                            alert('PDF generation failed: ' + err.message);
                                            btn.disabled = false;
                                            btn.innerHTML = '&#11015; Download Bill as PDF';
                                        });
                                    }
                                </script>
                    </body>

                    </html>