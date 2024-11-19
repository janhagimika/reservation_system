package com.example.reservation_system.controllers;

import com.example.reservation_system.models.AvailableTimeRequest;
import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.ReservationRequest;
import com.example.reservation_system.services.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping
    public List<Map<String, Object>> getAllReservations() {
        return reservationService.getAllReservationsWithCommodities();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Long userId) {
        List<Reservation> reservations = reservationService.getReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getStatistics() {
        List<Map<String, Object>> statistics = reservationService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/available-times")
    public Map<String, List<LocalDateTime>> getAvailableTimes(@RequestBody AvailableTimeRequest request) {
        Long serviceId = request.getServiceId(); // Extract from request body
        LocalDateTime startTime = request.getStartTime();
        LocalDateTime endTime = request.getEndTime();
        String serviceType = request.getServiceType();
        // Fetch available times for different durations

        List<LocalDateTime> thirtyMinSlots = reservationService.findAvailableTimes(serviceId, startTime, endTime, serviceType, Duration.ofMinutes(30));

        // Return the time slots in a structured way
        Map<String, List<LocalDateTime>> availableTimes = new HashMap<>();
        availableTimes.put("30_min_slots", thirtyMinSlots);

        return availableTimes;
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody Map<String, Object> requestBody) {
        ReservationRequest request = new ReservationRequest();

        Reservation reservation = reservationService.convertToReservation(requestBody);
        request.setReservation(reservation);
        request.setServiceType((String) requestBody.get("serviceType"));
        try {
            reservationService.createReservation(request.getReservation(), request.getServiceType());  // Call service method with reservation and type
            return ResponseEntity.ok("Rezervace vytvořena úspěšně.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        if (reservationService.updateReservation(id, reservation) > 0) {
            return ResponseEntity.ok("Rezervace upravena úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        if (reservationService.deleteReservation(id)) {
            return ResponseEntity.ok("Reservation deleted successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found.");
        }
    }

}
