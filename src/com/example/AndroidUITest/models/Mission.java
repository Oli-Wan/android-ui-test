package com.example.AndroidUITest.models;

public class Mission {
    private long id;
    private String observation;
    private String type;
    private String vehicle;
    private String responsible;


    public Mission(long id, String observation) {
        this.id = id;
        this.observation = observation;
    }

    public Mission() {
    }

    public long getId() {
        return id;
    }

    public String getObservation() {
        return observation;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }
}
