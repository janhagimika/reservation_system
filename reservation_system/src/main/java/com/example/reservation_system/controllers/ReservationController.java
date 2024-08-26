package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservation = reservationService.getReservationById(id);
        return reservation.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createReservation(@RequestBody Reservation reservation) {
        try {
            reservationService.createReservation(reservation);
            return ResponseEntity.ok("Rezervace vytvořena úspěšně.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateReservation(@PathVariable Long id, @RequestBody Reservation reservation) {
        if (reservationService.updateReservation(id, reservation) > 0) {
            return ResponseEntity.ok("Rezervace upravena úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteReservation(@PathVariable Long id) {
        if (reservationService.deleteReservation(id) > 0) {
            return ResponseEntity.ok("Rezervace smazána úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }
}
