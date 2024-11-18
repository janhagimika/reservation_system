package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Room;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByServiceId(Long serviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Room")
    int deleteAllRooms();
}
