package com.hotel.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException() {
        super("Booking not found.");
    }
}
