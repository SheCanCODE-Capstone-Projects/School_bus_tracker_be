package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.LocationPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository for {@link LocationPoint} entities.
 */
@Repository
public interface LocationPointRepository extends JpaRepository<LocationPoint, Long> {
    Optional<LocationPoint> findTopByBusIdOrderByTimestampDesc(Long busId);
}
