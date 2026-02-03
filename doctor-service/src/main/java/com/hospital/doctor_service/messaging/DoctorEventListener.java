package com.hospital.doctor_service.messaging;

import com.hospital.doctor_service.dto.DoctorCreatedEvent;
import com.hospital.doctor_service.entity.Doctor;
import com.hospital.doctor_service.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DoctorEventListener {
    private final DoctorRepository doctorRepository;

    @RabbitListener(queues = "doctor.queue")
    public void handleDoctorCreated(DoctorCreatedEvent event) {

        System.out.println("📥 Received doctor from Auth Service: " + event.getEmail());

        Doctor doctor = Doctor.builder()
                .authUserId(event.getAuthUserId())   // <-- Link to Auth Service user
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .phone(event.getPhone())
                .specialization(event.getSpecialization())
                .availability("NOT_SET")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        doctorRepository.save(doctor);
        System.out.println("✅ Doctor saved in Doctor Service DB");
    }
}
