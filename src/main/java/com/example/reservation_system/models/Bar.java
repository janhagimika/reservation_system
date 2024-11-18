package com.example.reservation_system.models;

import jakarta.persistence.*;

@Entity
public class Bar extends Serv {

    private String cuisineType;
    private int capacity;
    private String location;

    // Default Constructor
    public Bar() {}

    // Constructor matching the parameters used in mapRowToBar
    public Bar(String serviceName, String cuisineType, int capacity, String location) {
        super(serviceName); // Set inherited attribute from Serv
        this.cuisineType = cuisineType;
        this.capacity = capacity;
        this.location = location;
    }

    // Getters and Setters
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}