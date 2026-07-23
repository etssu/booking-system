package com.hotel.booking.controller;

import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.dto.UserCreateRequestDTO;
import com.hotel.booking.dto.UserResponseDTO;
import com.hotel.booking.dto.UserUpdateRequestDTO;
import com.hotel.booking.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hotel.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final BookingService bookingService;

    public UserController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<UserResponseDTO> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserUpdateRequestDTO request) {
        return userService.updateUser(id, request);
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@RequestBody UserCreateRequestDTO request) {
        UserResponseDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponseDTO>> getUserBookings(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                bookingService.getBookingsByUser(id)
        );
    }
}
