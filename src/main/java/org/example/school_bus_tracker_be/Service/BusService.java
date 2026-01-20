package org.example.school_bus_tracker_be.Service;

import org.example.school_bus_tracker_be.DTO.AssignBusToDriverRequest;
import org.example.school_bus_tracker_be.DTO.AssignStudentsToBusRequest;
import org.example.school_bus_tracker_be.Model.Bus;
import org.example.school_bus_tracker_be.Model.Driver;
import java.util.List;

public interface BusService {
    Bus assignBusToDriver(AssignBusToDriverRequest request, Long adminId);
    List<org.example.school_bus_tracker_be.Model.Student> assignStudentsToBus(AssignStudentsToBusRequest request, Long adminId);
}
