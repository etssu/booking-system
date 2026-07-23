package com.hotel.booking.dto;

import java.time.LocalDate;

public class BookingCreateRequestDTO {

    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer numberOfGuests;

    private Long userId;
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
