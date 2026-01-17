package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Student;
<<<<<<< Updated upstream
import org.example.school_bus_tracker_be.Model.School;
=======
import org.example.school_bus_tracker_be.Model.User;
>>>>>>> Stashed changes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

<<<<<<< Updated upstream
import java.util.List;

/**
 * Repository for {@link Student} entities.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchool(School school);
    long countBySchool(School school);
=======
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByStudentNumber(String studentNumber);
    List<Student> findByParent(User parent);
    List<Student> findByParentId(Long parentId);
>>>>>>> Stashed changes
}
