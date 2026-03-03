<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="en">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Help &amp; Documentation - Ocean View Resort</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <style>
            .help-section {
                margin-bottom: 2rem;
                padding-bottom: 2rem;
                border-bottom: 1px solid var(--color-gray-300);
            }

            .help-section:last-child {
                border-bottom: none;
            }

            .help-section h2 {
                margin-bottom: 1rem;
                padding-bottom: 0.5rem;
                border-bottom: 2px solid var(--color-black);
            }

            .step-list {
                counter-reset: step-counter;
                list-style: none;
                padding-left: 0;
            }

            .step-list li {
                counter-increment: step-counter;
                margin-bottom: 1rem;
                padding-left: 3rem;
                position: relative;
            }

            .step-list li::before {
                content: counter(step-counter);
                position: absolute;
                left: 0;
                top: 0;
                width: 2rem;
                height: 2rem;
                background: var(--color-black);
                color: var(--color-white);
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: bold;
            }
        </style>
    </head>

    <body>
        <%@ include file="/WEB-INF/views/includes/header.jsp" %>

            <main class="main-content">
                <div class="container">
                    <div class="page-header">
                        <h1 class="page-title">HELP &amp; DOCUMENTATION</h1>
                    </div>

                    <div class="card">
                        <div class="card-header">
                            <h2>Welcome to Ocean View Resort Reservation System</h2>
                        </div>
                        <div class="card-body">
                            <p>This comprehensive guide will help you navigate and use all features of the reservation
                                management system effectively.</p>
                        </div>
                    </div>

                    <!-- Getting Started -->
                    <div class="help-section">
                        <h2>Getting Started</h2>

                        <h3>System Login</h3>
                        <ol class="step-list">
                            <li>Navigate to the login page at <code>/login</code></li>
                            <li>Enter your username and password</li>
                            <li>Click the "LOGIN" button</li>
                            <li>You will be redirected to the dashboard</li>
                        </ol>

                        <div class="alert alert-info">
                            <strong>Default Credentials:</strong><br>
                            Username: <code>admin</code><br>
                            Password: <code>123456</code>
                        </div>

                        <h3>Dashboard Overview</h3>
                        <p>The dashboard displays key statistics including:</p>
                        <ul>
                            <li>Total number of reservations</li>
                            <li>Active reservations (Confirmed and Checked-in)</li>
                            <li>Total registered guests</li>
                            <li>Completed reservations</li>
                        </ul>
                    </div>

                    <!-- Managing Guests -->
                    <div class="help-section">
                        <h2>Managing Guests</h2>

                        <h3>Registering a New Guest</h3>
                        <ol class="step-list">
                            <li>Click "Guests" in the main navigation</li>
                            <li>Click "+ Register New Guest" button</li>
                            <li>Fill in all required fields (marked with *):
                                <ul>
                                    <li><strong>Full Name:</strong> Guest's complete name</li>
                                    <li><strong>Contact Number:</strong> 10-15 digit phone number</li>
                                    <li><strong>Address:</strong> Complete address</li>
                                </ul>
                            </li>
                            <li>Optional fields:
                                <ul>
                                    <li><strong>Email Address:</strong> Guest's email</li>
                                    <li><strong>Identification Type:</strong> NIC, Passport, or Driving License</li>
                                    <li><strong>Identification Number:</strong> ID number</li>
                                </ul>
                            </li>
                            <li>Click "Register Guest" to save</li>
                        </ol>

                        <h3>Viewing Guest Details</h3>
                        <ol class="step-list">
                            <li>Navigate to Guests section</li>
                            <li>Click "View" button next to any guest</li>
                            <li>Review complete guest information</li>
                        </ol>

                        <h3>Editing Guest Information</h3>
                        <ol class="step-list">
                            <li>Navigate to guest details page</li>
                            <li>Click "Edit Details" button</li>
                            <li>Update the required fields</li>
                            <li>Click "Update Guest" to save changes</li>
                        </ol>

                        <h3>Searching Guests</h3>
                        <ol class="step-list">
                            <li>Go to Guests section</li>
                            <li>Use the search box to enter guest name or contact number</li>
                            <li>Click "Search" button</li>
                            <li>Results will display matching guests</li>
                        </ol>
                    </div>

                    <!-- Managing Reservations -->
                    <div class="help-section">
                        <h2>Managing Reservations</h2>

                        <h3>Creating a New Reservation</h3>
                        <ol class="step-list">
                            <li>Click "Reservations" → "New Reservation"</li>
                            <li>Select a guest from the dropdown (or register a new guest first)</li>
                            <li>Choose the room type:
                                <ul>
                                    <li><strong>Standard:</strong> LKR 150.00 per night</li>
                                    <li><strong>Deluxe:</strong> LKR 250.00 per night</li>
                                    <li><strong>Suite:</strong> LKR 450.00 per night</li>
                                </ul>
                            </li>
                            <li>Enter number of guests</li>
                            <li>Select check-in and check-out dates (check-in must be today or future date)</li>
                            <li>Add any special requests (optional)</li>
                            <li>Click "Create Reservation"</li>
                            <li>A unique reservation number will be generated</li>
                        </ol>

                        <h3>Reservation Status Workflow</h3>
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Status</th>
                                    <th>Description</th>
                                    <th>Available Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><span class="badge badge-pending">PENDING</span></td>
                                    <td>Reservation created but not confirmed</td>
                                    <td>Confirm, Cancel</td>
                                </tr>
                                <tr>
                                    <td><span class="badge badge-confirmed">CONFIRMED</span></td>
                                    <td>Reservation confirmed by staff</td>
                                    <td>Check-in, Cancel</td>
                                </tr>
                                <tr>
                                    <td><span class="badge badge-checked-in">CHECKED IN</span></td>
                                    <td>Guest has checked in</td>
                                    <td>Check-out</td>
                                </tr>
                                <tr>
                                    <td><span class="badge badge-checked-out">CHECKED OUT</span></td>
                                    <td>Guest has checked out (final)</td>
                                    <td>None</td>
                                </tr>
                                <tr>
                                    <td><span class="badge badge-cancelled">CANCELLED</span></td>
                                    <td>Reservation cancelled (final)</td>
                                    <td>None</td>
                                </tr>
                            </tbody>
                        </table>

                        <h3>Viewing Reservation Details</h3>
                        <ol class="step-list">
                            <li>Navigate to Reservations section</li>
                            <li>Click "View" on any reservation</li>
                            <li>Review complete reservation information including:
                                <ul>
                                    <li>Guest details</li>
                                    <li>Room type and rate</li>
                                    <li>Check-in/Check-out dates</li>
                                    <li>Total amount</li>
                                    <li>Special requests</li>
                                </ul>
                            </li>
                        </ol>

                        <h3>Confirming a Reservation</h3>
                        <ol class="step-list">
                            <li>Open reservation details page</li>
                            <li>Verify all information is correct</li>
                            <li>Click "Confirm Reservation" button</li>
                            <li>Status will change to CONFIRMED</li>
                        </ol>

                        <h3>Checking In a Guest</h3>
                        <ol class="step-list">
                            <li>Open confirmed reservation on check-in date</li>
                            <li>Click "Check-in Guest" button</li>
                            <li>Guest is now checked in</li>
                        </ol>

                        <h3>Checking Out a Guest</h3>
                        <ol class="step-list">
                            <li>Ensure payment is marked as PAID</li>
                            <li>Open reservation details</li>
                            <li>Click "Check-out Guest" button</li>
                            <li>Reservation is now complete</li>
                        </ol>
                    </div>

                    <!-- Bill Generation -->
                    <div class="help-section">
                        <h2>Bill Generation</h2>

                        <h3>Generating a Bill</h3>
                        <ol class="step-list">
                            <li>Open reservation details page</li>
                            <li>Click "View Bill" button</li>
                            <li>Bill displays:
                                <ul>
                                    <li>Guest and reservation information</li>
                                    <li>Room type and rate per night</li>
                                    <li>Number of nights</li>
                                    <li>Total amount calculation</li>
                                </ul>
                            </li>
                            <li>Click "Print Bill" to print or save as PDF</li>
                        </ol>

                        <h3>Understanding Bill Calculation</h3>
                        <p><strong>Formula:</strong> Total Amount = Rate per Night × Number of Nights</p>
                        <p><strong>Example:</strong></p>
                        <ul>
                            <li>Room Type: Deluxe (LKR 250.00/night)</li>
                            <li>Check-in: March 1, 2026</li>
                            <li>Check-out: March 4, 2026</li>
                            <li>Number of Nights: 3</li>
                            <li><strong>Total: LKR 750.00</strong></li>
                        </ul>
                    </div>

                    <!-- Reports -->
                    <div class="help-section">
                        <h2>Reports &amp; Analytics</h2>

                        <h3>Available Reports</h3>
                        <ul>
                            <li><strong>System Statistics:</strong> Overview of all system metrics</li>
                            <li><strong>Reservations by Status:</strong> Grouped view of reservations by their status
                            </li>
                            <li><strong>Guest Analytics:</strong> Guest information and history</li>
                        </ul>

                        <h3>Generating Reports</h3>
                        <ol class="step-list">
                            <li>Click "Reports" in main navigation</li>
                            <li>Select desired report type</li>
                            <li>View or print the generated report</li>
                        </ol>
                    </div>

                    <!-- Troubleshooting -->
                    <div class="help-section">
                        <h2>Troubleshooting</h2>

                        <h3>Common Issues and Solutions</h3>

                        <div style="margin-bottom: 1.5rem;">
                            <h4>Cannot Login</h4>
                            <ul>
                                <li>Verify username and password are correct</li>
                                <li>Check if account is active</li>
                                <li>Contact system administrator if issue persists</li>
                            </ul>
                        </div>

                        <div style="margin-bottom: 1.5rem;">
                            <h4>Cannot Create Reservation</h4>
                            <ul>
                                <li>Ensure all required fields are filled</li>
                                <li>Check that check-in date is not in the past</li>
                                <li>Verify check-out date is after check-in date</li>
                                <li>Confirm room type is available for selected dates</li>
                            </ul>
                        </div>

                        <div style="margin-bottom: 1.5rem;">
                            <h4>Error Messages</h4>
                            <ul>
                                <li><strong>"Invalid date range":</strong> Check-out must be after check-in</li>
                                <li><strong>"Room not available":</strong> Selected room type is booked for those dates
                                </li>
                                <li><strong>"Guest not found":</strong> Register guest first before creating reservation
                                </li>
                                <li><strong>"Cannot check-out":</strong> Ensure payment is marked as PAID</li>
                            </ul>
                        </div>
                    </div>

                    <!-- System Information -->
                    <div class="help-section">
                        <h2>System Information</h2>

                        <h3>User Roles</h3>
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Role</th>
                                    <th>Permissions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td><strong>ADMIN</strong></td>
                                    <td>Full system access including user management</td>
                                </tr>
                                <tr>
                                    <td><strong>MANAGER</strong></td>
                                    <td>Manage reservations, guests, and view reports</td>
                                </tr>
                                <tr>
                                    <td><strong>RECEPTIONIST</strong></td>
                                    <td>Create and manage reservations and guests</td>
                                </tr>
                            </tbody>
                        </table>

                        <h3>System Features</h3>
                        <ul>
                            <li>✓ User authentication and authorization</li>
                            <li>✓ Guest registration and management</li>
                            <li>✓ Reservation creation and tracking</li>
                            <li>✓ Multiple reservation statuses</li>
                            <li>✓ Automated bill calculation</li>
                            <li>✓ Room availability checking</li>
                            <li>✓ Search functionality</li>
                            <li>✓ Comprehensive reports</li>
                            <li>✓ Print capabilities</li>
                            <li>✓ Audit trail logging</li>
                        </ul>

                        <h3>Technical Specifications</h3>
                        <ul>
                            <li><strong>Backend:</strong> Java EE (Servlets, JSP)</li>
                            <li><strong>Database:</strong> MySQL with stored procedures and triggers</li>
                            <li><strong>Server:</strong> Apache Tomcat 9</li>
                            <li><strong>Design Patterns:</strong> MVC, DAO, Singleton, Factory, Front Controller</li>
                            <li><strong>Architecture:</strong> 3-Tier (Presentation, Business, Data)</li>
                        </ul>
                    </div>

                    <!-- Contact Support -->
                    <div class="help-section" style="border-bottom: none;">
                        <h2>Need More Help?</h2>
                        <p>If you couldn't find the answer to your question in this documentation:</p>
                        <ul>
                            <li>Contact your system administrator</li>
                            <li>Email: support@oceanviewresort.com</li>
                            <li>Phone: +94 123 456 789</li>
                        </ul>

                        <div class="mt-2">
                            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary">
                                Return to Dashboard
                            </a>
                        </div>
                    </div>
                </div>
            </main>

            <%@ include file="/WEB-INF/views/includes/footer.jsp" %>
    </body>

    </html>