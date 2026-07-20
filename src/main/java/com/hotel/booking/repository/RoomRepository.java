package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.enums.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByType(RoomType type);
    List<Room> findByCapacity(int capacity);

    @Query("""
    SELECT r
    FROM Room r
    WHERE NOT EXISTS (
        SELECT b
        FROM Booking b
        WHERE b.room = r
        AND b.checkIn < :checkOut
        AND b.checkOut > :checkIn
    )
""")
    List<Room> findAvailableRooms(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

}
