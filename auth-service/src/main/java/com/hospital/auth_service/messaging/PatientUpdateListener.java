package com.hospital.auth_service.messaging;

import com.hospital.auth_service.dto.PatientUpdatedEvent;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.RefreshTokenService;
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
    private final RefreshTokenService refreshTokenService;

    @RabbitListener(queues = "patient.update.queue")
    public void handlePatientUpdated(PatientUpdatedEvent event) {

        log.info("Received Update for User ID: {}", event.getAuthUserId());

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", event.getAuthUserId());
                    return new RuntimeException("User not found");
                });

        if (event.getEmail() != null && !event.getEmail().isEmpty()) {
            user.setEmail(event.getEmail());
        }

        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(event.getPassword()));
        }

        if (event.getIsActive() != null) {
            user.setEnabled(event.getIsActive());
            log.info("Patient auth account {} for user ID: {}", event.getIsActive() ? "enabled" : "disabled", event.getAuthUserId());
            if (!event.getIsActive()) {
                refreshTokenService.revokeAllUserTokens(user);
                log.info("Refresh tokens revoked for deactivated patient user ID: {}", event.getAuthUserId());
            }
        }

        userRepository.save(user);
        log.info("Auth Service updated patient user: {}", user.getEmail());
    }
}
