package com.example.reservation_system.repositories;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // Search by name only
    @Query("SELECT r FROM Reservation r " +
            "JOIN r.user u " +
            "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :name, '%')) " +
            "OR LOWER(u.surname) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Reservation> findReservationsByName(@Param("name") String name);

    // Search by start date only
    @Query("SELECT r FROM Reservation r " +
            "WHERE DATE(r.startTime) = :date ORDER BY r.startTime ASC")
    List<Reservation> findReservationsByStartDate(@Param("date") LocalDate date);

    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation")
    int deleteAllReservations();
}
