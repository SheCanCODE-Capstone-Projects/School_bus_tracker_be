package org.example.school_bus_tracker_be.Dtos.emergency;

public class ResolveEmergencyRequest {
    private String resolutionNotes;
    private Boolean notifyParents = false;

    public ResolveEmergencyRequest() {}

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public Boolean getNotifyParents() {
        return notifyParents;
    }

    public void setNotifyParents(Boolean notifyParents) {
        this.notifyParents = notifyParents;
    }
}
