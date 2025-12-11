package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentNumber(String studentNumber);
/**
 * Repository for {@link Student} entities.
 */




}
