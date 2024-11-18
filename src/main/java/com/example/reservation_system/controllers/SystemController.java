package com.example.reservation_system.controllers;

import com.example.reservation_system.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemController {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private UserService userService;
    @Autowired
    private ServService servService;
    @Autowired
    private LiftService liftService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private BarService barService;

    // Get current business hours
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','USER')")
    @GetMapping("/business-hours")
    public ResponseEntity<Map<String, String>> getBusinessHours() {
        Map<String, String> businessHours = reservationService.getBusinessHours();
        return ResponseEntity.ok(businessHours);
    }

    // Update business hours
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/business-hours")
    public ResponseEntity<String> updateBusinessHours(@RequestBody Map<String, String> businessHours) {
        reservationService.updateBusinessHours(businessHours);
        return ResponseEntity.ok("Business hours updated successfully.");
    }

    // Reset database - delete all entries in all tables
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/reset")
    public ResponseEntity<String> resetDatabase() {
        try {
            reservationService.deleteAllReservations();
            userService.deleteAllUsers();
            servService.deleteAllServices();
            liftService.deleteAllLifts();
            roomService.deleteAllRooms();
            barService.deleteAllBars();

            return ResponseEntity.ok("Database reset successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error resetting database: " + e.getMessage());
        }
    }
}
