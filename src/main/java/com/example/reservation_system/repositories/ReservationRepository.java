package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    @Query("SELECT CASE WHEN COUNT(r) = 0 THEN true ELSE false END FROM Reservation r WHERE r.service.serviceId = :serviceId AND ((:startTime < r.endTime) AND (:endTime > r.startTime))")
    boolean isServiceAvailable(@Param("serviceId") Long serviceId,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(r) > 0 FROM Reservation r WHERE r.user.id = :userId AND r.service.serviceId = :serviceId AND ((r.startTime < :endTime AND r.endTime > :startTime) OR (r.startTime < :endTime AND r.endTime > :startTime))")
    boolean hasOverlappingReservation(Long userId, Long serviceId, LocalDateTime startTime, LocalDateTime endTime);

    @Query("SELECT r FROM Reservation r WHERE r.service.serviceId = :serviceId AND " +
            "((:startDate < r.endTime) AND (:endDate > r.startTime))")
    List<Reservation> findReservationsByServiceAndDateRange(
            @Param("serviceId") Long serviceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation")
    int deleteAllReservations();
}
