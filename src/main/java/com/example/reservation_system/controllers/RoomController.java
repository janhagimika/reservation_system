package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Room;
import com.example.reservation_system.services.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        Optional<Room> room = roomService.getRoomById(id);
        return room.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<String> createRoom(@RequestBody Room room) {
        roomService.createRoom(room);
        return ResponseEntity.ok("Pokoj vytvořen úspěšně.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRoom(@PathVariable Long id, @RequestBody Room room) {
        if (roomService.getRoomById(id).isPresent()) {
            roomService.updateRoom(id, room);
            return ResponseEntity.ok("Pokoj upraven úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        if (roomService.getRoomById(id).isPresent()) {
            roomService.deleteRoom(id);
            return ResponseEntity.ok("Pokoj smazán úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
