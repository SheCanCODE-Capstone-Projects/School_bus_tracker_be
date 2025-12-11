package org.example.school_bus_tracker_be.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    @JsonIgnore
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    @JsonIgnore
    private User parent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_stop_id")
    @JsonIgnore
    private BusStop busStop;

    @Column(nullable = false)
    private String name;

    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer grade;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Student() {}

    public Student(School school, User parent, BusStop busStop, String name, String studentNumber, Integer age, Integer grade) {
        this.school = school;
        this.parent = parent;
        this.busStop = busStop;
        this.name = name;
        this.studentNumber = studentNumber;
        this.age = age;
        this.grade = grade;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public School getSchool() { return school; }
    public void setSchool(School school) { this.school = school; }

    public User getParent() { return parent; }
    public void setParent(User parent) { this.parent = parent; }

    public BusStop getBusStop() { return busStop; }
    public void setBusStop(BusStop busStop) { this.busStop = busStop; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}
