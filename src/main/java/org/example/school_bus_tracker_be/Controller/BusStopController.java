package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Dtos.bus.BusStopRequest;
import org.example.school_bus_tracker_be.Dtos.bus.BusStopResponse;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bus-stops")
public class BusStopController {

    private final BusStopRepository busStopRepository;
    private final SchoolRepository schoolRepository;

    public BusStopController(BusStopRepository busStopRepository, SchoolRepository schoolRepository) {
        this.busStopRepository = busStopRepository;
        this.schoolRepository = schoolRepository;
    }

    @GetMapping
    public ResponseEntity<List<BusStopResponse>> list() {
        List<BusStop> stops = busStopRepository.findAll();
        List<BusStopResponse> resp = stops.stream()
                .map(s -> new BusStopResponse(s.getId(), s.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(resp);
    }

    @PostMapping
    public ResponseEntity<BusStopResponse> create(@Valid @RequestBody BusStopRequest req) {
        School school = null;
        if (req.getSchoolId() != null) {
            school = schoolRepository.findById(req.getSchoolId())
                    .orElseThrow(() -> new RuntimeException("School not found"));
        } else {
            List<School> schools = schoolRepository.findAll();
            if (schools.isEmpty()) throw new RuntimeException("No school available to associate with bus stop");
            school = schools.get(0);
        }

        BusStop stop = new BusStop(school, req.getName(), req.getLatitude(), req.getLongitude());
        stop = busStopRepository.save(stop);
        return ResponseEntity.ok(new BusStopResponse(stop.getId(), stop.getName()));
    }
}
