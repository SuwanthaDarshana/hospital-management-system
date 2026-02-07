package com.hospital.patient_service.messaging;

import com.hospital.patient_service.dto.PatientCreatedEvent;
import com.hospital.patient_service.entity.Patient;
import com.hospital.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientCreatedEventListener {

    private final PatientRepository patientRepository;

    @RabbitListener(queues = "patient.queue")
    public void handlePatientCreated(PatientCreatedEvent event) {
        if (patientRepository.existsByEmail(event.getEmail())) {
            return;
        }

        Patient patient = Patient.builder()
                .authUserId(event.getAuthUserId())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .email(event.getEmail())
                .phone(event.getPhone())
                .address(event.getAddress())
                .gender(event.getGender())
                .dateOfBirth(event.getDateOfBirth())
                .bloodGroup(event.getBloodGroup())
                .isActive(event.isActive())
                .role("PATIENT")
                .build();
        patientRepository.save(patient);
        System.out.println("✅ Patient Service: Profile created automatically for " + event.getEmail());

    }
}
