package com.hospital.auth_service.messaging;

import com.hospital.auth_service.config.RabbitConfig;
import com.hospital.auth_service.dto.StaffUpdatedEvent;
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
public class StaffUpdatedListner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @RabbitListener(queues = RabbitConfig.STAFF_UPDATE_QUEUE)
    public void handleStaffUpdated(StaffUpdatedEvent event) {
        log.info(" Received Staff update event for Auth User ID: {}", event.getAuthUserId());

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> {
                    log.error(" Sync Failed: User not found with ID: {}", event.getAuthUserId());
                    return new RuntimeException("User not found");
                });

        // 1. Sync Email (The Username)
        if (event.getEmail() != null && !event.getEmail().isEmpty()) {
            user.setEmail(event.getEmail());
        }

        // 2. Sync Role (Permission changes)
        if (event.getRole() != null) {
            try {
                user.setRole(Role.valueOf(event.getRole()));
            } catch (IllegalArgumentException e) {
                log.error("Invalid role received: {}", event.getRole());
            }
        }

        // 3. Sync Password
        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(event.getPassword()));
            log.info("🔑 Password updated for user: {}", user.getEmail());
        }

        // 4. Industry Standard: Sync Account Status
        // If your User entity has an isActive or isEnabled field, update it here.
        // user.setEnabled(event.getIsActive());

        userRepository.save(user);
        log.info("✅ Auth Service successfully synchronized Staff user: {}", user.getEmail());
    }
}
