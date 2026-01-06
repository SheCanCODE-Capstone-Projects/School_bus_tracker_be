package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bus-stops")
public class BusStopController {

    private final BusStopRepository busStopRepository;

    public BusStopController(BusStopRepository busStopRepository) {
        this.busStopRepository = busStopRepository;
    }

    @PostMapping
    public ResponseEntity<BusStop> createBusStop(@RequestBody BusStop busStop) {
        return ResponseEntity.ok(busStopRepository.save(busStop));
    }

    @GetMapping
    public ResponseEntity<List<BusStop>> getAllBusStops() {
        return ResponseEntity.ok(busStopRepository.findAll());
    }
}