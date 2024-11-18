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

    public void createRoom(Room room) {
        roomRepository.save(room);
    }

    public void updateRoom(Long id, Room updatedRoom) {
        roomRepository.findById(id).ifPresent(existingRoom -> {
            existingRoom.setType(updatedRoom.getType());
            existingRoom.setCapacity(updatedRoom.getCapacity());
            existingRoom.setPricePerNight(updatedRoom.getPricePerNight());
            existingRoom.setServiceName(updatedRoom.getServiceName());
            roomRepository.save(existingRoom);
        });
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public void deleteAllRooms() {
        roomRepository.deleteAll();
    }

}
