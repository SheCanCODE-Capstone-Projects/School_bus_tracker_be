package org.example.school_bus_tracker_be.Config;

import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SchoolRepository schoolRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.admin.default.email}")
    private String defaultAdminEmail;
    
    @Value("${app.admin.default.password}")
    private String defaultAdminPassword;

    public DataInitializer(UserRepository userRepository, 
                          SchoolRepository schoolRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.schoolRepository = schoolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Create default school if none exists
        if (schoolRepository.count() == 0) {
            School defaultSchool = new School();
            defaultSchool.setName("Default School");
            defaultSchool.setAddress("123 Main St");
            defaultSchool.setPhone("555-0123");
            schoolRepository.save(defaultSchool);
        }

        // Create default admin if none exists
        if (userRepository.findByRole(User.Role.ADMIN).isEmpty()) {
            School defaultSchool = schoolRepository.findAll().get(0);
            
            User admin = new User(
                defaultSchool,
                "System Admin",
                defaultAdminEmail,
                passwordEncoder.encode(defaultAdminPassword),
                "555-0100",
                User.Role.ADMIN
            );
            
            userRepository.save(admin);
            System.out.println("Default admin created: " + defaultAdminEmail + " / " + defaultAdminPassword);
        }
    }
}