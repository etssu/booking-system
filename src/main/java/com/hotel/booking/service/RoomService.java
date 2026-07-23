package com.hotel.booking.service;

import com.hotel.booking.dto.RoomCreateRequestDTO;
import com.hotel.booking.dto.RoomResponseDTO;
import com.hotel.booking.dto.RoomUpdateRequestDTO;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.exception.RoomNotFoundException;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.entity.Room;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public RoomResponseDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(RoomNotFoundException::new);

        return convertToDTO(room);
    }

    public RoomResponseDTO updateRoom(Long id, RoomUpdateRequestDTO request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(RoomNotFoundException::new);

        if (request.getType() != null) {
            room.setType(request.getType());
        }

        if (request.getCapacity() != null) {
            room.setCapacity(request.getCapacity());
        }

        if (request.getPrice() != null) {
            room.setPrice(request.getPrice());
        }

        if (request.getRoomNumber() != null) {
            if (roomRepository.existsByRoomNumberAndIdNot(request.getRoomNumber(), id)) {
                throw new IllegalArgumentException("Room number already exists.");
            }

            room.setRoomNumber(request.getRoomNumber());
        }

        return convertToDTO(roomRepository.save(room));
    }

    public RoomResponseDTO createRoom(RoomCreateRequestDTO request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists.");
        }
        Room room = new Room();

        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setType(request.getType());
        room.setCapacity(request.getCapacity());

        return convertToDTO(roomRepository.save(room));
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(RoomNotFoundException::new);

        roomRepository.delete(room);
    }

    public Page<RoomResponseDTO> getAvailableRooms(
            LocalDate checkIn,
            LocalDate checkOut,
            Pageable pageable
    ) {
        return roomRepository.findAvailableRooms(
                checkIn,
                checkOut,
                pageable
        ).map(this::convertToDTO);
    }

    public Page<RoomResponseDTO> searchRooms(
            RoomType type,
            Integer capacity,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Pageable pageable
    ) {
        return roomRepository.searchRooms(
                type,
                capacity,
                minPrice,
                maxPrice,
                pageable
        ).map(this::convertToDTO);
    }

    private RoomResponseDTO convertToDTO(Room room) {
        return new RoomResponseDTO(
                room.getId(),
                room.getRoomNumber(),
                room.getPrice(),
                room.getType(),
                room.getCapacity()
        );
    }

    public Page<RoomResponseDTO> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable)
                .map(this::convertToDTO);
    }
}
