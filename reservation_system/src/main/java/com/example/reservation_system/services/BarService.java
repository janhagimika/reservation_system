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

    public int createBar(Bar bar) {
        return barRepository.save(bar);
    }

    public int updateBar(Long id, Bar bar) {
        Optional<Bar> existingBar = barRepository.findById(id);
        if (existingBar.isPresent()) {
            bar.setId(id);
            return barRepository.update(bar);
        } else {
            return 0;
        }
    }

    public int deleteBar(Long id) {
        return barRepository.deleteById(id);
    }
}
