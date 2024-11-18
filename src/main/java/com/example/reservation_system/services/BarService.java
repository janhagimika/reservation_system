package com.example.reservation_system.services;

import com.example.reservation_system.models.Bar;
import com.example.reservation_system.repositories.BarRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BarService {

    private final BarRepository barRepository;

    public BarService(BarRepository barRepository) {
        this.barRepository = barRepository;
    }

    public List<Bar> getAllBars() {
        return barRepository.findAll();
    }

    public Optional<Bar> getBarById(Long id) {
        return barRepository.findById(id);
    }

    public Bar createBar(Bar bar) {
        return barRepository.save(bar);
    }

    public void updateBar(Long id, Bar updatedBar) {
        barRepository.findById(id).ifPresent(existingBar -> {
            existingBar.setCuisineType(updatedBar.getCuisineType());
            existingBar.setCapacity(updatedBar.getCapacity());
            existingBar.setLocation(updatedBar.getLocation());
            existingBar.setServiceName(updatedBar.getServiceName());
            barRepository.save(existingBar);
        });
    }

    public void deleteBar(Long id) {
        barRepository.deleteById(id);
    }

    public void deleteAllBars() {
        barRepository.deleteAllBars();
    }
}
