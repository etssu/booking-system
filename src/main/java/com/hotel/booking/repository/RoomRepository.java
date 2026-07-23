package com.hotel.booking.repository;

import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.enums.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumber(Integer roomNumber);
    boolean existsByRoomNumberAndIdNot(Integer roomNumber, Long id);

    @Query("""
    SELECT r
    FROM Room r
    WHERE NOT EXISTS (
        SELECT b
        FROM Booking b
        WHERE b.room = r
        AND b.status <> com.hotel.booking.entity.enums.BookingStatus.CANCELLED
        AND b.checkIn < :checkOut
        AND b.checkOut > :checkIn
    )
""")
    Page<Room> findAvailableRooms(
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            Pageable pageable
    );

    @Query("""
    SELECT r FROM Room r
    WHERE (:type IS NULL OR r.type = :type)
    AND (:capacity IS NULL OR r.capacity >= :capacity)
    AND (:minPrice IS NULL OR r.price >= :minPrice)
    AND (:maxPrice IS NULL OR r.price <= :maxPrice)
    """)
    Page<Room> searchRooms(
            @Param("type") RoomType type,
            @Param("capacity") Integer capacity,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );


}
