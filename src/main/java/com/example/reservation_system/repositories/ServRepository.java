package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Serv;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServRepository extends JpaRepository<Serv, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM Serv")
    int deleteAllServs();
}
