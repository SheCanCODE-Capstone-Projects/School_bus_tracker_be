package org.example.school_bus_tracker_be.DTO;

public class SimpleApiResponse {
    private String message;
    private boolean success;

    public SimpleApiResponse() {}

    public SimpleApiResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public static SimpleApiResponse success(String message) {
        return new SimpleApiResponse(message, true);
    }

    public static SimpleApiResponse error(String message) {
        return new SimpleApiResponse(message, false);
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
}
