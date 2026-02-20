package com.hospital.auth_service.messaging;

import com.hospital.auth_service.dto.PatientUpdatedEvent;
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
public class PatientUpdateListener {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Ensure this queue name matches your RabbitConfig: "patient.update.queue"
    @RabbitListener(queues = "patient.update.queue")
    public void handlePatientUpdated(PatientUpdatedEvent event) {

        log.info("Received Update for User ID: {}", event.getAuthUserId());

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", event.getAuthUserId());
                    return new RuntimeException("User not found");
                });

        // 1. Update Email if changed
        if (event.getEmail() != null && !event.getEmail().isEmpty()) {
            log.debug("Updating email for user ID: {} to: {}", event.getAuthUserId(), event.getEmail());
            user.setEmail(event.getEmail());
        }

        // 2. Update Password ONLY if a new one is provided
        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(event.getPassword()));
            log.info("Password updated for user: {}", user.getEmail());
        }

        userRepository.save(user);
        log.info("Auth Service updated user: {}", user.getEmail());
    }
}
