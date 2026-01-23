package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.AssignBusToDriverRequest;
import org.example.school_bus_tracker_be.DTO.AssignStudentsToBusRequest;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.Driver;
import org.example.school_bus_tracker_be.Model.Student;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.DriverRepository;
import org.example.school_bus_tracker_be.Repository.StudentRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.BusService;
import org.example.school_bus_tracker_be.Enum.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    public BusServiceImpl(
            BusRepository busRepository,
            DriverRepository driverRepository,
            StudentRepository studentRepository,
            UserRepository userRepository) {
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Bus assignBusToDriver(AssignBusToDriverRequest request, Long adminId) {
        // Verify admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can assign buses to drivers");
        }

        // Get driver from drivers table (driverId is the driver entity ID, not userId)
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found with ID: " + request.getDriverId()));

        // Get bus
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Verify same school
        if (!driver.getSchool().getId().equals(bus.getSchool().getId())) {
            throw new RuntimeException("Driver and bus must belong to the same school");
        }

        // If bus already has a driver, unassign that driver
        if (bus.getAssignedDriver() != null) {
            Driver previousDriver = bus.getAssignedDriver();
            previousDriver.setAssignedBus(null);
            driverRepository.save(previousDriver);
        }

        // If driver already has a bus, unassign that bus
        if (driver.getAssignedBus() != null) {
            Bus previousBus = driver.getAssignedBus();
            previousBus.setAssignedDriver(null);
            busRepository.save(previousBus);
        }

        // Assign bus to driver
        bus.setAssignedDriver(driver);
        driver.setAssignedBus(bus);

        busRepository.save(bus);
        driverRepository.save(driver);

        return bus;
    }

    @Override
    @Transactional
    public List<Student> assignStudentsToBus(AssignStudentsToBusRequest request, Long adminId) {
        // Verify admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can assign students to buses");
        }

        // Get bus
        Bus bus = busRepository.findById(request.getBusId())
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Check bus capacity
        if (bus.getCapacity() != null) {
            long currentStudentCount = studentRepository.findAll().stream()
                    .filter(s -> s.getAssignedBus() != null && s.getAssignedBus().getId().equals(bus.getId()))
                    .count();
            
            if (currentStudentCount + request.getStudentIds().size() > bus.getCapacity()) {
                throw new RuntimeException("Cannot assign students: bus capacity exceeded");
            }
        }

        List<Student> assignedStudents = new ArrayList<>();

        // Assign each student to the bus
        for (Long studentId : request.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found with ID: " + studentId));

            // Verify same school
            if (!student.getSchool().getId().equals(bus.getSchool().getId())) {
                throw new RuntimeException("Student and bus must belong to the same school");
            }

            student.setAssignedBus(bus);
            studentRepository.save(student);
            assignedStudents.add(student);
        }

        return assignedStudents;
    }
}
