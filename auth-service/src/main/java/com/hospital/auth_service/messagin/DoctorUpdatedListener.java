package com.hospital.auth_service.messagin;


import com.hospital.auth_service.dto.DoctorUpdatedEvent;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorUpdatedListener {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @RabbitListener(queues = "auth.queue")
    public void handleDoctorUpdated(DoctorUpdatedEvent event) {

        User user = userRepository.findById(event.getAuthUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (event.getEmail() != null)
            user.setEmail(event.getEmail());

        if (event.getRole() != null)
            user.setRole(Role.valueOf(event.getRole()));

        if (event.getPassword() != null)
            user.setPassword(passwordEncoder.encode(event.getPassword()));

        userRepository.save(user);

        System.out.println("✅ Auth Service updated user " + user.getEmail());
    }

}
