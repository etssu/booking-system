package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public class BookingStatusRequest {
    @Schema(
            description = "New booking status",
            example = "CONFIRMED"
    )
    private BookingStatus status;

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
