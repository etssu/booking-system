package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.BookingStatus;

public class BookingStatusRequest {
    private BookingStatus status;

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
