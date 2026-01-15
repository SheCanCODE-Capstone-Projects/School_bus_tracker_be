package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.ParentBus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for mapping parents to buses. Although named
 * {@code StudentBusRepository}, it persists {@link ParentBus} entities that
 * link a parent (user) to a bus. Feel free to rename this class in a future
 * refactoring to better reflect its purpose.
 */
@Repository
public interface StudentBusRepository extends JpaRepository<ParentBus, Long> {
}
