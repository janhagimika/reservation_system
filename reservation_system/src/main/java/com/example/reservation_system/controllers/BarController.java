package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Bar;
import com.example.reservation_system.services.BarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bars")
public class BarController {

    private final BarService barService;

    public BarController(BarService barService) {
        this.barService = barService;
    }

    @GetMapping
    public List<Bar> getAllBars() {
        return barService.getAllBars();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bar> getBarById(@PathVariable Long id) {
        Optional<Bar> bar = barService.getBarById(id);
        return bar.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<String> createBar(@RequestBody Bar bar) {
        barService.createBar(bar);
        return ResponseEntity.ok("Bar vytvořen úspěšně.");
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBar(@PathVariable Long id, @RequestBody Bar bar) {
        if (barService.updateBar(id, bar) > 0) {
            return ResponseEntity.ok("Bar upraven úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBar(@PathVariable Long id) {
        if (barService.deleteBar(id) > 0) {
            return ResponseEntity.ok("Bar smazán úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }
}
