package com.example.reservation_system.models;

public class Room extends Serv {
    private int capacity;
    private double pricePerNight;
    private Serv service;

    // Konstruktor
    public Room(Long id, String name, Serv service, int capacity, double pricePerNight) {
        super(id, name);
        this.service = service;
        this.capacity = capacity;
        this.pricePerNight = pricePerNight;
    }

    // Gettery a Settery
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

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}
