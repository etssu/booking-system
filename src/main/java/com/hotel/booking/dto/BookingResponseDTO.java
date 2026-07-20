package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.BookingStatus;

import java.time.LocalDate;

public class BookingResponseDTO {
    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfGuests;
    private BookingStatus status;

    private Long userId;
    private String guestName;

    private Long roomId;
    private Integer roomNumber;

    public BookingResponseDTO(Long id, LocalDate checkIn, LocalDate checkOut,
            Integer numberOfGuests, BookingStatus status, Long userId,
            String guestName, Long roomId, Integer roomNumber
    ) {
        this.id = id;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.numberOfGuests = numberOfGuests;
        this.status = status;
        this.userId = userId;
        this.guestName = guestName;
        this.roomId = roomId;
        this.roomNumber = roomNumber;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoomId() {
        return roomId;
    }

}
