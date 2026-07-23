package com.hotel.booking.controller;


import com.hotel.booking.dto.BookingCreateRequestDTO;
import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.dto.BookingStatusRequest;
import com.hotel.booking.dto.BookingUpdateRequestDTO;
import com.hotel.booking.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(
            @PathVariable Long id,
            @RequestBody BookingUpdateRequestDTO request
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @RequestBody BookingCreateRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody BookingStatusRequest request
    ) {


        return ResponseEntity.ok(bookingService.updateBookingStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

}
