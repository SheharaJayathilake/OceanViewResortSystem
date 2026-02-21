package com.oceanview.model;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Guest entity with comprehensive validation
 * Implements data integrity checks
 */
public class Guest extends BaseModel {
    private static final long serialVersionUID = 1L;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");

    private Integer guestId;
    private String guestName;
    private String address;
    private String contactNumber;
    private String email;
    private String identificationType;
    private String identificationNumber;

    public Guest() {
        super();
    }

    public Guest(String guestName, String address, String contactNumber) {
        this();
        this.guestName = guestName;
        this.address = address;
        this.contactNumber = contactNumber;
    }

    public Integer getGuestId() {
        return guestId;
    }

    public void setGuestId(Integer guestId) {
        this.guestId = guestId;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        if (guestName == null || guestName.trim().length() < 2) {
            throw new IllegalArgumentException("Guest name must be at least 2 characters");
        }
        this.guestName = guestName.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address != null ? address.trim() : null;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        if (contactNumber == null || !PHONE_PATTERN.matcher(contactNumber).matches()) {
            throw new IllegalArgumentException("Invalid contact number format");
        }
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public void setIdentificationNumber(String identificationNumber) {
        this.identificationNumber = identificationNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Guest))
            return false;
        Guest guest = (Guest) o;
        return Objects.equals(guestId, guest.guestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guestId);
    }

    @Override
    public String toString() {
        return String.format("Guest[id=%d, name=%s, contact=%s]",
                guestId, guestName, contactNumber);
    }
}
