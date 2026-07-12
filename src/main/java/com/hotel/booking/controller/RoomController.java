package com.hotel.booking.controller;



import com.hotel.booking.entity.Room;
import com.hotel.booking.service.RoomService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;


    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }


    @GetMapping
    public List<Room> getRooms() {
        return roomService.getAllRooms();
    }


    @GetMapping("/{id}")
    public Room getRoom(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }
}
