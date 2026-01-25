package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotBlank;

public class UpdateBusStatusRequest {
    @NotBlank(message = "Status is required")
    private String status;

    public UpdateBusStatusRequest() {}

    public UpdateBusStatusRequest(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
