package org.example.school_bus_tracker_be.Config;

import org.example.school_bus_tracker_be.Model.BusStop;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Repository.BusStopRepository;
import org.example.school_bus_tracker_be.Repository.SchoolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final SchoolRepository schoolRepository;
    private final BusStopRepository busStopRepository;

    public DataLoader(SchoolRepository schoolRepository, BusStopRepository busStopRepository) {
        this.schoolRepository = schoolRepository;
        this.busStopRepository = busStopRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // If bus stops already exist, skip seeding
        if (busStopRepository.count() > 0) return;

        School school;
        List<School> schools = schoolRepository.findAll();
        if (schools.isEmpty()) {
            school = new School("Default School", "123 Default Address","+250782600535");
            school = schoolRepository.save(school);
        } else {
            school = schools.get(0);
        }

        BusStop s1 = new BusStop(school, "Main Street Stop", 12.9716, 77.5946);
        BusStop s2 = new BusStop(school, "North Gate Stop", 12.9750, 77.5970);
        BusStop s3 = new BusStop(school, "East Park Stop", 12.9680, 77.5900);

        busStopRepository.saveAll(List.of(s1, s2, s3));
    }
}