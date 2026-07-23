package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public class RoomUpdateRequestDTO {
    @Min(value = 1, message = "Room number must be positive.")
    private Integer roomNumber;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0.")
    private BigDecimal price;

    private RoomType type;

    @Min(value = 1, message = "Capacity must be at least 1.")
    private Integer capacity;

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public RoomType getType() {
        return type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
}
