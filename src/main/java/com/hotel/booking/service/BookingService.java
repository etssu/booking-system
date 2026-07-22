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


    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, UserRepository userRepository) {
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

    public BookingResponseDTO createBooking(Booking booking) {
        validateAndSetReferences(booking);
        validateBookingDates(booking);

        validateRoomAvailability(bookingRepository.existsOverlappingBooking(
                booking.getRoom(),
                booking.getCheckIn(),
                booking.getCheckOut()));

        booking.setStatus(BookingStatus.PENDING);
        return convertToDTO(bookingRepository.save(booking));
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return convertToDTO(booking);
    }

    public BookingResponseDTO updateBooking(Long id, Booking updatedBooking) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCheckIn(updatedBooking.getCheckIn());
        booking.setCheckOut(updatedBooking.getCheckOut());
        booking.setNumberOfGuests(updatedBooking.getNumberOfGuests());

        if (updatedBooking.getRoom() != null) {
            booking.setRoom(updatedBooking.getRoom());
        }

        if (updatedBooking.getUser() != null) {
            booking.setUser(updatedBooking.getUser());
        }

        validateAndSetReferences(booking);
        validateBookingDates(booking);

        validateRoomAvailability(
                bookingRepository.existsOverlappingBookingExceptId(
                        booking.getRoom(),
                        booking.getCheckIn(),
                        booking.getCheckOut(),
                        id
                )
        );

        return convertToDTO(bookingRepository.save(booking));
    }

    public BookingResponseDTO cancelBooking(Long id) {
        return updateBookingStatus(id, BookingStatus.CANCELLED);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        bookingRepository.delete(booking);
    }

    public BookingResponseDTO updateBookingStatus(Long id, BookingStatus status) {
        Booking booking = bookingRepository.findById(id).orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!isValidTransition(booking.getStatus(), status)) {
            throw new IllegalArgumentException(
                    "Invalid booking status transition"
            );
        }

        booking.setStatus(status);
        Booking savedBooking = bookingRepository.save(booking);

        return convertToDTO(savedBooking);
    }

    public List<BookingResponseDTO> getBookingsByUser(Long id) {
        return bookingRepository.findByUserId(id).stream()
                .map(this::convertToDTO)
                .toList();
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

    private BookingResponseDTO convertToDTO(Booking booking) {

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

    private void validateBookingDates(Booking booking) {
        if (booking.getCheckIn().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("You cannot book a date before today");
        }

        if (!booking.getCheckOut().isAfter(booking.getCheckIn())) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }
    }

    private void validateAndSetReferences(Booking booking) {
        if (booking.getRoom() == null) {
            throw new IllegalArgumentException("Room is required");
        }

        if (booking.getUser() == null) {
            throw new IllegalArgumentException("User is required");
        }

        Room room = roomRepository.findById(booking.getRoom().getId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        User user = userRepository.findById(booking.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        booking.setRoom(room);
        booking.setUser(user);
    }

    private void validateRoomAvailability(boolean exists) {
        if (exists) {
            throw new IllegalArgumentException("Room is already booked for these dates");
        }
    }
}
