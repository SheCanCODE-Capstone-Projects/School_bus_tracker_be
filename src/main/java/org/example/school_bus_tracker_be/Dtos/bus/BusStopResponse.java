package org.example.school_bus_tracker_be.Dtos.bus;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Bus stop information")
public class BusStopResponse {

    @Schema(description = "Bus stop id", example = "1")
    private Long id;

    @Schema(description = "Bus stop name", example = "Main Street Stop")
    private String name;

    public BusStopResponse() {}

    public BusStopResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
