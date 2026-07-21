package com.hotel.booking.service;

import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.entity.*;
import com.hotel.booking.entity.enums.BookingStatus;
import com.hotel.booking.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private LocalDate today = LocalDate.now();


    public BookingService(BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<BookingResponseDTO> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public Booking createBooking(Booking booking) {
        if (booking.getRoom() != null) {
            Room room = roomRepository.findById(booking.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            booking.setRoom(room);
        }

        if (booking.getUser() != null) {
            User user = userRepository.findById(booking.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            booking.setUser(user);
        }

        if (booking.getCheckIn().isAfter(booking.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        if (booking.getCheckIn().isBefore(today)) {
            throw new IllegalArgumentException("You cannot book a date before today");
        }

        if (!booking.getCheckOut().isAfter(booking.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        boolean exists = bookingRepository.existsOverlappingBooking(    // checking if overlapping booking exists
                booking.getRoom(),
                booking.getCheckIn(),
                booking.getCheckOut()
        );

        if (exists) {
            throw new IllegalArgumentException("Room is already booked for these dates");
        }

        booking.setStatus(BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return convertToDTO(booking);
    }

    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCheckIn(updatedBooking.getCheckIn());
        booking.setCheckOut(updatedBooking.getCheckOut());
        booking.setNumberOfGuests(updatedBooking.getNumberOfGuests());

        if (updatedBooking.getRoom() != null) {
            Room room = roomRepository.findById(updatedBooking.getRoom().getId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            booking.setRoom(room);
        }

        if (updatedBooking.getUser() != null) {
            User user = userRepository.findById(updatedBooking.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            booking.setUser(user);
        }

        if (booking.getCheckIn().isAfter(booking.getCheckOut())) {
            throw new IllegalArgumentException("Check-in date must be before check-out date");
        }

        if (booking.getCheckIn().isBefore(today)) {
            throw new IllegalArgumentException("You cannot book a date before today");
        }

        if (!booking.getCheckOut().isAfter(booking.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        boolean exists = bookingRepository.existsOverlappingBookingExceptId(
                booking.getRoom(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                id
        );

        if (exists) {
            throw new IllegalArgumentException("Room is already booked for these dates");
        }

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        bookingRepository.delete(booking);
    }

    public Booking updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!isValidTransition(booking.getStatus(), status)) {
            throw new IllegalArgumentException(
                    "Invalid booking status transition"
            );
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    private boolean isValidTransition(BookingStatus current, BookingStatus next) {
        return switch (current) {
            case PENDING -> next == BookingStatus.CONFIRMED ||
                            next == BookingStatus.CANCELLED;

            case CONFIRMED -> next == BookingStatus.CHECKED_IN ||
                            next == BookingStatus.CANCELLED;

            case CHECKED_IN -> next == BookingStatus.COMPLETED;

            case COMPLETED, CANCELLED -> false;
        };
    }

    public BookingResponseDTO convertToDTO(Booking booking) {

        String guestName = booking.getUser().getFirstName()
                + " "
                + booking.getUser().getLastName();

        return new BookingResponseDTO(
                booking.getId(),
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getNumberOfGuests(),
                booking.getStatus(),
                booking.getUser().getId(),
                guestName,
                booking.getRoom().getId(),
                booking.getRoom().getRoomNumber()
        );
    }

}
