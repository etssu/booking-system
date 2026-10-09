package com.hotel.booking.dto;

import com.hotel.booking.entity.enums.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public class BookingStatusRequest {
    @Schema(
            description = "New booking status",
            example = "CONFIRMED"
    )

    @NotNull(message = "Status is required.")
    private BookingStatus status;

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
