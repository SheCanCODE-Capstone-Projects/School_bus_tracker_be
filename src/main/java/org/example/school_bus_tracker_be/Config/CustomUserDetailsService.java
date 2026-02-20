package org.example.school_bus_tracker_be.Config;

import org.example.school_bus_tracker_be.Model.User;
import org.example.school_bus_tracker_be.Repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found"));
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        // Admin-added parents have null password; use unguessable value so login fails until they set password via reset
        String encodedPassword = user.getPassword();
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            encodedPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                encodedPassword,
                authorities
        );
    }
}