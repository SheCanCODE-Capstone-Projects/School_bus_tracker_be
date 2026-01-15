package org.example.school_bus_tracker_be.Dtos.bus;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to create a bus stop")
public class BusStopRequest {

    @NotBlank(message = "Bus stop name is required")
    @Schema(description = "Bus stop name", example = "Main Street Stop", required = true)
    private String name;

    @NotNull(message = "Latitude is required")
    @Schema(description = "Latitude coordinate", example = "40.7128", required = true)
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @Schema(description = "Longitude coordinate", example = "-74.0060", required = true)
    private Double longitude;

    @JsonProperty("school_id")
    @NotNull(message = "School ID is required")
    @Schema(description = "School ID", example = "1", required = true)
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
