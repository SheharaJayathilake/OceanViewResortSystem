<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="com.oceanview.model.Room" %>
        <%@ page import="com.oceanview.model.RoomType" %>
            <%@ page import="java.util.List" %>
                <!DOCTYPE html>
                <html lang="en">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <% Room room=(Room) request.getAttribute("room"); boolean isEdit=(room !=null); String formAction;
                        String pageTitle; String cardTitle; String submitLabel; String roomNumVal; String floorVal;
                        String checkedAttr; if (isEdit) { formAction=request.getContextPath() + "/rooms/room/update/" +
                        room.getRoomId(); pageTitle="Edit Room" ; cardTitle="Edit Room Details" ;
                        submitLabel="Update Room" ; roomNumVal=room.getRoomNumber();
                        floorVal=String.valueOf(room.getFloor()); checkedAttr=room.isActive() ? "checked" : "" ; } else
                        { formAction=request.getContextPath() + "/rooms/room/create" ; pageTitle="Add Room" ;
                        cardTitle="New Room Details" ; submitLabel="Create Room" ; roomNumVal="" ; floorVal="1" ;
                        checkedAttr="checked" ; } @SuppressWarnings("unchecked") List<RoomType> roomTypes = (List
                        <RoomType>) request.getAttribute("roomTypes");
                            %>
                            <title>
                                <%= pageTitle %> - Ocean View Resort
                            </title>
                            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
                </head>

                <body>
                    <%@ include file="/WEB-INF/views/includes/header.jsp" %>
                        <main class="main-content">
                            <div class="container">
                                <div class="page-header">
                                    <h1 class="page-title">
                                        <%= pageTitle.toUpperCase() %>
                                    </h1>
                                </div>
                                <form action="<%= formAction %>" method="post">
                                    <div class="card">
                                        <div class="card-header">
                                            <%= cardTitle %>
                                        </div>
                                        <div class="card-body">
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="roomNumber" class="form-label">Room Number</label>
                                                    <input type="text" id="roomNumber" name="roomNumber"
                                                        class="form-control" value="<%= roomNumVal %>"
                                                        placeholder="e.g. 101" required maxlength="10">
                                                </div>
                                                <div class="form-group">
                                                    <label for="roomTypeId" class="form-label">Room Type</label>
                                                    <select id="roomTypeId" name="roomTypeId" class="form-control"
                                                        required>
                                                        <option value="">-- Select Room Type --</option>
                                                        <% if (roomTypes !=null) { for (RoomType rt : roomTypes) { int
                                                            rtId=rt.getRoomTypeId(); String rtName=rt.getTypeName();
                                                            String rtRate=String.format("%,.2f", rt.getRatePerNight());
                                                            int rtCap=rt.getCapacity(); String sel="" ; if (isEdit &&
                                                            room.getRoomTypeId() !=null &&
                                                            room.getRoomTypeId().equals(rtId)) { sel="selected" ; } %>
                                                            <option value="<%= rtId %>" <%=sel %>><%= rtName %> (LKR <%=
                                                                        rtRate %>, <%= rtCap %> guests)</option>
                                                            <% } } %>
                                                    </select>
                                                </div>
                                            </div>
                                            <div class="form-row">
                                                <div class="form-group">
                                                    <label for="floor" class="form-label">Floor</label>
                                                    <input type="number" id="floor" name="floor" class="form-control"
                                                        value="<%= floorVal %>" min="0" max="100" required>
                                                </div>
                                                <div class="form-group">
                                                    <label class="form-label">Status</label>
                                                    <div style="padding-top:8px;">
                                                        <label
                                                            style="display:flex;align-items:center;gap:8px;cursor:pointer;">
                                                            <input type="checkbox" name="isActive" <%=checkedAttr %>>
                                                            <span>Active</span>
                                                        </label>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="card-footer">
                                            <div class="d-flex justify-between">
                                                <a href="${pageContext.request.contextPath}/rooms"
                                                    class="btn btn-secondary">Cancel</a>
                                                <button type="submit" class="btn btn-primary">
                                                    <%= submitLabel %>
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