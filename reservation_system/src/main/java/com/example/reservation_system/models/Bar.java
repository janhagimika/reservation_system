package com.example.reservation_system.models;

public class Bar extends Serv {
    private String cuisineType;
    private Serv service;
    private int capacity;
    private String location;

    // Konstruktor
    public Bar(Long id, String name, Serv service, String cuisineType, int capacity, String location) {
        super(id, name);
        this.service = service;
        this.cuisineType = cuisineType;
        this.capacity = capacity;
        this.location = location;
    }

    // Gettery a Settery
    public String getCuisineType() {
        return cuisineType;
    }

    public void setCuisineType(String cuisineType) {
        this.cuisineType = cuisineType;
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

