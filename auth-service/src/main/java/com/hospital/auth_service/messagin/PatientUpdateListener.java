package com.hospital.auth_service.messagin;

import com.hospital.auth_service.dto.PatientUpdatedEvent;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientUpdateListener {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Ensure this queue name matches your RabbitConfig: "patient.update.queue"
    @RabbitListener(queues = "patient.update.queue")
    public void handlePatientUpdated(PatientUpdatedEvent event) {

        System.out.println("Received Update for User ID: " + event.getAuthUserId());

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 1. Update Email if changed
        if (event.getEmail() != null && !event.getEmail().isEmpty()) {
            user.setEmail(event.getEmail());
        }

        // 2. Update Password ONLY if a new one is provided
        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(event.getPassword()));
            System.out.println("Password updated for user: " + user.getEmail());
        }

        userRepository.save(user);
        System.out.println("✅ Auth Service updated user " + user.getEmail());
    }
}
