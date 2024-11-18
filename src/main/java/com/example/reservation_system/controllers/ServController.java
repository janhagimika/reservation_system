package com.example.reservation_system.controllers;

import com.example.reservation_system.models.Serv;
import com.example.reservation_system.services.ServService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @CrossOrigin(origins = "https://localhost:3000")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping
    public ResponseEntity<Serv> createService(@RequestBody Serv serv) {
        System.out.println("Received POST request to create service: " + serv);
        Serv createdServ = servService.createService(serv);
        return ResponseEntity.ok(createdServ);
    }

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PutMapping("/{id}")
    public ResponseEntity<String> updateService(@PathVariable Long id, @RequestBody Serv serv) {
        if (servService.getServiceById(id).isPresent()) {
            servService.updateService(id, serv);
            return ResponseEntity.ok("Servis upraven úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        if (servService.getServiceById(id).isPresent()) {
            servService.deleteService(id);
            return ResponseEntity.ok("Servis smazán úspěšně.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
