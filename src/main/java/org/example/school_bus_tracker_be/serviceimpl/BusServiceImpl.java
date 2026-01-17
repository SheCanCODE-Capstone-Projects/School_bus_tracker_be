package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.DTO.BusRequest;
import org.example.school_bus_tracker_be.DTO.BusResponse;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.BusRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.example.school_bus_tracker_be.Service.BusService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final SchoolRepository schoolRepository;
    private final UserRepository userRepository;

    public BusServiceImpl(BusRepository busRepository, SchoolRepository schoolRepository, UserRepository userRepository) {
        this.busRepository = busRepository;
        this.schoolRepository = schoolRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BusResponse createBus(BusRequest request) {
        validateBusRequest(request);

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getRole() != User.Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        Bus bus = new Bus(school, driver, request.getBusNumber(), request.getPlateNumber(), request.getStatus());
        Bus savedBus = busRepository.save(bus);

        return new BusResponse(savedBus);
    }

    @Override
    public List<BusResponse> getAllBuses() {
        return busRepository.findAll()
                .stream()
                .map(BusResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public BusResponse getBusById(Long id) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));
        return new BusResponse(bus);
    }

    @Override
    public BusResponse updateBus(Long id, BusRequest request) {
        Bus bus = busRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        School school = schoolRepository.findById(request.getSchoolId())
                .orElseThrow(() -> new RuntimeException("School not found"));

        User driver = userRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getRole() != User.Role.DRIVER) {
            throw new RuntimeException("User is not a driver");
        }

        bus.setSchool(school);
        bus.setDriver(driver);
        bus.setBusNumber(request.getBusNumber());
        bus.setPlateNumber(request.getPlateNumber());
        bus.setStatus(request.getStatus());

        Bus updatedBus = busRepository.save(bus);
        return new BusResponse(updatedBus);
    }

    @Override
    public void deleteBus(Long id) {
        if (!busRepository.existsById(id)) {
            throw new RuntimeException("Bus not found");
        }
        busRepository.deleteById(id);
    }

    private void validateBusRequest(BusRequest request) {
        if (busRepository.existsByBusNumber(request.getBusNumber())) {
            throw new RuntimeException("Bus number already exists");
        }
        if (busRepository.existsByPlateNumber(request.getPlateNumber())) {
            throw new RuntimeException("Plate number already exists");
        }
    }
}