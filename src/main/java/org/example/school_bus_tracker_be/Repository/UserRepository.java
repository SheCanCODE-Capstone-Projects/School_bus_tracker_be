package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Model.School;
import org.example.school_bus_tracker_be.Enum.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;



@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<User> findByRole(Role role);
    List<User> findBySchoolAndRole(School school, Role role);
    long countBySchoolAndRole(School school, Role role);
}
