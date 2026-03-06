package com.hospital.staff_service.messaging;

import com.hospital.staff_service.config.RabbitConfig;
import com.hospital.staff_service.dto.StaffCreatedEvent;
import com.hospital.staff_service.entity.Staff;
import com.hospital.staff_service.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class StaffEventListener {

    private final StaffRepository staffRepository;

    @RabbitListener(queues = RabbitConfig.STAFF_QUEUE) // Use constant for safety
    public void handleStaffCreated(StaffCreatedEvent event) {

        log.info("📥 Received staff registration from Auth Service: {}", event.getEmail());

        // 1. IDEMPOTENCY CHECK: Prevent duplicate profiles
        if (staffRepository.existsByAuthUserId(event.getAuthUserId())) {
            log.warn("⚠️ Staff profile already exists for authUserId: {}. Skipping...", event.getAuthUserId());
            return;
        }

        // 2. BUILD ENTITY: Map all fields from the DTO
        Staff staff = Staff.builder()
                .authUserId(event.getAuthUserId())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .phone(event.getPhone())
                .department(event.getDepartment())
                .address(event.getAddress())
                .gender(event.getGender())
                .dateOfBirth(event.getDateOfBirth())
                .bloodGroup(event.getBloodGroup())
                .role(event.getRole())
                .isActive(true)
                .build();

        // 3. SAVE TO DB
        try {
            staffRepository.save(staff);
            log.info("Staff profile successfully created in Staff Service DB for: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to save staff profile: {}", e.getMessage());
            // In a real system, you might throw an exception here to trigger a RabbitMQ retry
        }
    }
}
