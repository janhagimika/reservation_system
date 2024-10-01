package com.example.reservation_system.services;

import com.example.reservation_system.models.Lift;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.repositories.LiftRepository;
import com.example.reservation_system.repositories.ServRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LiftService {

    private final LiftRepository liftRepository;
    private final ServRepository servRepository;

    public LiftService(LiftRepository liftRepository, ServRepository servRepository) {
        this.liftRepository = liftRepository;
        this.servRepository = servRepository;
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
            lift.setServiceId(id);
            return liftRepository.update(lift);
        } else {
            return 0;
        }
    }

    public int deleteLift(Long id) {
        return liftRepository.deleteById(id);
    }
}
