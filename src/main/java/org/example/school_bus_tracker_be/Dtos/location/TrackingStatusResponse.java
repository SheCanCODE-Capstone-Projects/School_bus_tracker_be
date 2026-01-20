package org.example.school_bus_tracker_be.Dtos.location;

import java.time.LocalDateTime;

public class TrackingStatusResponse {
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime stoppedAt;
    private String driverName;
    private String busNumber;

    public TrackingStatusResponse() {}

    public TrackingStatusResponse(String status, LocalDateTime startedAt, LocalDateTime stoppedAt, 
                                  String driverName, String busNumber) {
        this.status = status;
        this.startedAt = startedAt;
        this.stoppedAt = stoppedAt;
        this.driverName = driverName;
        this.busNumber = busNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getStoppedAt() {
        return stoppedAt;
    }

    public void setStoppedAt(LocalDateTime stoppedAt) {
        this.stoppedAt = stoppedAt;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }
}
