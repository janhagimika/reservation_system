package com.example.reservation_system.services;

import com.example.reservation_system.models.Serv;
import com.example.reservation_system.repositories.ServRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServService {

    private final ServRepository servRepository;

    public ServService(ServRepository servRepository) {
        this.servRepository = servRepository;
    }

    public List<Serv> getAllServices() {
        return servRepository.findAll();
    }

    public Optional<Serv> getServiceById(Long id) {
        return servRepository.findById(id);
    }

    public int createService(Serv serv) {
        return servRepository.save(serv);
    }

    public int updateService(Long id, Serv serv) {
        Optional<Serv> existingService = servRepository.findById(id);
        if (existingService.isPresent()) {
            serv.setId(id);
            return servRepository.update(serv);
        } else {
            return 0;
        }
    }

    public int deleteService(Long id) {
        return servRepository.deleteById(id);
    }
}
