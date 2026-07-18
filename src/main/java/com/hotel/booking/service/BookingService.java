package com.hotel.booking.service;

import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;


    public BookingService(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
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

        return bookingRepository.save(booking);
    }

    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public Booking updateBooking(Long id, Booking updatedBooking) {
        Booking booking  = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setCheckIn(updatedBooking.getCheckIn());
        booking.setCheckOut(updatedBooking.getCheckOut());
        booking.setNumberOfGuests(updatedBooking.getNumberOfGuests());
        booking.setRoom(updatedBooking.getRoom());

        return bookingRepository.save(booking);
    }

    public void deleteBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        bookingRepository.delete(booking);
    }

}
