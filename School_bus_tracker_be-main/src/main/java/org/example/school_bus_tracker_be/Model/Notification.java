package org.example.school_bus_tracker_be.Model;



import jakarta.persistence.*;

@Entity
@Table(name = "notifications")
public class Notification {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    public Notification() {}

    public Notification(User user, String title, String message, Type type) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }

    public enum Type { INFO, WARNING, EMERGENCY }


}
