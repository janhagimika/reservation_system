package com.example.reservation_system.tests.services;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.ReservationRepository;
import com.example.reservation_system.repositories.ServRepository;
import com.example.reservation_system.repositories.UserRepository;
import com.example.reservation_system.services.EmailService;
import com.example.reservation_system.services.ReservationService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ServRepository servRepository;
    @Mock
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateReservation_Success() throws MessagingException {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Serv service = new Serv(1L, "Test Service");
        Reservation reservation = new Reservation(user, service,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(servRepository.findByServiceId(1L)).thenReturn(Optional.of(service));
        when(reservationRepository.hasOverlappingReservation(1L, 1L, reservation.getStartTime(), reservation.getEndTime())).thenReturn(false);
        when(reservationRepository.isServiceAvailable(1L, reservation.getStartTime(), reservation.getEndTime())).thenReturn(true);

        // Act
        reservationService.createReservation(reservation, "BAR");

        // Assert
        verify(reservationRepository).save(reservation);
        verify(emailService).sendReservationConfirmation(
                eq(user.getEmail()), anyString(), contains("Rezervace potvrzena"));
    }

    @Test
    void testCreateReservation_OverlapException() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Serv service = new Serv(1L, "Test Service");
        Reservation reservation = new Reservation(user, service,
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(servRepository.findByServiceId(1L)).thenReturn(Optional.of(service));
        when(reservationRepository.hasOverlappingReservation(1L, 1L, reservation.getStartTime(), reservation.getEndTime())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> reservationService.createReservation(reservation, "BAR"));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void testFindAvailableTimes() {
        // Arrange
        Long serviceId = 1L;
        LocalDateTime startDate = LocalDateTime.of(2023, 11, 27, 8, 0);
        LocalDateTime endDate = LocalDateTime.of(2023, 11, 27, 18, 0);
        Duration duration = Duration.ofHours(1);

        when(reservationRepository.findReservationsByServiceAndDateRange(serviceId, startDate, endDate))
                .thenReturn(Collections.emptyList());

        // Act
        List<LocalDateTime> availableTimes = reservationService.findAvailableTimes(
                serviceId, startDate, endDate, "LIFT", duration);

        // Assert
        assertEquals(10, availableTimes.size()); // Example: 8:00 to 18:00 with 1-hour slots
        assertEquals(LocalDateTime.of(2023, 11, 27, 8, 0), availableTimes.get(0));
        assertEquals(LocalDateTime.of(2023, 11, 27, 17, 0), availableTimes.get(availableTimes.size() - 1));
    }

    @Test
    void testDeleteReservation_Success() {
        // Arrange
        Long reservationId = 1L;
        when(reservationRepository.existsById(reservationId)).thenReturn(true);

        // Act
        boolean result = reservationService.deleteReservation(reservationId);

        // Assert
        assertTrue(result);
        verify(reservationRepository).deleteById(reservationId);
    }

    @Test
    void testDeleteReservation_NotFound() {
        // Arrange
        Long reservationId = 1L;
        when(reservationRepository.existsById(reservationId)).thenReturn(false);

        // Act
        boolean result = reservationService.deleteReservation(reservationId);

        // Assert
        assertFalse(result);
        verify(reservationRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetStatistics() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Serv service = new Serv(1L, "Test Service");
        Reservation reservation = new Reservation(user, service,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusHours(1));

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));
        when(reservationRepository.findByUserId(1L)).thenReturn(Collections.singletonList(reservation));

        // Act
        List<Map<String, Object>> statistics = reservationService.getStatistics();

        // Assert
        assertEquals(1, statistics.size());
        Map<String, Object> userStats = statistics.get(0);
        assertEquals(1, userStats.get("totalReservations"));
        assertEquals(1, userStats.get("passedReservations"));
        assertEquals("Test Service", userStats.get("mostReservedCommodity"));
    }


    @Test
    void testUpdateReservation_Success() {
        // Arrange
        Long reservationId = 1L;
        Reservation existingReservation = new Reservation();
        Reservation updatedReservation = new Reservation();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(existingReservation));

        // Act
        int result = reservationService.updateReservation(reservationId, updatedReservation);

        // Assert
        assertEquals(1, result);
        verify(reservationRepository).save(updatedReservation);
    }

    @Test
    void testUpdateReservation_NotFound() {
        // Arrange
        Long reservationId = 1L;
        Reservation updatedReservation = new Reservation();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.empty());

        // Act
        int result = reservationService.updateReservation(reservationId, updatedReservation);

        // Assert
        assertEquals(0, result);
        verify(reservationRepository, never()).save(any(Reservation.class));
    }


}
