package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Bus} entities.
 */
@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
}
