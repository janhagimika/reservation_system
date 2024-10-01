package com.example.reservation_system.models;

public class Bar extends Serv {
    private String cuisine_type;
    private Serv service;
    private int capacity;
    private String location;

    // Konstruktor
    public Bar(Long id, String name, Serv service, String cuisine_type, int capacity, String location) {
        super(id, name);
        this.service = service;
        this.cuisine_type = cuisine_type;
        this.capacity = capacity;
        this.location = location;
    }

    // Gettery a Settery
    public String getCuisineType() {
        return cuisine_type;
    }

    public void setCuisineType(String cuisine_type) {
        this.cuisine_type = cuisine_type;
    }

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

