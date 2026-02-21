package com.oceanview.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base Model implementing common properties for all domain objects
 * Follows JavaBean conventions for enterprise applications
 */
public abstract class BaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BaseModel() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        if (createdAt != null) {
            this.createdAt = createdAt;
        }
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        if (updatedAt != null) {
            this.updatedAt = updatedAt;
        }
    }

    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public abstract String toString();
}
