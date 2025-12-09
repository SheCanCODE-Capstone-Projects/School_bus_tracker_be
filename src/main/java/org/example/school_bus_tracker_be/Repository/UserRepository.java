package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 *
 * <p>
 * Extends {@link JpaRepository} to provide basic CRUD operations and
 * introduces a convenience method to look up a user by their email address.
 * Spring Data JPA automatically implements this interface at runtime.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their unique email address.
     *
     * @param email the user’s email address
     * @return an {@link Optional} containing the user if found, otherwise empty
     */
    Optional<User> findByEmail(String email);
}
