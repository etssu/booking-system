package com.hotel.booking.service;

import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.repository.RoomRepository;
import com.hotel.booking.entity.Room;
import org.springframework.stereotype.Service;

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
                .orElseThrow();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    public List<Room> getRoomsByType(RoomType type) {
        return roomRepository.findByType(type);
    }

    public List<Room> getRoomsByCapacity(int capacity) {
        return roomRepository.findByCapacity(capacity);
    }
}
