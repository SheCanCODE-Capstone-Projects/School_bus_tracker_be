package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

    private final SchoolRepository schoolRepository;

    public SchoolController(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @PostMapping
    public ResponseEntity<School> createSchool(@RequestBody School school) {
        return ResponseEntity.ok(schoolRepository.save(school));
    }
}
