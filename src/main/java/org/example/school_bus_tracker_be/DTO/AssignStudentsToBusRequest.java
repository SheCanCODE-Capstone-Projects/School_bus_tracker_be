package org.example.school_bus_tracker_be.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AssignStudentsToBusRequest {
    @NotNull(message = "Bus ID is required")
    private Long busId;
    
    @NotEmpty(message = "At least one student ID is required")
    private List<Long> studentIds;

    public AssignStudentsToBusRequest() {}

    public AssignStudentsToBusRequest(Long busId, List<Long> studentIds) {
        this.busId = busId;
        this.studentIds = studentIds;
    }

    public Long getBusId() {
        return busId;
    }

    public void setBusId(Long busId) {
        this.busId = busId;
    }

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }
}
