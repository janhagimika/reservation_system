package com.example.reservation_system.models;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private User user;
    private Serv service;
    private LocalDateTime startTime;  // Začátek rezervace
    private LocalDateTime endTime;    // Konec rezervace

    public Reservation(Long id, User user, Serv service, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Reservation(Serv serv, User user, LocalDateTime startTime, LocalDateTime endTime) {
        this.user = user;
        this.service = serv;
        this.startTime = startTime;
        this.endTime = endTime;
    }


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
