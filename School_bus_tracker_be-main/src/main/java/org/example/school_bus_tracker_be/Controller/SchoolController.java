package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.school.SchoolRequest;
import org.example.school_bus_tracker_be.Dtos.school.SchoolResponse;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schools")
@Tag(name = "School Management", description = "APIs for managing schools")
public class SchoolController {

    private final SchoolRepository schoolRepository;

    public SchoolController(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    @GetMapping
    @Operation(summary = "Get all schools", description = "Retrieve all schools with their generated IDs")
    public ResponseEntity<ApiResponse<List<SchoolResponse>>> getAllSchools() {
        try {
            List<SchoolResponse> schools = schoolRepository.findAll().stream()
                .map(school -> new SchoolResponse(
                    school.getId(),
                    school.getName(),
                    school.getAddress(),
                    school.getPhone()
                ))
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Schools retrieved successfully", schools));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create new school", description = "Create a new school with auto-generated ID")
    public ResponseEntity<ApiResponse<SchoolResponse>> createSchool(@Valid @RequestBody SchoolRequest request) {
        try {
            School school = new School();
            school.setName(request.getName());
            school.setAddress(request.getAddress());
            school.setPhone(request.getPhone());
            
            School savedSchool = schoolRepository.save(school);
            
            SchoolResponse response = new SchoolResponse(
                savedSchool.getId(),
                savedSchool.getName(),
                savedSchool.getAddress(),
                savedSchool.getPhone()
            );
            
            return ResponseEntity.ok(ApiResponse.success("School created successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get school by ID", description = "Retrieve a specific school by its ID")
    public ResponseEntity<ApiResponse<SchoolResponse>> getSchoolById(@PathVariable Long id) {
        try {
            School school = schoolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("School not found"));
            
            SchoolResponse response = new SchoolResponse(
                school.getId(),
                school.getName(),
                school.getAddress(),
                school.getPhone()
            );
            
            return ResponseEntity.ok(ApiResponse.success("School retrieved successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}