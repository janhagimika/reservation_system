package com.example.reservation_system.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation", indexes = {
        @Index(name = "idx_reservation_start_time", columnList = "startTime")
})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "service_id")
    private Serv service;

    // Constructors
    public Reservation() {}

    public Reservation(User user, Serv service, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.service = service;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    // ... (Include getters and setters for all fields)


// Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Serv getService() { return service; }

    public void setService(Serv service) {
        this.service = service;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }}
