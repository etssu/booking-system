package com.hotel.booking.service;

import com.hotel.booking.dto.BookingCreateRequestDTO;
import com.hotel.booking.dto.BookingResponseDTO;
import com.hotel.booking.dto.BookingUpdateRequestDTO;
import com.hotel.booking.entity.Booking;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.User;
import com.hotel.booking.entity.enums.BookingStatus;
import com.hotel.booking.entity.enums.Role;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.exception.BookingNotFoundException;
import com.hotel.booking.exception.RoomNotFoundException;
import com.hotel.booking.exception.UserNotFoundException;
import com.hotel.booking.repository.BookingRepository;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private Room existingRoom;
    private User existingUser;
    private Booking existingBooking;


    @BeforeEach
    void setUp() {
        existingRoom = new Room();
        ReflectionTestUtils.setField(existingRoom, "id", 1L);
        existingRoom.setRoomNumber(101);
        existingRoom.setCapacity(2);
        existingRoom.setPrice(BigDecimal.valueOf(100));
        existingRoom.setType(RoomType.STANDARD);

        existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "id", 1L);
        existingUser.setFirstName("John");
        existingUser.setLastName("Doe");
        existingUser.setUsername("johndoe");
        existingUser.setEmail("john@mail.com");
        existingUser.setPassword("password123");
        existingUser.setRole(Role.GUEST);


        existingBooking = new Booking();
        ReflectionTestUtils.setField(existingBooking, "id", 1L);
        existingBooking.setCheckIn(LocalDate.of(2026, 8, 10));
        existingBooking.setCheckOut(LocalDate.of(2026, 8, 15));
        existingBooking.setNumberOfGuests(2);
        existingBooking.setStatus(BookingStatus.CONFIRMED);
        existingBooking.setRoom(existingRoom);
        existingBooking.setUser(existingUser);
    }

    @Test
    void getAllBookings_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Booking> bookingPage = new PageImpl<>(List.of(existingBooking));

        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);

        Page<BookingResponseDTO> result = bookingService.getAllBookings(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(existingBooking.getId());

        verify(bookingRepository).findAll(pageable);
    }

    // get Booking By ID
    @Test
    void getBookingById_shouldReturnBooking() {
        when(bookingRepository.findById(existingBooking.getId())).thenReturn(Optional.of(existingBooking));

        BookingResponseDTO result = bookingService.getBookingById(existingBooking.getId());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomId()).isEqualTo(existingBooking.getRoom().getId());
        assertThat(result.getUserId()).isEqualTo(existingBooking.getUser().getId());

        verify(bookingRepository).findById(existingBooking.getId());
    }

    @Test
    void getBookingById_shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(existingBooking.getId())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(99L));
    }

    // create a Booking
    @Test
    void createBooking_shouldCreateBookingSuccessfully() {
        BookingCreateRequestDTO request = new BookingCreateRequestDTO();
        request.setRoomId(1L);
        request.setUserId(1L);
        request.setCheckIn(LocalDate.of(2026, 8, 10));
        request.setCheckOut(LocalDate.of(2026, 8, 15));
        request.setNumberOfGuests(2);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        when(bookingRepository.existsOverlappingBooking(existingRoom,
                request.getCheckIn(),
                request.getCheckOut())).thenReturn(false);


        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
                    Booking booking = invocation.getArgument(0);
                    ReflectionTestUtils.setField(booking, "id", 1L);
                    return booking;
                });


        BookingResponseDTO result = bookingService.createBooking(request);

        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(BookingStatus.PENDING);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void createBooking_shouldThrowException_whenRoomNotFound() {
        BookingCreateRequestDTO request = new BookingCreateRequestDTO();

        request.setRoomId(99L);
        request.setUserId(1L);
        request.setCheckIn(LocalDate.of(2026, 8, 10));
        request.setCheckOut(LocalDate.of(2026, 8, 15));

        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenUserNotFound() {
        BookingCreateRequestDTO request = new BookingCreateRequestDTO();

        request.setRoomId(1L);
        request.setUserId(99L);
        request.setCheckIn(LocalDate.of(2026, 8, 10));
        request.setCheckOut(LocalDate.of(2026, 8, 15));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenRoomAlreadyBooked() {
        BookingCreateRequestDTO request = new BookingCreateRequestDTO();

        request.setRoomId(1L);
        request.setUserId(1L);
        request.setCheckIn(LocalDate.of(2026, 8, 10));
        request.setCheckOut(LocalDate.of(2026, 8, 15));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        when(bookingRepository.existsOverlappingBooking(existingRoom, request.getCheckIn(),
                request.getCheckOut())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(request));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_shouldThrowException_whenCheckOutBeforeCheckIn() {
        BookingCreateRequestDTO request = new BookingCreateRequestDTO();

        request.setRoomId(1L);
        request.setUserId(1L);

        request.setCheckIn(LocalDate.of(2026, 8, 15));
        request.setCheckOut(LocalDate.of(2026, 8, 10));

        assertThrows(IllegalArgumentException.class, () -> bookingService.createBooking(request));
        verifyNoInteractions(bookingRepository);
    }

    // update Booking
    @Test
    void updateBooking_shouldUpdateBooking() {
        BookingUpdateRequestDTO request = new BookingUpdateRequestDTO();

        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2026, 8, 20));
        request.setCheckOut(LocalDate.of(2026, 8, 25));
        request.setNumberOfGuests(3);


        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));

        when(bookingRepository.existsOverlappingBookingExceptId(
                existingRoom,
                request.getCheckIn(),
                request.getCheckOut(),
                1L
        )).thenReturn(false);

        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO result = bookingService.updateBooking(1L, request);

        assertThat(result.getRoomId()).isEqualTo(1L);
        assertThat(result.getNumberOfGuests()).isEqualTo(3);

        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void updateBooking_shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.updateBooking(99L, new BookingUpdateRequestDTO()));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void deleteBooking_shouldDeleteBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        bookingService.deleteBooking(1L);
        verify(bookingRepository).delete(existingBooking);
    }

    @Test
    void deleteBooking_shouldThrowException_whenBookingNotFound() {
        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class, () -> bookingService.deleteBooking(99L));
        verify(bookingRepository, never()).delete(any());
    }

    // update booking status
    @Test
    void updateBookingStatus_shouldUpdateStatus() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponseDTO result = bookingService.updateBookingStatus(1L, BookingStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(bookingRepository).save(existingBooking);
    }

    @Test
    void updateBookingStatus_shouldThrowException_whenTransitionInvalid() {
        existingBooking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(existingBooking));

        assertThrows(IllegalArgumentException.class, () -> bookingService.updateBookingStatus(1L,
                        BookingStatus.CONFIRMED));

        verify(bookingRepository, never()).save(any());
    }
}
