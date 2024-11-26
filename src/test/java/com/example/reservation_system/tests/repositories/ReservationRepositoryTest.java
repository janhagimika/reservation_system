package com.example.reservation_system.tests.repositories;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.repositories.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void testIsServiceAvailable_ServiceAvailable() {
        // Arrange: Insert the service manually
        entityManager.createNativeQuery("INSERT INTO serv (service_name) VALUES (:name)")
                .setParameter("name", "Test Service")
                .executeUpdate();

        // Retrieve the service ID for the inserted record
        Long serviceId = (Long) entityManager.createNativeQuery("SELECT service_id FROM serv WHERE service_name = :name")
                .setParameter("name", "Test Service")
                .getSingleResult();

        // Arrange: Insert a reservation manually
        entityManager.createNativeQuery(
                        "INSERT INTO reservation (service_id, start_time, end_time) VALUES (:serviceId, :startTime, :endTime)")
                .setParameter("serviceId", serviceId)
                .setParameter("startTime", LocalDateTime.of(2024, 1, 1, 10, 0))
                .setParameter("endTime", LocalDateTime.of(2024, 1, 1, 12, 0))
                .executeUpdate();

        // Act: Call the repository method
        boolean result = reservationRepository.isServiceAvailable(
                serviceId,
                LocalDateTime.of(2024, 1, 1, 12, 0),
                LocalDateTime.of(2024, 1, 1, 14, 0)
        );

        // Assert: Verify service availability
        assertTrue(result);
    }


    @Test
    void testIsServiceAvailable_ServiceNotAvailable() {
        // Arrange: Insert the service manually
        entityManager.createNativeQuery("INSERT INTO serv (service_name) VALUES (:name)")
                .setParameter("name", "Test Service")
                .executeUpdate();

        // Retrieve the service ID for the inserted record
        Long serviceId = (Long) entityManager.createNativeQuery("SELECT service_id FROM serv WHERE service_name = :name")
                .setParameter("name", "Test Service")
                .getSingleResult();

        // Arrange: Insert a reservation manually
        entityManager.createNativeQuery(
                        "INSERT INTO reservation (service_id, start_time, end_time) VALUES (:serviceId, :startTime, :endTime)")
                .setParameter("serviceId", serviceId)
                .setParameter("startTime", LocalDateTime.of(2024, 1, 1, 10, 0))
                .setParameter("endTime", LocalDateTime.of(2024, 1, 1, 12, 0))
                .executeUpdate();

        // Act: Call the repository method
        boolean result = reservationRepository.isServiceAvailable(
                serviceId,
                LocalDateTime.of(2024, 1, 1, 11, 0),
                LocalDateTime.of(2024, 1, 1, 13, 0)
        );

        // Assert: Verify service availability
        assertFalse(result);
    }

    @Test
    void testHasOverlappingReservation() {
        // Arrange: Insert the service manually
        entityManager.createNativeQuery("INSERT INTO serv (service_name) VALUES (:name)")
                .setParameter("name", "Test Service")
                .executeUpdate();

        // Retrieve the service ID
        Long serviceId = ((Number) entityManager.createNativeQuery("SELECT service_id FROM serv WHERE service_name = :name")
                .setParameter("name", "Test Service")
                .getSingleResult()).longValue();

        // Insert the user manually
        entityManager.createNativeQuery(
                        "INSERT INTO \"user\" (username, first_name, surname, email, password, phone_number, role) " +
                                "VALUES (:username, :firstName, :surname, :email, :password, :phoneNumber, :role)")
                .setParameter("username", "testuser")
                .setParameter("firstName", "John")
                .setParameter("surname", "Doe")
                .setParameter("email", "testuser@example.com")
                .setParameter("password", "password123")
                .setParameter("phoneNumber", "123456789")
                .setParameter("role", "USER")
                .executeUpdate();

        // Retrieve the user ID
        Long userId = ((Number) entityManager.createNativeQuery("SELECT id FROM \"user\" WHERE username = :username")
                .setParameter("username", "testuser")
                .getSingleResult()).longValue();

        // Insert a reservation manually
        entityManager.createNativeQuery(
                        "INSERT INTO reservation (user_id, service_id, start_time, end_time) VALUES (:userId, :serviceId, :startTime, :endTime)")
                .setParameter("userId", userId)
                .setParameter("serviceId", serviceId)
                .setParameter("startTime", LocalDateTime.now())
                .setParameter("endTime", LocalDateTime.now().plusHours(2))
                .executeUpdate();

        // Act: Check overlapping reservation
        boolean hasOverlap = reservationRepository.hasOverlappingReservation(
                userId,
                serviceId,
                LocalDateTime.now().plusMinutes(30),
                LocalDateTime.now().plusHours(3)
        );

        // Assert: Verify overlap detection
        assertTrue(hasOverlap);
    }

    @Test
    void testFindReservationsByServiceAndDateRange() {
        // Arrange: Insert the service manually
        entityManager.createNativeQuery("INSERT INTO serv (service_name) VALUES (:name)")
                .setParameter("name", "Test Service")
                .executeUpdate();

        // Retrieve the service ID
        Long serviceId = ((Number) entityManager.createNativeQuery("SELECT service_id FROM serv WHERE service_name = :name")
                .setParameter("name", "Test Service")
                .getSingleResult()).longValue();

        // Insert a reservation manually
        entityManager.createNativeQuery(
                        "INSERT INTO reservation (service_id, start_time, end_time) VALUES (:serviceId, :startTime, :endTime)")
                .setParameter("serviceId", serviceId)
                .setParameter("startTime", LocalDateTime.of(2024, 1, 1, 10, 0))
                .setParameter("endTime", LocalDateTime.of(2024, 1, 1, 12, 0))
                .executeUpdate();

        // Act: Query reservations by service and date range
        List<Reservation> reservations = reservationRepository.findReservationsByServiceAndDateRange(
                serviceId,
                LocalDateTime.of(2024, 1, 1, 9, 0),
                LocalDateTime.of(2024, 1, 1, 11, 0)
        );

        // Assert: Verify the result
        assertFalse(reservations.isEmpty());
        assertEquals(1, reservations.size());
    }
    }
