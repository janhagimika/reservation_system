package com.example.reservation_system.services;

import com.example.reservation_system.models.Serv;
import com.example.reservation_system.repositories.ServRepository;
import jakarta.transaction.Transactional;
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

    public Serv createService(Serv serv) {
        return servRepository.save(serv);
    }

    @Transactional
    public void updateService(Long serviceId, Serv updatedService) {
        Serv service = servRepository.findByServiceId(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service not found"));

        // Update service details
        service.setServiceName (updatedService.getServiceName());

        // Save and return updated service
        servRepository.save(service);
    }


    public void deleteService(Long id) {
        servRepository.deleteById(id);
    }

    public void deleteAllServices() {
        servRepository.deleteAllServs();
    }
}
