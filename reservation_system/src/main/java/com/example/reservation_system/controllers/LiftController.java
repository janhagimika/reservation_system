package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.services.LiftService;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<String> createLift(@RequestBody Lift lift) {
        liftService.createLift(lift);
        return ResponseEntity.ok("Lanovka vytvořena úspěšně.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateLift(@PathVariable Long id, @RequestBody Lift lift) {
        if (liftService.updateLift(id, lift) > 0) {
            return ResponseEntity.ok("Lanovka upravena úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLift(@PathVariable Long id) {
        if (liftService.deleteLift(id) > 0) {
            return ResponseEntity.ok("Lanovka smazána úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }
}
