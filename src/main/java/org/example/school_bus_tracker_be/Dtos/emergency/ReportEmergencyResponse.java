package org.example.school_bus_tracker_be.Dtos.emergency;

public class ReportEmergencyResponse {
    private String message;
    private Long emergencyId;

    public ReportEmergencyResponse() {}

    public ReportEmergencyResponse(String message, Long emergencyId) {
        this.message = message;
        this.emergencyId = emergencyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getEmergencyId() {
        return emergencyId;
    }

    public void setEmergencyId(Long emergencyId) {
        this.emergencyId = emergencyId;
    }
}
