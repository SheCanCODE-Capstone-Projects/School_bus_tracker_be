package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.BusStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BusStopRepository extends JpaRepository<BusStop, Long> {
    List<BusStop> findBySchoolId(Long schoolId);
}