package com.example.reservation_system.models;

public class Serv {
    private Long id;
    private String serviceName;
    private String availability;

    // Konstruktor
    public Serv(Long id, String serviceName) {
        this.id = id;
        this.serviceName = serviceName;
    }

    // Gettery a Settery
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

}
