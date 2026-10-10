package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingCreateRequestDTO;
import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.dto.BookingStatusRequest;
import com.hotel.booking.dto.BookingUpdateRequestDTO;
import com.hotel.booking.security.CustomUserDetails;
import com.hotel.booking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public ResponseEntity<Page<BookingResponseDTO>> getAllBookings(
            Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(bookingService.getAllBookings(
                pageable,
                currentUser.getUserId(),
                currentUser.isAdmin()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> getBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(bookingService.getBookingById(
                id,
                currentUser.getUserId(),
                currentUser.isAdmin()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookingResponseDTO> updateBooking(@PathVariable Long id,
            @Valid @RequestBody BookingUpdateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.ok(bookingService.updateBooking(id, request, currentUser.getUserId(), currentUser.isAdmin()));
    }

    @PostMapping
    public ResponseEntity<BookingResponseDTO> createBooking(
            @Valid @RequestBody BookingCreateRequestDTO request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(bookingService.createBooking(request, currentUser.getUserId()
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<BookingResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusRequest request
    ) {
        System.out.println("PATCH /bookings/{id}/status reached controller");
        return ResponseEntity.ok(bookingService.updateBookingStatus(id, request.getStatus()));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

}
