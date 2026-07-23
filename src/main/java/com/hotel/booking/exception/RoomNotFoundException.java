package com.hotel.booking.exception;

public class RoomNotFoundException extends RuntimeException {
    public RoomNotFoundException() {
        super("Room not found.");
    }
}
