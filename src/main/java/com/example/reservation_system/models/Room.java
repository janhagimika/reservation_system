package com.example.reservation_system.models;

import jakarta.persistence.*;

@Entity
@Table(name = "room", indexes = {
                @Index(name = "idx_room_service_id", columnList = "service_id")
        }
)
public class Room extends Serv {

    private String type;
    private int capacity;
    private double pricePerNight;

    // Constructors
    public Room() {}

    public Room(String serviceName, String type, int capacity, double pricePerNight) {
        super(serviceName); // Setting inherited attribute from Serv
        this.type = type;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

}