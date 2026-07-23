package com.hotel.booking.controller;

import com.hotel.booking.dto.RoomCreateRequestDTO;
import com.hotel.booking.dto.RoomResponseDTO;
import com.hotel.booking.dto.RoomUpdateRequestDTO;
import com.hotel.booking.entity.enums.RoomType;
import com.hotel.booking.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<Page<RoomResponseDTO>> getRooms(
            @PageableDefault(
                    sort = "roomNumber",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                roomService.getAllRooms(pageable)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<RoomResponseDTO>> searchRooms(
            @RequestParam(required = false) RoomType type,
            @RequestParam(required = false) Integer capacity,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                roomService.searchRooms(type, capacity, minPrice, maxPrice, pageable));
    }

    @GetMapping("/available")
    public ResponseEntity<Page<RoomResponseDTO>> getAvailableRooms(
            @RequestParam LocalDate checkIn,
            @RequestParam LocalDate checkOut,
            Pageable pageable
    ) {
        return ResponseEntity.ok(roomService.getAvailableRooms(checkIn, checkOut, pageable));
    }

    @GetMapping("/{id}")
    public RoomResponseDTO getRoom(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PutMapping("/{id}")
    public RoomResponseDTO updateRoom(@PathVariable Long id,
            @RequestBody RoomUpdateRequestDTO request) {
        return roomService.updateRoom(id, request);
    }

    @PostMapping
    public ResponseEntity<RoomResponseDTO> createRoom(
            @RequestBody @Valid RoomCreateRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(roomService.createRoom(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }

}
