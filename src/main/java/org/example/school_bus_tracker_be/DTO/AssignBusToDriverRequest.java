package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotNull;

public class AssignBusToDriverRequest {
    @NotNull(message = "Driver ID is required")
    private Long driverId;
    
    @NotNull(message = "Bus ID is required")
    private Long busId;

    public AssignBusToDriverRequest() {}

    public AssignBusToDriverRequest(Long driverId, Long busId) {
        this.driverId = driverId;
        this.busId = busId;
    }

    public Long getDriverId() {
        return driverId;
    }

    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }
}
