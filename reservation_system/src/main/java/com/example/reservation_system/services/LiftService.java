package com.example.reservation_system.services;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.repositories.LiftRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public int createLift(Lift lift) {
        return liftRepository.save(lift);
    }

    public int updateLift(Long id, Lift lift) {
        Optional<Lift> existingLift = liftRepository.findById(id);
        if (existingLift.isPresent()) {
            lift.setId(id);
            return liftRepository.update(lift);
        } else {
            return 0;
        }
    }

    public int deleteLift(Long id) {
        return liftRepository.deleteById(id);
    }
}
