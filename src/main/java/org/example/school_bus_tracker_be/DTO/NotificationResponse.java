package org.example.school_bus_tracker_be.DTO;

public class NotificationResponse {
    
    private Long id;
    private String title;
    private String message;
    private String type;
    private boolean isRead;

    public NotificationResponse() {}

    public NotificationResponse(Long id, String title, String message, String type, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}