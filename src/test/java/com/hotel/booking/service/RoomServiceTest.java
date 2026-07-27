package com.hotel.booking.service;

import com.hotel.booking.dto.RoomCreateRequestDTO;
import com.hotel.booking.dto.RoomResponseDTO;
import com.hotel.booking.dto.RoomUpdateRequestDTO;
import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.exception.RoomNotFoundException;
import com.hotel.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    private Room existingRoom;

    @BeforeEach
    void setUp() {
        existingRoom = new Room();
        ReflectionTestUtils.setField(existingRoom, "id", 1L);
        existingRoom.setRoomNumber(1);
        existingRoom.setPrice(BigDecimal.valueOf(65.00));
        existingRoom.setCapacity(4);
        existingRoom.setType(RoomType.STANDARD);
    }

    @Test
    void getAllRooms_shouldReturnMappedPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Room> roomPage = new PageImpl<>(List.of(existingRoom));

        when(roomRepository.findAll(pageable)).thenReturn(roomPage);

        Page<RoomResponseDTO> result = roomService.getAllRooms(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getRoomNumber())
                .isEqualTo(existingRoom.getRoomNumber());

        verify(roomRepository).findAll(pageable);
    }

    // get room by ID
    @Test
    void getRoomById_shouldReturnRoom() {
        when(roomRepository.findById(existingRoom.getId())).thenReturn(Optional.of(existingRoom));

        RoomResponseDTO result = roomService.getRoomById(existingRoom.getId());

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getRoomNumber()).isEqualTo(existingRoom.getRoomNumber());
    }

    @Test
    void getRoomById_shouldThrowException_whenRoomNotFound() {
        when(roomRepository.findById(existingRoom.getId())).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(99L));
    }

    // create room
    @Test
    void createRoom_shouldSaveAndReturnRoom() {
        RoomCreateRequestDTO request = new RoomCreateRequestDTO();

        request.setRoomNumber(101);
        request.setCapacity(2);
        request.setPrice(new BigDecimal("100"));
        request.setType(RoomType.STANDARD);

        Room savedRoom = new Room();
        savedRoom.setRoomNumber(101);
        savedRoom.setCapacity(2);
        savedRoom.setPrice(new BigDecimal("100"));
        savedRoom.setType(RoomType.STANDARD);

        when(roomRepository.save(any(Room.class))).thenReturn(savedRoom);

        RoomResponseDTO result = roomService.createRoom(request);

        assertThat(result.getRoomNumber()).isEqualTo(101);
        assertThat(result.getCapacity()).isEqualTo(2);
        assertThat(result.getType()).isEqualTo(RoomType.STANDARD);

        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);

        verify(roomRepository).save(captor.capture());

        assertThat(captor.getValue().getRoomNumber())
                .isEqualTo(101);
        assertThat(captor.getValue().getCapacity())
                .isEqualTo(2);
    }

    @Test
    void updateRoom_shouldReturnRoom() {
        RoomUpdateRequestDTO request = new RoomUpdateRequestDTO();
        request.setRoomNumber(101);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RoomResponseDTO result = roomService.updateRoom(1L, request);
        assertThat(result.getRoomNumber()).isEqualTo(101);
        // capacity shouldnt change
        assertThat(result.getCapacity()).isEqualTo(4);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateRoom_shouldThrowException_whenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.updateRoom(99L, new RoomUpdateRequestDTO()));
        verify(roomRepository, never()).save(any(Room.class));
    }

    // delete room
    @Test
    void deleteRoom_shouldDeleteRoomSuccessfully() {
        when(roomRepository.findById(existingRoom.getId())).thenReturn(Optional.of(existingRoom));

        roomService.deleteRoom(existingRoom.getId());

        verify(roomRepository).delete(existingRoom);
    }

    @Test
    void deleteRoom_shouldThrowException_whenRoomNotFound() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(99L));

        verify(roomRepository, never()).delete(any(Room.class));
    }

}
