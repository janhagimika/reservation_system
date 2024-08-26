package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Serv;
import com.example.reservation_system.services.ServService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/services")
public class ServController {

    private final ServService servService;

    public ServController(ServService servService) {
        this.servService = servService;
    }

    @GetMapping
    public List<Serv> getAllServices() {
        return servService.getAllServices();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Serv> getServiceById(@PathVariable Long id) {
        Optional<Serv> serv = servService.getServiceById(id);
        return serv.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createService(@RequestBody Serv serv) {
        servService.createService(serv);
        return ResponseEntity.ok("Servis vytvořen úspěšně.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateService(@PathVariable Long id, @RequestBody Serv serv) {
        if (servService.updateService(id, serv) > 0) {
            return ResponseEntity.ok("Servis upraven úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        if (servService.deleteService(id) > 0) {
            return ResponseEntity.ok("Servis smazán úspěšně.");
        }
        return ResponseEntity.notFound().build();
    }
}
