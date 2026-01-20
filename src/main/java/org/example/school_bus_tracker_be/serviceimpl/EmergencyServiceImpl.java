package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Dtos.emergency.*;
import org.example.school_bus_tracker_be.Enum.EmergencyType;
import org.example.school_bus_tracker_be.Enum.Role;
import org.example.school_bus_tracker_be.Model.*;
import org.example.school_bus_tracker_be.Repository.*;
import org.example.school_bus_tracker_be.Service.EmergencyService;
import org.example.school_bus_tracker_be.Service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EmergencyServiceImpl implements EmergencyService {

    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    @Value("${app.uploads.dir:uploads/emergencies}")
    private String uploadsDir;

    public EmergencyServiceImpl(
            EmergencyRepository emergencyRepository,
            UserRepository userRepository,
            DriverRepository driverRepository,
            BusRepository busRepository,
            StudentRepository studentRepository,
            NotificationService notificationService) {
        this.emergencyRepository = emergencyRepository;
        this.userRepository = userRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ReportEmergencyResponse reportEmergency(ReportEmergencyRequest request, Long driverId) {
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

        // Business rule: Only one ACTIVE emergency per bus
        List<Emergency> activeEmergencies = emergencyRepository.findActiveByBusId(bus.getId());
        if (!activeEmergencies.isEmpty()) {
            throw new RuntimeException("Bus already has an active emergency. Please resolve it first.");
        }

        // Validation: at least description OR voice must be provided
        if ((request.getDescription() == null || request.getDescription().trim().isEmpty()) 
            && (request.getVoiceAudio() == null || request.getVoiceAudio().isEmpty())) {
            throw new RuntimeException("At least description or voice recording must be provided");
        }

        // Handle voice recording upload
        String voiceRecordingUrl = null;
        if (request.getVoiceAudio() != null && !request.getVoiceAudio().isEmpty()) {
            try {
                voiceRecordingUrl = saveVoiceRecording(request.getVoiceAudio());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save voice recording: " + e.getMessage());
            }
        }

        // Create emergency
        Emergency emergency = new Emergency(
                request.getType(),
                request.getDescription(),
                bus,
                driverUser,
                request.getLatitude(),
                request.getLongitude()
        );
        emergency.setVoiceRecordingUrl(voiceRecordingUrl);
        emergency.setStatus(Emergency.Status.ACTIVE);
        emergency.setParentsNotified(false);

        emergency = emergencyRepository.save(emergency);

        // Notify parents (initial alert) + mark notified
        notifyParentsForBusEmergency(bus, emergency, false);
        emergency.setParentsNotified(true);
        emergencyRepository.save(emergency);

        return new ReportEmergencyResponse("Emergency reported successfully", emergency.getId());
    }

    @Override
    public EmergencyStatsResponse getEmergencyStats() {
        Long active = emergencyRepository.countActive();
        
        // Calculate start and end of today
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        
        Long resolvedToday = emergencyRepository.countResolvedToday(startOfDay, endOfDay);
        Long total = emergencyRepository.countTotal();
        
        return new EmergencyStatsResponse(active, resolvedToday, total);
    }

    @Override
    public List<EmergencyResponse> getEmergencies(Emergency.Status status) {
        List<Emergency> emergencies;
        if (status != null) {
            emergencies = emergencyRepository.findByStatus(status);
        } else {
            emergencies = emergencyRepository.findAll();
        }
        
        return emergencies.stream()
                .map(EmergencyResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyResponse getEmergencyById(Long emergencyId, Long userId, String userRole) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found"));

        // Visibility check based on role
        if (userRole.equals("DRIVER")) {
            User driver = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Driver not found"));
            Driver driverEntity = driverRepository.findByEmail(driver.getEmail())
                    .orElseThrow(() -> new RuntimeException("Driver entity not found"));
            
            if (driverEntity.getAssignedBus() == null || 
                !emergency.getBus().getId().equals(driverEntity.getAssignedBus().getId())) {
                throw new RuntimeException("Access denied: Emergency does not belong to your bus");
            }
        } else if (userRole.equals("PARENT")) {
            User parent = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            
            List<Student> students = studentRepository.findBySchool(parent.getSchool());
            List<Long> parentBusIds = students.stream()
                    .filter(s -> s.getAssignedBus() != null)
                    .map(s -> s.getAssignedBus().getId())
                    .distinct()
                    .collect(Collectors.toList());
            
            if (!parentBusIds.contains(emergency.getBus().getId())) {
                throw new RuntimeException("Access denied: Emergency does not belong to your children's buses");
            }
        }
        // ADMIN can see all emergencies, no check needed

        return new EmergencyResponse(emergency);
    }

    @Override
    @Transactional
    public void resolveEmergency(Long emergencyId, ResolveEmergencyRequest request, Long adminId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found"));

        // Validate emergency is ACTIVE
        if (emergency.getStatus() != Emergency.Status.ACTIVE) {
            throw new RuntimeException("Only active emergencies can be resolved");
        }

        // Get admin
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!admin.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("Only admins can resolve emergencies");
        }

        // Update emergency
        emergency.setStatus(Emergency.Status.RESOLVED);
        emergency.setResolvedAt(LocalDateTime.now());
        emergency.setResolvedByAdmin(admin);
        emergency.setResolutionNotes(request.getResolutionNotes());

        // Notify parents if requested
        if (request.getNotifyParents() != null && request.getNotifyParents()) {
            notifyParentsForBusEmergency(emergency.getBus(), emergency, true);
            emergency.setParentsNotified(true);
        }

        emergencyRepository.save(emergency);
    }

    private void notifyParentsForBusEmergency(Bus bus, Emergency emergency, boolean isResolved) {
        if (bus == null) return;

        // Find students assigned to this bus
        List<Student> studentsOnBus = studentRepository.findBySchool(bus.getSchool())
                .stream()
                .filter(s -> s.getAssignedBus() != null && s.getAssignedBus().getId().equals(bus.getId()))
                .collect(Collectors.toList());

        if (studentsOnBus.isEmpty()) return;

        // Distinct parent phones for this bus
        List<String> parentPhones = studentsOnBus.stream()
                .map(Student::getParentPhone)
                .distinct()
                .collect(Collectors.toList());

        String title = isResolved ? "Emergency Resolved" : "Emergency Alert";
        String statusText = isResolved ? "RESOLVED" : "ACTIVE";
        String busNumber = bus.getBusNumber() != null ? bus.getBusNumber() : ("Bus #" + bus.getId());
        String type = emergency.getType() != null ? emergency.getType().name() : "UNKNOWN";

        String message = "Bus: " + busNumber + "\n"
                + "Type: " + type + "\n"
                + "Status: " + statusText + "\n"
                + (emergency.getDescription() != null ? ("Message: " + emergency.getDescription()) : "");

        for (String phone : parentPhones) {
            User parent = userRepository.findByPhone(phone).orElse(null);
            if (parent != null && parent.getRole() == Role.PARENT) {
                notificationService.createNotification(parent, title, message, Notification.Type.EMERGENCY);
            }
        }
    }

    @Override
    public List<EmergencyResponse> getDriverEmergencies(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Driver driverEntity = driverRepository.findByEmail(driver.getEmail())
                .orElseThrow(() -> new RuntimeException("Driver entity not found"));

        if (driverEntity.getAssignedBus() == null) {
            return new ArrayList<>();
        }

        List<Emergency> emergencies = emergencyRepository.findByBusId(driverEntity.getAssignedBus().getId());
        
        return emergencies.stream()
                .map(EmergencyResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyResponse getDriverEmergencyById(Long emergencyId, Long driverId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found"));

        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        Driver driverEntity = driverRepository.findByEmail(driver.getEmail())
                .orElseThrow(() -> new RuntimeException("Driver entity not found"));

        if (driverEntity.getAssignedBus() == null || 
            !emergency.getBus().getId().equals(driverEntity.getAssignedBus().getId())) {
            throw new RuntimeException("Access denied: Emergency does not belong to your bus");
        }

        return new EmergencyResponse(emergency);
    }

    @Override
    public List<EmergencyResponse> getParentEmergencies(Long parentId) {
        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        // Get all students of this parent (by matching parent phone/email)
        List<Student> students = studentRepository.findBySchool(parent.getSchool());
        List<Student> parentStudents = students.stream()
                .filter(s -> s.getParentPhone().equals(parent.getPhone()) || 
                            s.getParentName().equals(parent.getName()))
                .collect(Collectors.toList());

        if (parentStudents.isEmpty()) {
            return new ArrayList<>();
        }

        // Get unique bus IDs
        List<Long> busIds = parentStudents.stream()
                .filter(s -> s.getAssignedBus() != null)
                .map(s -> s.getAssignedBus().getId())
                .distinct()
                .collect(Collectors.toList());

        if (busIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Emergency> emergencies = emergencyRepository.findByBusIds(busIds);
        
        return emergencies.stream()
                .map(EmergencyResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    public EmergencyResponse getParentEmergencyById(Long emergencyId, Long parentId) {
        Emergency emergency = emergencyRepository.findById(emergencyId)
                .orElseThrow(() -> new RuntimeException("Emergency not found"));

        User parent = userRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        // Get all students of this parent
        List<Student> students = studentRepository.findBySchool(parent.getSchool());
        List<Long> parentBusIds = students.stream()
                .filter(s -> s.getAssignedBus() != null)
                .map(s -> s.getAssignedBus().getId())
                .distinct()
                .collect(Collectors.toList());

        if (!parentBusIds.contains(emergency.getBus().getId())) {
            throw new RuntimeException("Access denied: Emergency does not belong to your children's buses");
        }

        return new EmergencyResponse(emergency);
    }

    private String saveVoiceRecording(MultipartFile file) throws IOException {
        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(uploadsDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".mp3";
        String filename = UUID.randomUUID().toString() + extension;

        // Save file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Return URL (relative path)
        return "/uploads/emergencies/" + filename;
    }
}
