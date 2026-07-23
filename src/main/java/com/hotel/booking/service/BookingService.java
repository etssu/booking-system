package com.hotel.booking.service;

import com.hotel.booking.dto.BookingCreateRequestDTO;
import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.dto.BookingUpdateRequestDTO;
import com.hotel.booking.entity.*;
import com.hotel.booking.entity.enums.BookingStatus;
import com.hotel.booking.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<BookingResponseDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::convertToDTO);
    }

    public BookingResponseDTO createBooking(BookingCreateRequestDTO request) {

        Booking booking = new Booking();

        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setNumberOfGuests(request.getNumberOfGuests());

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        booking.setRoom(room);
        booking.setUser(user);

        validateBookingDates(booking);
        validateGuestCount(booking);

        validateRoomAvailability(
                bookingRepository.existsOverlappingBooking(
                        booking.getRoom(),
                        booking.getCheckIn(),
                        booking.getCheckOut()
                )
        );

        booking.setStatus(BookingStatus.PENDING);

        return convertToDTO(bookingRepository.save(booking));
    }

    public BookingResponseDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        return convertToDTO(booking);
    }

    public BookingResponseDTO updateBooking(Long id, BookingUpdateRequestDTO updatedBooking) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCheckIn(updatedBooking.getCheckIn());
        booking.setCheckOut(updatedBooking.getCheckOut());
        booking.setNumberOfGuests(updatedBooking.getNumberOfGuests());

        if (updatedBooking.getRoomId() != null) {
            Room room = roomRepository.findById(updatedBooking.getRoomId())
                    .orElseThrow(() -> new RuntimeException("Room not found"));

            booking.setRoom(room);
        }

        if (updatedBooking.getUserId() != null) {
            User user = userRepository.findById(updatedBooking.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            booking.setUser(user);
        }

        validateBookingDates(booking);
        validateGuestCount(booking);

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

    private void validateRoomAvailability(boolean exists) {
        if (exists) {
            throw new IllegalArgumentException("Room is already booked for these dates");
        }
    }

    private void validateGuestCount(Booking booking) {
        if (booking.getNumberOfGuests() > booking.getRoom().getCapacity()) {
            throw new IllegalArgumentException(
                    "Number of guests exceeds room capacity."
            );
        }
    }
}
