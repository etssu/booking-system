package com.hotel.booking.service;

import com.hotel.booking.dto.RoomCreateRequestDTO;
import com.hotel.booking.dto.RoomResponseDTO;
import com.hotel.booking.dto.RoomUpdateRequestDTO;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.entity.Room;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class RoomService {
    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll().stream().map(this::convertToDTO).toList();
    }

    public RoomResponseDTO getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return convertToDTO(room);
    }

    public RoomResponseDTO updateRoom(Long id, RoomUpdateRequestDTO request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

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

            if (roomRepository.existsByRoomNumberAndIdNot(
                    request.getRoomNumber(),
                    id
            )) {
                throw new IllegalArgumentException("Room number already exists");
            }

            room.setRoomNumber(request.getRoomNumber());
        }

        return convertToDTO(roomRepository.save(room));
    }

    public RoomResponseDTO createRoom(RoomCreateRequestDTO request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists");
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
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomRepository.delete(room);
    }

    public List<RoomResponseDTO> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut).stream()
                .map(this::convertToDTO).toList();
    }

    public List<RoomResponseDTO> searchRooms(
            RoomType type,
            Integer capacity,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return roomRepository.searchRooms(type, capacity, minPrice, maxPrice).stream()
                .map(this::convertToDTO).toList();
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
}
