package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.RoomType;

import java.math.BigDecimal;

public class RoomUpdateRequestDTO {
    private Integer roomNumber;
    private BigDecimal price;
    private RoomType type;
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
