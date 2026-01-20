package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.BusTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusTrackingRepository extends JpaRepository<BusTracking, Long> {
    
    Optional<BusTracking> findByBusIdAndStatus(Long busId, BusTracking.Status status);
    
    Optional<BusTracking> findByDriverIdAndStatus(Long driverId, BusTracking.Status status);
    
    List<BusTracking> findByBusId(Long busId);
    
    List<BusTracking> findByDriverId(Long driverId);
    
    @Query("SELECT bt FROM BusTracking bt WHERE bt.bus.id = :busId AND bt.status = org.example.school_bus_tracker_be.Model.BusTracking.Status.ACTIVE")
    Optional<BusTracking> findActiveByBusId(@Param("busId") Long busId);
    
    @Query("SELECT bt FROM BusTracking bt WHERE bt.driver.id = :driverId AND bt.status = org.example.school_bus_tracker_be.Model.BusTracking.Status.ACTIVE")
    Optional<BusTracking> findActiveByDriverId(@Param("driverId") Long driverId);
}
