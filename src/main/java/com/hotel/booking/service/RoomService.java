package com.hotel.booking.service;

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


    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    public Room updateRoom(Long id, Room updatedRoom) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setType(updatedRoom.getType());
        room.setCapacity(updatedRoom.getCapacity());
        room.setPrice(updatedRoom.getPrice());
        room.setRoomNumber(updatedRoom.getRoomNumber());

        return roomRepository.save(room);
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        roomRepository.delete(room);
    }

    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findAvailableRooms(checkIn, checkOut);
    }

    public List<Room> searchRooms(
            RoomType type,
            Integer capacity,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        return roomRepository.searchRooms(type, capacity, minPrice, maxPrice);
    }
}
