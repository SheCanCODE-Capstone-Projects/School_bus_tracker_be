package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Emergency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Emergency} entities.
 */
@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
}
