package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link School} entities.
 */
@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
}
