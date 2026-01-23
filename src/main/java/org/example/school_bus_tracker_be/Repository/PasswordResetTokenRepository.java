package org.example.school_bus_tracker_be.Repository;

import org.example.school_bus_tracker_be.Model.PasswordResetToken;
import org.example.school_bus_tracker_be.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByCode(String code);
    Optional<PasswordResetToken> findByUser(User user);
    void deleteByUser(User user);
    
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("DELETE FROM PasswordResetToken p WHERE p.user.id = :userId")
    void deleteByUserId(Long userId);
}
