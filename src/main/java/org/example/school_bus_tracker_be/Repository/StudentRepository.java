package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentNumber(String studentNumber);
    List<Student> findBySchool(School school);
    long countBySchool(School school);
}
