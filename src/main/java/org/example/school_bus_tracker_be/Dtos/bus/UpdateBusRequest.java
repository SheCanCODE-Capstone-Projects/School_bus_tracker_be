package org.example.school_bus_tracker_be.Dtos.bus;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request body for PUT /api/buses/{id} (Edit Bus form).
 * All fields optional; only provided fields are updated.
 * Frontend sends: busName, busNumber, school (schoolId), assign driver (assignedDriverId), status.
 */
@Schema(description = "Request to update a bus (Edit Bus form)")
public class UpdateBusRequest {

    @Schema(description = "Bus name", example = "School Bus D")
    private String busName;

    @Schema(description = "Bus number", example = "SB-004")
    private String busNumber;

    @JsonProperty("school_id")
    @Schema(description = "School ID to associate the bus with", example = "1")
    private Long schoolId;

    @JsonProperty("assigned_driver_id")
    @Schema(description = "Driver ID (drivers table) to assign to this bus; omit to leave unchanged")
    private Long assignedDriverId;

    @JsonProperty("unassign_driver")
    @Schema(description = "If true, remove the current driver assignment from this bus")
    private Boolean unassignDriver;

    @Schema(description = "Status: ACTIVE, INACTIVE, MAINTENANCE, ON_ROUTE (case-insensitive)", example = "ACTIVE")
    private String status;

    @Schema(description = "Bus capacity", example = "30")
    private Integer capacity;

    @Schema(description = "Route description", example = "Route 1")
    private String route;

    public UpdateBusRequest() {}

    public String getBusName() { return busName; }
    public void setBusName(String busName) { this.busName = busName; }
    public String getBusNumber() { return busNumber; }
    public void setBusNumber(String busNumber) { this.busNumber = busNumber; }
    public Long getSchoolId() { return schoolId; }
    public void setSchoolId(Long schoolId) { this.schoolId = schoolId; }
    public Long getAssignedDriverId() { return assignedDriverId; }
    public void setAssignedDriverId(Long assignedDriverId) { this.assignedDriverId = assignedDriverId; }
    public Boolean getUnassignDriver() { return unassignDriver; }
    public void setUnassignDriver(Boolean unassignDriver) { this.unassignDriver = unassignDriver; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }
}
