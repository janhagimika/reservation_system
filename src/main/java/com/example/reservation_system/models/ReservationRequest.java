package com.example.reservation_system.models;

public class ReservationRequest {
    private Reservation reservation;
    private String serviceType;


    //class for getting request for reservation logic and going through business hours
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getServiceType() {
        return serviceType;
    }



    // getters and setters
}
