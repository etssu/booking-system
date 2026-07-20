package com.hotel.booking.entity;

import com.hotel.booking.entity.enums.RoomType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;


@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer roomNumber;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private RoomType type;

    private int capacity;

    @OneToMany(mappedBy = "room")
    private List<Booking> bookings;


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

    public int getCapacity() {
        return capacity;
    }

    public void setType(RoomType type) {
        this.type = type;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }

}
