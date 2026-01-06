package org.example.school_bus_tracker_be.Dtos.bus;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BusStopRequest {

    @NotBlank(message = "Bus stop name is required")
    private String name;

    @NotNull(message = "Latitude is required")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    private Double longitude;

    @JsonProperty("school_id")
    private Long schoolId;

    public BusStopRequest() {}

    public BusStopRequest(String name, Double latitude, Double longitude, Long schoolId) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.schoolId = schoolId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
}