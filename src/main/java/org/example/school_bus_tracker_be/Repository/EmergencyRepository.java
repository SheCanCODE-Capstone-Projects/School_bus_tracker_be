package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.Emergency;
import org.example.school_bus_tracker_be.Model.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for {@link Emergency} entities.
 */
@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {
    
    List<Emergency> findByBusId(Long busId);
    
    List<Emergency> findByStatus(Emergency.Status status);
    
    List<Emergency> findByBusIdAndStatus(Long busId, Emergency.Status status);
    
    @Query("SELECT e FROM Emergency e WHERE e.bus.id IN :busIds")
    List<Emergency> findByBusIds(@Param("busIds") List<Long> busIds);
    
    @Query("SELECT e FROM Emergency e WHERE e.bus.id IN :busIds AND e.status = :status")
    List<Emergency> findByBusIdsAndStatus(@Param("busIds") List<Long> busIds, @Param("status") Emergency.Status status);
    
    @Query("SELECT COUNT(e) FROM Emergency e WHERE e.status = org.example.school_bus_tracker_be.Model.Emergency.Status.ACTIVE")
    Long countActive();
    
    @Query("SELECT COUNT(e) FROM Emergency e WHERE e.status = org.example.school_bus_tracker_be.Model.Emergency.Status.RESOLVED AND e.resolvedAt >= :startOfDay AND e.resolvedAt < :endOfDay")
    Long countResolvedToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
    
    @Query("SELECT COUNT(e) FROM Emergency e")
    Long countTotal();
    
    @Query("SELECT e FROM Emergency e WHERE e.bus.id = :busId AND e.status = org.example.school_bus_tracker_be.Model.Emergency.Status.ACTIVE")
    List<Emergency> findActiveByBusId(@Param("busId") Long busId);
}
