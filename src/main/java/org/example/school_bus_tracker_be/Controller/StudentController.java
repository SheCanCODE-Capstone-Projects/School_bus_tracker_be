package org.example.school_bus_tracker_be.Controller;

import org.example.school_bus_tracker_be.DTO.ApiResponse;
import org.example.school_bus_tracker_be.Dtos.student.StudentRequest;
import org.example.school_bus_tracker_be.Dtos.student.StudentResponse;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "APIs for managing students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final SchoolRepository schoolRepository;
    private final BusRepository busRepository;
    private final BusStopRepository busStopRepository;

    public StudentController(StudentRepository studentRepository, SchoolRepository schoolRepository,
                           BusRepository busRepository, BusStopRepository busStopRepository) {
        this.studentRepository = studentRepository;
        this.schoolRepository = schoolRepository;
        this.busRepository = busRepository;
        this.busStopRepository = busStopRepository;
    }

    @GetMapping
    @Operation(summary = "Get all students", description = "Retrieve all students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {
        try {
            List<StudentResponse> students = studentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
            return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", students));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID", description = "Retrieve a specific student by their ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        try {
            Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", convertToResponse(student)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping
    @Operation(summary = "Create new student", description = "Create a new student")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody StudentRequest request,
            @RequestParam Long schoolId) {
        try {
            School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new RuntimeException("School not found"));
            
            Student student = new Student();
            student.setSchool(school);
            student.setStudentName(request.getStudentName());
            student.setAge(request.getAge());
            student.setParentName(request.getParentName());
            student.setParentPhone(request.getParentPhone());
            student.setAddress(request.getAddress());
            
            if (request.getBusStopId() != null) {
                BusStop busStop = busStopRepository.findById(request.getBusStopId())
                    .orElseThrow(() -> new RuntimeException("Bus stop not found"));
                student.setBusStop(busStop);
            }
            
            if (request.getAssignedBusId() != null) {
                Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
                student.setAssignedBus(bus);
            }
            
            Student savedStudent = studentRepository.save(student);
            return ResponseEntity.ok(ApiResponse.success("Student created successfully", convertToResponse(savedStudent)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update student", description = "Update an existing student")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {
        try {
            Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            
            student.setStudentName(request.getStudentName());
            student.setAge(request.getAge());
            student.setParentName(request.getParentName());
            student.setParentPhone(request.getParentPhone());
            student.setAddress(request.getAddress());
            
            if (request.getBusStopId() != null) {
                BusStop busStop = busStopRepository.findById(request.getBusStopId())
                    .orElseThrow(() -> new RuntimeException("Bus stop not found"));
                student.setBusStop(busStop);
            } else {
                student.setBusStop(null);
            }
            
            if (request.getAssignedBusId() != null) {
                Bus bus = busRepository.findById(request.getAssignedBusId())
                    .orElseThrow(() -> new RuntimeException("Bus not found"));
                student.setAssignedBus(bus);
            } else {
                student.setAssignedBus(null);
            }
            
            Student updatedStudent = studentRepository.save(student);
            return ResponseEntity.ok(ApiResponse.success("Student updated successfully", convertToResponse(updatedStudent)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete student", description = "Delete a student by their ID")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable Long id) {
        try {
            Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
            studentRepository.delete(student);
            return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private StudentResponse convertToResponse(Student student) {
        StudentResponse.BusStopInfo busStopInfo = null;
        if (student.getBusStop() != null) {
            busStopInfo = new StudentResponse.BusStopInfo(
                student.getBusStop().getId(),
                student.getBusStop().getName(),
                student.getBusStop().getLatitude() + ", " + student.getBusStop().getLongitude()
            );
        }
        
        StudentResponse.AssignedBusInfo busInfo = null;
        if (student.getAssignedBus() != null) {
            busInfo = new StudentResponse.AssignedBusInfo(
                student.getAssignedBus().getId(),
                student.getAssignedBus().getBusName(),
                student.getAssignedBus().getBusNumber()
            );
        }
        
        StudentResponse response = new StudentResponse(
            student.getId(),
            student.getStudentName(),
            student.getAge(),
            student.getParentName(),
            student.getParentPhone(),
            student.getAddress(),
            busStopInfo,
            busInfo
        );
        response.setSchoolId(student.getSchool().getId());
        return response;
    }
}
