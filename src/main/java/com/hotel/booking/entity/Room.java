package com.hotel.booking.entity;

import com.hotel.booking.entity.enums.RoomType;
import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    private int number;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private RoomType type;


    private int capacity;
    private boolean isAvailable;

}
