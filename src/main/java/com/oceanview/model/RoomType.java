package com.oceanview.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * RoomType entity with pricing and capacity management
 */
public class RoomType extends BaseModel {
    private static final long serialVersionUID = 1L;

    private Integer roomTypeId;
    private String typeName;
    private BigDecimal ratePerNight;
    private Integer capacity;
    private String description;
    private List<String> amenities;
    private boolean isAvailable;

    public RoomType() {
        super();
        this.amenities = new ArrayList<>();
        this.isAvailable = true;
    }

    public RoomType(String typeName, BigDecimal ratePerNight, Integer capacity) {
        this();
        this.typeName = typeName;
        this.ratePerNight = ratePerNight;
        this.capacity = capacity;
    }

    public Integer getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Integer roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        if (typeName == null || typeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Type name cannot be empty");
        }
        this.typeName = typeName.trim();
    }

    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    public void setRatePerNight(BigDecimal ratePerNight) {
        if (ratePerNight == null || ratePerNight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Rate must be greater than zero");
        }
        this.ratePerNight = ratePerNight;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        if (capacity == null || capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getAmenities() {
        return new ArrayList<>(amenities);
    }

    public void setAmenities(List<String> amenities) {
        this.amenities = amenities != null ? new ArrayList<>(amenities) : new ArrayList<>();
    }

    public void addAmenity(String amenity) {
        if (amenity != null && !amenity.trim().isEmpty()) {
            this.amenities.add(amenity.trim());
        }
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof RoomType))
            return false;
        RoomType roomType = (RoomType) o;
        return Objects.equals(roomTypeId, roomType.roomTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomTypeId);
    }

    @Override
    public String toString() {
        return String.format("RoomType[id=%d, name=%s, rate=%.2f]",
                roomTypeId, typeName, ratePerNight);
    }
}
