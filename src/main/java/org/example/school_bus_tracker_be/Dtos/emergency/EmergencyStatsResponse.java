package org.example.school_bus_tracker_be.Dtos.emergency;

public class EmergencyStatsResponse {
    private Long active;
    private Long resolvedToday;
    private Long total;

    public EmergencyStatsResponse() {}

    public EmergencyStatsResponse(Long active, Long resolvedToday, Long total) {
        this.active = active;
        this.resolvedToday = resolvedToday;
        this.total = total;
    }

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }

    public Long getResolvedToday() {
        return resolvedToday;
    }

    public void setResolvedToday(Long resolvedToday) {
        this.resolvedToday = resolvedToday;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
