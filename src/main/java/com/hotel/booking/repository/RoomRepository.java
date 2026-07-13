package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByAvailableTrue();
    List<Room> findByType(RoomType type);
    List<Room> findByCapacity(int capacity);

}
