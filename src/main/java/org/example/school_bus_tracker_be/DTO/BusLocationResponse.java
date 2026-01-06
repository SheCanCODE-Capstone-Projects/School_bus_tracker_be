package org.example.school_bus_tracker_be.DTO;

public class BusLocationResponse {
    
    private double latitude;
    private double longitude;
    private double speed;
    private String status;

    public BusLocationResponse() {}

    public BusLocationResponse(double latitude, double longitude, double speed, String status) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.status = status;
    }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}