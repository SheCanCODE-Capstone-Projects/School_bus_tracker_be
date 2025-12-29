package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Student} entities.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchool(School school);
    long countBySchool(School school);
}
