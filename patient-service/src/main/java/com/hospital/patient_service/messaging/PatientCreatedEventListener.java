package com.hospital.patient_service.messaging;

import com.hospital.patient_service.dto.PatientCreatedEvent;
import com.hospital.patient_service.entity.Patient;
import com.hospital.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PatientCreatedEventListener {

    private final PatientRepository patientRepository;

    @RabbitListener(queues = "patient.queue")
    public void handlePatientCreated(PatientCreatedEvent event) {
        try {
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
            log.info("Patient Service: Profile created automatically for {}", event.getEmail());
        } catch (Exception e) {
            log.error("Patient Service: Failed to create profile for {} — message discarded: {}", event.getEmail(), e.getMessage());
        }
    }
}
