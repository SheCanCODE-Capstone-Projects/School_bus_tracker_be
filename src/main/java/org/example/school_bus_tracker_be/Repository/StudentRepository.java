package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Student} entities.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findBySchool(School school);
    long countBySchool(School school);
    
    // Find students by parent phone
    List<Student> findByParentPhone(String parentPhone);
    
    // Find students by parent name
    List<Student> findByParentName(String parentName);
    
    // Find students by parent phone and school
    @Query("SELECT s FROM Student s WHERE s.parentPhone = :parentPhone AND s.school.id = :schoolId")
    List<Student> findByParentPhoneAndSchoolId(@Param("parentPhone") String parentPhone, @Param("schoolId") Long schoolId);
    
    // Find students by parent name and school
    @Query("SELECT s FROM Student s WHERE s.parentName = :parentName AND s.school.id = :schoolId")
    List<Student> findByParentNameAndSchoolId(@Param("parentName") String parentName, @Param("schoolId") Long schoolId);
    
    // Find students assigned to a bus
    @Query("SELECT s FROM Student s WHERE s.assignedBus.id = :busId")
    List<Student> findByAssignedBusId(@Param("busId") Long busId);
    
    // Find students assigned to a bus stop
    @Query("SELECT s FROM Student s WHERE s.busStop.id = :busStopId")
    List<Student> findByBusStopId(@Param("busStopId") Long busStopId);
}
