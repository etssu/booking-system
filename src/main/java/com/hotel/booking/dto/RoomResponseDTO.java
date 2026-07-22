package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.RoomType;

import java.math.BigDecimal;

public class RoomResponseDTO {

    private Long id;
    private Integer roomNumber;
    private BigDecimal price;
    private RoomType type;
    private Integer capacity;

    public RoomResponseDTO(
            Long id,
            Integer roomNumber,
            BigDecimal price,
            RoomType type,
            Integer capacity
    ) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.price = price;
        this.type = type;
        this.capacity = capacity;
    }

    public Long getId() {
        return id;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public RoomType getType() {
        return type;
    }

    public Integer getCapacity() {
        return capacity;
    }
}
