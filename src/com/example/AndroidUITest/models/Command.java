package com.example.AndroidUITest.models;

public class Command {
    private long date;
    private String origin;
    private String data;

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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Command sent at "+date+" from "+origin+" with data "+data;
    }
}
