package com.example.reservation_system.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "serv", indexes = {
                @Index(name = "idx_service_name", columnList = "serviceName") // Index on serviceName for faster queries by serviceName
        }
)
public class Serv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceId;
    @Column(nullable = false)
    private String serviceName;

    // Constructors
    public Serv() {}

    public Serv(String serviceName) {
        this.serviceName = serviceName;
    }

    public Serv(long serviceId, String serviceName) {
        this.serviceName = serviceName;
        this.serviceId = serviceId;
    }

    // Getters and Setters
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}