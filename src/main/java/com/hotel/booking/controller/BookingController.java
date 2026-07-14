package com.hotel.booking.controller;


import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.service.BookingService;
import com.hotel.booking.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService = roomService;
    }

    @GetMapping
    public List<Booking> getBookings() { return bookingService.getAllBookings(); }

    @GetMapping("/{id}")
    public Booking getBooking(@PathVariable Long id) {
        return bookingService.getBookingById(id);
    }

    @PutMapping("/{id}")
    public Booking updateBooking(
            @PathVariable Long id,
            @RequestBody Booking booking
    ) {
        return bookingService.updateBooking(id, booking);
    }

    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        return bookingService.createBooking(booking);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
