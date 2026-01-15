package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    Optional<Driver> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    boolean existsByLicenseNumber(String licenseNumber);
    
    List<Driver> findBySchool(School school);
    
    List<Driver> findBySchoolId(Long schoolId);
    
    Optional<Driver> findByAssignedBusId(Long busId);
}