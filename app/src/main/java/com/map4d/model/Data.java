package com.map4d.model;

import java.util.List;

public class Data {

    private int id;
    private String status;
    private Double latitude;
    private Double longitude;
    private String time;

    public Data(int id, String status, Double latitude, Double longitude, String time ) {
        this.id = id;
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}