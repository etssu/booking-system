package com.hotel.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;

public class BookingCreateRequestDTO {
    @NotNull(message = "Check-in date is required.")
    private LocalDate checkIn;

    @NotNull(message = "Check-out date is required.")
    private LocalDate checkOut;

    @NotNull(message = "Number of guests is required.")
    @Min(value = 1, message = "Number of guests must be at least 1.")
    private Integer numberOfGuests;

    @NotNull(message = "Room id is required.")
    private Long userId;
    @NotNull(message = "Room id is required.")
    private Long roomId;

    public LocalDate getCheckIn() {
        return checkIn;
    }
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }

    public Integer getNumberOfGuests() {
        return numberOfGuests;
    }
    public void setNumberOfGuests(Integer numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getRoomId() {
        return roomId;
    }
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
}
