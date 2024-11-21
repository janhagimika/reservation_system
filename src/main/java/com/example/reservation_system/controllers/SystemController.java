package com.example.reservation_system.controllers;

import com.example.reservation_system.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/system")
public class SystemController {
    private final ReservationService reservationService;
    private final UserService userService;
    private final ServService servService;
    private final LiftService liftService;
    private final RoomService roomService;
    private final BarService barService;

    public SystemController(ReservationService reservationService,
                            UserService userService, ServService servService,
                            LiftService liftService, RoomService roomService,
                            BarService barService){
        this.reservationService = reservationService;
        this.userService = userService;
        this.servService = servService;
        this.liftService = liftService;
        this.roomService = roomService;
        this.barService = barService;
    }

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
