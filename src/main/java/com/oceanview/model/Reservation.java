package com.oceanview.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Reservation entity with business logic for booking management
 * Implements complex validation and calculation logic
 */
public class Reservation extends BaseModel {
    private static final long serialVersionUID = 1L;

    private Integer reservationId;
    private String reservationNumber;
    private Integer guestId;
    private Integer roomTypeId;
    private Integer roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer numberOfGuests;
    private ReservationStatus status;
    private PaymentStatus paymentStatus;
    private BigDecimal totalAmount;
    private String specialRequests;
    private Integer createdBy;

    private Guest guest;
    private RoomType roomType;
    private Room room;

    public enum ReservationStatus {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    public enum PaymentStatus {
        PENDING, PARTIAL, PAID, REFUNDED
    }

    public Reservation() {
        super();
        this.status = ReservationStatus.PENDING;
        this.paymentStatus = PaymentStatus.PENDING;
        this.numberOfGuests = 1;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getReservationNumber() {
        return reservationNumber;
    }

    public void setReservationNumber(String reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getRoomNumber() {
        if (room != null) {
            return room.getRoomNumber();
        }
        return null;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        if (checkInDate == null) {
            throw new IllegalArgumentException("Check-in date cannot be null");
        }
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        if (checkOutDate == null) {
            throw new IllegalArgumentException("Check-out date cannot be null");
        }
        if (this.checkInDate != null && checkOutDate.isBefore(this.checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
        this.checkOutDate = checkOutDate;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(Integer numberOfGuests) {
        if (numberOfGuests == null || numberOfGuests < 1) {
            throw new IllegalArgumentException("Number of guests must be at least 1");
        }
        this.numberOfGuests = numberOfGuests;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status != null ? status : ReservationStatus.PENDING;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    public BigDecimal calculateTotal(BigDecimal ratePerNight) {
        long nights = getNumberOfNights();
        if (nights > 0 && ratePerNight != null) {
            return ratePerNight.multiply(BigDecimal.valueOf(nights));
        }
        return BigDecimal.ZERO;
    }

    public boolean isModifiable() {
        return status == ReservationStatus.PENDING || status == ReservationStatus.CONFIRMED;
    }

    public boolean canCheckIn() {
        return status == ReservationStatus.CONFIRMED &&
                LocalDate.now().equals(checkInDate);
    }

    public boolean canCheckOut() {
        return status == ReservationStatus.CHECKED_IN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Reservation))
            return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId) &&
                Objects.equals(reservationNumber, that.reservationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, reservationNumber);
    }

    @Override
    public String toString() {
        return String.format("Reservation[id=%d, number=%s, guest=%d, status=%s]",
                reservationId, reservationNumber, guestId, status);
    }
}
