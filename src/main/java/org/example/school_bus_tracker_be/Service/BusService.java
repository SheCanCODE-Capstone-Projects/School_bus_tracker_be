package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.BusRequest;
import org.example.school_bus_tracker_be.DTO.BusResponse;

import java.util.List;

public interface BusService {
    
    BusResponse createBus(BusRequest request);
    List<BusResponse> getAllBuses();
    BusResponse getBusById(Long id);
    BusResponse updateBus(Long id, BusRequest request);
    void deleteBus(Long id);
}