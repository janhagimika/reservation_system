package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Bar;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarRepository extends JpaRepository<Bar, Long> {

    Optional<Bar> findByServiceId(Long serviceId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Bar")
    int deleteAllBars();
}
