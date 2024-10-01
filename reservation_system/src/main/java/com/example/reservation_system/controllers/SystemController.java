package com.example.reservation_system.controllers;

import com.example.reservation_system.repositories.*;
import com.example.reservation_system.services.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private LiftRepository liftRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BarRepository barRepository;

    @Autowired
    private ServRepository servRepository;

    @Autowired
    private ReservationService reservationService;

    // Získání aktuální provozní doby
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/business-hours")
    public ResponseEntity<Map<String, String>> getBusinessHours() {
        Map<String, String> businessHours = reservationService.getBusinessHours();
        return ResponseEntity.ok(businessHours);
    }

    // Uložení nové provozní doby
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/business-hours")
    public ResponseEntity<String> updateBusinessHours(@RequestBody Map<String, String> businessHours) {
        reservationService.updateBusinessHours(businessHours);
        return ResponseEntity.ok("Provozní doba byla úspěšně aktualizována.");
    }

    // Resetování databáze - všechny tabulky se vymažou
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        try {
            reservationRepository.deleteAllReservations();
            userRepository.deleteAllUsers();
            servRepository.deleteAllServs();
            liftRepository.deleteAllLifts();
            roomRepository.deleteAllRooms();
            barRepository.deleteAllBars();

            return ResponseEntity.ok("Databáze byla úspěšně resetována.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Chyba při resetování databáze: " + e.getMessage());
        }
    }
}
