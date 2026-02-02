package com.hospital.doctor_service.messaging;

import com.hospital.doctor_service.dto.DoctorCreatedEvent;
import com.hospital.doctor_service.entity.Doctor;
import com.hospital.doctor_service.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DoctorEventListener {
    private final DoctorRepository doctorRepository;

    @RabbitListener(queues = "doctor.queue")
    public void handleDoctorCreated(DoctorCreatedEvent event) {

        Doctor doctor = Doctor.builder()
                .email(event.getEmail())
                .firstName(event.getUsername())
                .specialization("GENERAL")
                .build();

        doctorRepository.save(doctor);
    }
}
