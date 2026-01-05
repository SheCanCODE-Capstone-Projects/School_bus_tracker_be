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

    // ========================= RELATIONSHIPS =========================

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_bus_id")
    @JsonIgnore
    private Bus assignedBus;

    // ========================= STUDENT INFO =========================

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "student_number", nullable = false, unique = true)
    private String studentNumber;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Integer grade;

    // ========================= METADATA =========================

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ========================= CONSTRUCTORS =========================

    public Student() {}

    public Student(
            School school,
            User parent,
            BusStop busStop,
            String studentName,
            String studentNumber,
            Integer age,
            Integer grade
    ) {
        this.school = school;
        this.parent = parent;
        this.busStop = busStop;
        this.studentName = studentName;
        this.studentNumber = studentNumber;
        this.age = age;
        this.grade = grade;
    }

    // ========================= GETTERS & SETTERS =========================

    public Long getId() {
        return id;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public User getParent() {
        return parent;
    }

    public void setParent(User parent) {
        this.parent = parent;
    }

    public BusStop getBusStop() {
        return busStop;
    }

    public void setBusStop(BusStop busStop) {
        this.busStop = busStop;
    }

    public Bus getAssignedBus() {
        return assignedBus;
    }

    public void setAssignedBus(Bus assignedBus) {
        this.assignedBus = assignedBus;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ========================= LIFECYCLE =========================

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
