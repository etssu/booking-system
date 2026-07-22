package com.hotel.booking.controller;



import com.hotel.booking.entity.Room;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Room>> searchRooms(@RequestParam(required = false) RoomType type,
                                                  @RequestParam(required = false) Integer capacity,
                                                  @RequestParam(required = false) BigDecimal minPrice,
                                                  @RequestParam(required = false) BigDecimal maxPrice) {
        return ResponseEntity.ok(
                roomService.searchRooms(type, capacity, minPrice, maxPrice)
        );
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut
    ) {
        return ResponseEntity.ok(
                roomService.getAvailableRooms(checkIn, checkOut)
        );
    }

    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PutMapping("/{id}")
    public Room updateRoom(@PathVariable Long id, @RequestBody Room room) {
        return roomService.updateRoom(id, room);
    }

    @PostMapping
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

}
