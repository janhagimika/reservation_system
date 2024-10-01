package com.example.reservation_system.models;

import java.util.Optional;

public class Lift extends Serv {
    private int capacity;
    private String status;
    private Serv service;

    // Konstruktor
    public Lift(Long id, String name, Serv service, int capacity, String status) {
        super(id, name);
        this.service = service;
        this.capacity = capacity;
        this.status = status;
    }

    // Gettery a Settery
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Serv getService() { return service; }

    public void setService(Serv service) {
        this.service = service;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

