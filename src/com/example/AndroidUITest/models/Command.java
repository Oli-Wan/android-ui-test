package com.example.AndroidUITest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class Command {
    @JsonIgnore
    private Long id;
    private long date;
    private String origin;
    private Map<String, Object> data;
    @JsonIgnore
    private String status;

    public Command() {
        this.id = null;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Command sent at " + date + " from " + origin + " with data " + data + " and status " + status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
