package com.example.reservation_system.models;

import java.time.LocalDateTime;

public class AvailableTimeRequest {

    private Long serviceId;
    private String serviceType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    // Getter and Setter for serviceId
    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    // Getter and Setter for serviceType
    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    // Getter and Setter for startTime
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    // Getter and Setter for endTime
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

