package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.BusLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BusLocationRepository extends JpaRepository<BusLocation, Long> {
    
    List<BusLocation> findByBusIdOrderByRecordedAtDesc(Long busId);
    
    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId ORDER BY bl.recordedAt DESC")
    List<BusLocation> findByBusIdOrdered(@Param("busId") Long busId);
    
    @Query("SELECT bl FROM BusLocation bl WHERE bl.bus.id = :busId AND bl.recordedAt >= :from AND bl.recordedAt <= :to ORDER BY bl.recordedAt DESC")
    List<BusLocation> findByBusIdAndDateRange(@Param("busId") Long busId, 
                                              @Param("from") LocalDateTime from, 
                                              @Param("to") LocalDateTime to);
    
    @Query(value = "SELECT * FROM bus_locations WHERE bus_id = :busId ORDER BY recorded_at DESC LIMIT 1", nativeQuery = true)
    Optional<BusLocation> findLatestByBusId(@Param("busId") Long busId);
}
