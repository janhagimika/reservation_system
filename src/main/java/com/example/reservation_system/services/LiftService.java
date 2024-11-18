package com.example.reservation_system.services;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.repositories.LiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LiftService {

    private final LiftRepository liftRepository;

    public LiftService(LiftRepository liftRepository) {
        this.liftRepository = liftRepository;
    }

    public List<Lift> getAllLifts() {
        return liftRepository.findAll();
    }

    public Optional<Lift> getLiftById(Long id) {
        return liftRepository.findById(id);
    }

    public Lift createLift(Lift lift) {
        return liftRepository.save(lift);
    }

    public void updateLift(Long id, Lift updatedLift) {
        liftRepository.findById(id).ifPresent(existingLift -> {
            existingLift.setCapacity(updatedLift.getCapacity());
            existingLift.setStatus(updatedLift.getStatus());
            existingLift.setServiceName(updatedLift.getServiceName());
            liftRepository.save(existingLift);
        });
    }

    public void deleteLift(Long id) {
        liftRepository.deleteById(id);
    }

    public void deleteAllLifts() {
        liftRepository.deleteAllLifts();
    }
}
