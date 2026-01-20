package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Dtos.location.LocationResponse;
import org.example.school_bus_tracker_be.Dtos.location.LocationUpdateRequest;
import org.example.school_bus_tracker_be.Dtos.location.TrackingStatusResponse;
import org.example.school_bus_tracker_be.Enum.Role;
import org.example.school_bus_tracker_be.Model.*;
import org.example.school_bus_tracker_be.Repository.*;
import org.example.school_bus_tracker_be.Service.LocationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LocationServiceImpl implements LocationService {

    private final BusTrackingRepository busTrackingRepository;
    private final BusLocationRepository busLocationRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final StudentRepository studentRepository;

    public LocationServiceImpl(
            BusTrackingRepository busTrackingRepository,
            BusLocationRepository busLocationRepository,
            UserRepository userRepository,
            DriverRepository driverRepository,
            BusRepository busRepository,
            StudentRepository studentRepository) {
        this.busTrackingRepository = busTrackingRepository;
        this.busLocationRepository = busLocationRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public void startTracking(Long driverId) {
        // Get driver user
        User driverUser = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!driverUser.getRole().equals(Role.DRIVER)) {
            throw new RuntimeException("User is not a driver");
        }

        // Get driver entity
        Driver driver = driverRepository.findByEmail(driverUser.getEmail())
                .orElseThrow(() -> new RuntimeException("Driver entity not found"));

        // Verify driver has assigned bus
        if (driver.getAssignedBus() == null) {
            throw new RuntimeException("Driver does not have an assigned bus");
        }

        Bus bus = driver.getAssignedBus();

        // Business rule: Check no ACTIVE tracking exists for the bus
        if (busTrackingRepository.findByBusIdAndStatus(bus.getId(), BusTracking.Status.ACTIVE).isPresent()) {
            throw new RuntimeException("Bus already has an active tracking session");
        }

        // Create BusTracking record
        BusTracking tracking = new BusTracking(bus, driverUser, BusTracking.Status.ACTIVE);
        tracking.setStartedAt(LocalDateTime.now());
        busTrackingRepository.save(tracking);
    }

    @Override
    @Transactional
    public void updateLocation(Long driverId, LocationUpdateRequest request) {
        // Get driver user
        User driverUser = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!driverUser.getRole().equals(Role.DRIVER)) {
            throw new RuntimeException("User is not a driver");
        }

        // Get driver entity
        Driver driver = driverRepository.findByEmail(driverUser.getEmail())
                .orElseThrow(() -> new RuntimeException("Driver entity not found"));

        // Verify driver has assigned bus
        if (driver.getAssignedBus() == null) {
            throw new RuntimeException("Driver does not have an assigned bus");
        }

        Bus bus = driver.getAssignedBus();

        // Validate tracking status = ACTIVE
        BusTracking tracking = busTrackingRepository.findByDriverIdAndStatus(driverId, BusTracking.Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active tracking session found. Please start tracking first."));

        // Verify tracking is for the correct bus
        if (!tracking.getBus().getId().equals(bus.getId())) {
            throw new RuntimeException("Tracking session does not match driver's assigned bus");
        }

        // Save GPS location
        BusLocation location = new BusLocation(
                bus,
                request.getLatitude(),
                request.getLongitude(),
                request.getSpeed(),
                request.getHeading()
        );
        location.setRecordedAt(LocalDateTime.now());
        busLocationRepository.save(location);

        // TODO: Broadcast location to parents & admin (optional - can use WebSocket or SSE)
    }

    @Override
    @Transactional
    public void stopTracking(Long driverId) {
        // Get driver user
        User driverUser = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (!driverUser.getRole().equals(Role.DRIVER)) {
            throw new RuntimeException("User is not a driver");
        }

        // Verify ACTIVE tracking exists
        BusTracking tracking = busTrackingRepository.findByDriverIdAndStatus(driverId, BusTracking.Status.ACTIVE)
                .orElseThrow(() -> new RuntimeException("No active tracking session found"));

        // Update BusTracking
        tracking.setStatus(BusTracking.Status.STOPPED);
        tracking.setStoppedAt(LocalDateTime.now());
        busTrackingRepository.save(tracking);
    }

    @Override
    public LocationResponse getBusLocation(Long busId, Long parentId) {
        // Verify parent
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        if (!parent.getRole().equals(Role.PARENT)) {
            throw new RuntimeException("User is not a parent");
        }

        // Verify bus exists
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Business rule: Parent must have a child assigned to the bus
        List<Student> students = studentRepository.findBySchool(parent.getSchool());
        boolean hasChildOnBus = students.stream()
                .anyMatch(s -> s.getAssignedBus() != null && s.getAssignedBus().getId().equals(busId) &&
                        (s.getParentPhone().equals(parent.getPhone()) || s.getParentName().equals(parent.getName())));

        if (!hasChildOnBus) {
            throw new RuntimeException("Access denied: You do not have a child assigned to this bus");
        }

        // Get last known GPS location
        BusLocation latestLocation = busLocationRepository.findLatestByBusId(busId)
                .orElseThrow(() -> new RuntimeException("No location data available for this bus"));

        return new LocationResponse(
                latestLocation.getLatitude(),
                latestLocation.getLongitude(),
                latestLocation.getSpeed(),
                latestLocation.getHeading(),
                latestLocation.getRecordedAt()
        );
    }

    @Override
    public TrackingStatusResponse getTrackingStatus(Long busId) {
        // Verify bus exists
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        // Get latest tracking status
        BusTracking tracking = busTrackingRepository.findByBusIdAndStatus(busId, BusTracking.Status.ACTIVE)
                .orElse(null);

        if (tracking == null) {
            // Get most recent tracking (even if stopped)
            List<BusTracking> allTrackings = busTrackingRepository.findByBusId(busId);
            if (allTrackings.isEmpty()) {
                return new TrackingStatusResponse(
                        "STOPPED",
                        null,
                        null,
                        null,
                        bus.getBusNumber()
                );
            }
            tracking = allTrackings.get(0); // Most recent
        }

        return new TrackingStatusResponse(
                tracking.getStatus().name(),
                tracking.getStartedAt(),
                tracking.getStoppedAt(),
                tracking.getDriver().getName(),
                tracking.getBus().getBusNumber()
        );
    }

    @Override
    public List<LocationResponse> getLocationHistory(Long busId, LocalDateTime from, LocalDateTime to) {
        // Verify bus exists
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new RuntimeException("Bus not found"));

        List<BusLocation> locations;
        if (from != null && to != null) {
            locations = busLocationRepository.findByBusIdAndDateRange(busId, from, to);
        } else {
            locations = busLocationRepository.findByBusIdOrderByRecordedAtDesc(busId);
        }

        return locations.stream()
                .map(loc -> new LocationResponse(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        loc.getSpeed(),
                        loc.getHeading(),
                        loc.getRecordedAt()
                ))
                .collect(Collectors.toList());
    }
}
