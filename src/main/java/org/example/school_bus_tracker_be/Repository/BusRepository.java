package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Bus} entities.
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    List<Bus> findBySchool(School school);
    long countBySchool(School school);
}
