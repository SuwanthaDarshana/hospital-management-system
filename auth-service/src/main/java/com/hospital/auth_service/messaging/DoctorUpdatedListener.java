package com.hospital.auth_service.messaging;


import com.hospital.auth_service.dto.DoctorUpdatedEvent;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DoctorUpdatedListener {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @RabbitListener(queues = "doctor.update.queue")
    public void handleDoctorUpdated(DoctorUpdatedEvent event) {

        log.info("Received doctor update event for auth user ID: {}", event.getAuthUserId());

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", event.getAuthUserId());
                    return new RuntimeException("User not found");
                });

        if (event.getEmail() != null) {
            log.debug("Updating email for user ID: {} to: {}", event.getAuthUserId(), event.getEmail());
            user.setEmail(event.getEmail());
        }

        if (event.getRole() != null) {
            log.debug("Updating role for user ID: {} to: {}", event.getAuthUserId(), event.getRole());
            user.setRole(Role.valueOf(event.getRole()));
        }

        if (event.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(event.getPassword()));
            log.info("Password updated for user: {}", user.getEmail());
        }

        userRepository.save(user);
        log.info("Auth Service successfully updated user: {}", user.getEmail());
    }

}
