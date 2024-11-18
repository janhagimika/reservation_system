package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.services.LiftService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lifts")
public class LiftController {

    private final LiftService liftService;

    public LiftController(LiftService liftService) {
        this.liftService = liftService;
    }

    @GetMapping
    public List<Lift> getAllLifts() {
        return liftService.getAllLifts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Lift> getLiftById(@PathVariable Long id) {
        Optional<Lift> lift = liftService.getLiftById(id);
        return lift.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<String> createLift(@RequestBody Lift lift) {
        liftService.createLift(lift);
        return ResponseEntity.ok("Lanovka vytvořena úspěšně.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateLift(@PathVariable Long id, @RequestBody Lift lift) {
        if (liftService.getLiftById(id).isPresent()) {
            liftService.updateLift(id, lift);
            return ResponseEntity.ok("Lanovka upravena úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLift(@PathVariable Long id) {
        if (liftService.getLiftById(id).isPresent()) {
            liftService.deleteLift(id);
            return ResponseEntity.ok("Lanovka smazána úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
