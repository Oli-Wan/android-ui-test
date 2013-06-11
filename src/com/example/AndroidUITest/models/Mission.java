package com.example.AndroidUITest.models;

public class Mission {
    private long id;
    private String observation;

    public Mission(long id, String observation) {
        this.id = id;
        this.observation = observation;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
