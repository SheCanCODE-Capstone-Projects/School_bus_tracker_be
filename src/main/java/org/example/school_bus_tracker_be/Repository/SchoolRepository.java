package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
/**
 * Repository for {@link School} entities.
 */
=======
>>>>>>> 9bceed13dfbc78d051061dcf9ea6a75fc3d056f1
@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
}
