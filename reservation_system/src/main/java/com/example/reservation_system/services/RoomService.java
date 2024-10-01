package com.example.reservation_system.services;

import com.example.reservation_system.models.Room;
import com.example.reservation_system.repositories.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public int createRoom(Room room) {
        return roomRepository.save(room);
    }

    public int updateRoom(Long id, Room room) {
        Optional<Room> existingRoom = roomRepository.findById(id);
        if (existingRoom.isPresent()) {
            room.setServiceId(id);
            return roomRepository.update(room);
        } else {
            return 0;
        }
    }

    public int deleteRoom(Long id) {
        return roomRepository.deleteById(id);
    }
}
