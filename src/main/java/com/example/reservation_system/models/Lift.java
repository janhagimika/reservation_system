package com.example.reservation_system.models;

import jakarta.persistence.*;

@Entity
@Table(name = "lift", indexes = {
                @Index(name = "idx_lift_service_id", columnList = "service_id")
        }
)
public class Lift extends Serv {

    private int capacity;
    private String status;

    // Default Constructor
    public Lift() {}

    // Constructor matching the parameters being used in the code
    public Lift(String serviceName, int capacity, String status) {
        super(serviceName); // Set the inherited attribute from Serv
        this.capacity = capacity;
        this.status = status;
    }

    // Getters and Setters
    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}