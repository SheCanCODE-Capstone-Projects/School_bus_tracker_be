package org.example.school_bus_tracker_be.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many users can belong to one school
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnore
    private School school;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(name = "home_address")
    private String homeAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public User() {}

    public User(School school, String name, String email, String password, String phone, String homeAddress, Role role) {
        this.school = school;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.homeAddress = homeAddress;
        this.role = role;
    }

    // Getters
    public Long getId() { return id; }
    public School getSchool() { return school; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getHomeAddress() { return homeAddress; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setSchool(School school) { this.school = school; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }
    public void setRole(Role role) { this.role = role; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Role Enum
    /**
     * The different roles a user in the system can have.
     *
     * <p>
     * Spring Security expects roles to be prefixed with {@code ROLE_} when
     * constructing {@link org.springframework.security.core.GrantedAuthority}
     * instances. When building authentication tokens in the {@code JwtTokenProvider}
     * we therefore prefix each enum name with {@code ROLE_}. Adding new roles here
     * automatically exposes them for use in the security configuration.
     * </p>
     */
    public enum Role {
        /**
         * A user that can administer the entire system. Administrators can manage
         * drivers, parents and buses.
         */
        ADMIN,
        /**
         * A user that is registered as a driver. Drivers can update bus
         * locations and trigger emergencies.
         */
        DRIVER,
        /**
         * A user that is registered as a parent. Parents can register their
         * children and subscribe to bus notifications.
         */
        PARENT,
        /**
         * A user that is registered as a student. Students are primarily used
         * internally and do not authenticate directly.
         */
        STUDENT
    }


}
