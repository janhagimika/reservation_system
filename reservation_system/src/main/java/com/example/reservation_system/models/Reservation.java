package com.example.reservation_system.models;

import java.time.LocalDateTime;

public class Reservation {
    private Long id;
    private User user;
    private Serv service;
    private String detail;
    private LocalDateTime startTime;  // Začátek rezervace
    private LocalDateTime endTime;    // Konec rezervace

    public Reservation(Long id, User user, Serv service, String detail, LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.detail = detail;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
