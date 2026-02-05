package com.hospital.patient_service.service.impl;

import com.hospital.patient_service.config.RabbitConfig;
import com.hospital.patient_service.dto.PatientRequestDTO;
import com.hospital.patient_service.dto.PatientResponseDTO;
import com.hospital.patient_service.dto.PatientUpdatedEvent;
import com.hospital.patient_service.entity.Patient;
import com.hospital.patient_service.exception.ResourceNotFoundException;
import com.hospital.patient_service.repository.PatientRepository;
import com.hospital.patient_service.service.PatientService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final RabbitTemplate rabbitTemplate;




    @Override
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new RuntimeException("Email already registered with another patient");
        }

        if (patientRepository.existsByPhoneNumber(patientRequestDTO.getPhone())) {
            throw new RuntimeException("Phone number already registered with another patient");
        }

        Patient patient = Patient.builder()
                .firstName(patientRequestDTO.getFirstName())
                .lastName(patientRequestDTO.getLastName())
                .email(patientRequestDTO.getEmail())
                .phone(patientRequestDTO.getPhone())
                .address(patientRequestDTO.getAddress())
                .gender(patientRequestDTO.getGender())
                .dateOfBirth(patientRequestDTO.getDateOfBirth())
                .bloodGroup(patientRequestDTO.getBloodGroup())
                .isActive(true)
                .build();

        return mapToPatientResponseDTO(patientRepository.save(patient));
    }

    @Override
    public PatientResponseDTO getPatientById(Long id) {
        return patientRepository.findById(id)
                .map(this::mapToPatientResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));
    }

    @Override
    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientResponseDTO> searchPatientsDynamic(String name, String phone, String email, String bloodGroup, Boolean isActive) {

        Specification<Patient> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isEmpty()) {
                // Searches in both first and last name
                String pattern = "%" + name.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)
                ));
            }

            if (phone != null) {
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            }

            if (email != null) {
                predicates.add(cb.equal(root.get("email"), email));
            }

            if (bloodGroup != null) {
                predicates.add(cb.equal(root.get("bloodGroup"), bloodGroup));
            }

            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return patientRepository.findAll(spec).stream()
                .map(this::mapToPatientResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO requestDTO) {
        // 1. Fetch existing record
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        // 2. SECURITY CHECK: "Is this MY profile?"
        // We get the email of the person currently logged in from the JWT
        String loggedInEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        String loggedInRole = SecurityContextHolder.getContext().getAuthentication().getAuthorities().toString();

        // If I am a PATIENT, and this email in the DB is NOT mine -> Block access
        if (loggedInRole.contains("ROLE_PATIENT") && !patient.getEmail().equals(loggedInEmail)) {
            throw new RuntimeException("You are not authorized to update this profile");
            // Better to use AccessDeniedException here
        }

        // 3. Update fields
        patient.setFirstName(requestDTO.getFirstName());
        patient.setLastName(requestDTO.getLastName());
        patient.setPhone(requestDTO.getPhone());
        patient.setAddress(requestDTO.getAddress());
        patient.setGender(requestDTO.getGender());
        patient.setDateOfBirth(requestDTO.getDateOfBirth());
        patient.setBloodGroup(requestDTO.getBloodGroup());

        // Handle Email Update specifically (needs sync)
        boolean emailChanged = !patient.getEmail().equals(requestDTO.getEmail());
        if (emailChanged) {
            patient.setEmail(requestDTO.getEmail());
        }

        // 4. Save to DB
        Patient updatedPatient = patientRepository.save(patient);

        // 5. Send Synchronization Event to Auth Service (RabbitMQ)
        // We send if Email changed OR if Password is provided in the request
        if (emailChanged || (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty())) {

            PatientUpdatedEvent event = PatientUpdatedEvent.builder()
                    .authUserId(updatedPatient.getAuthUserId()) // The Link!
                    .email(updatedPatient.getEmail())
                    .password(requestDTO.getPassword()) // Will be null if not updating password
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_UPDATE_EXCHANGE,
                    RabbitConfig.PATIENT_UPDATE_ROUTING_KEY,
                    event
            );
            System.out.println("✅ Sync Message sent to Auth Service for: " + updatedPatient.getEmail());
        }

        return mapToPatientResponseDTO(updatedPatient);
    }

    @Override
    @Transactional //JPA will handle the transaction rollback if any exception occurs
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found with id: " + id);
        }
        patientRepository.deleteById(id);

    }

    //Entity convert to ResponseDTO
    private PatientResponseDTO mapToPatientResponseDTO(Patient patient){
        return PatientResponseDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .email(patient.getEmail())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .gender(patient.getGender())
                .dateOfBirth(patient.getDateOfBirth())
                .bloodGroup(patient.getBloodGroup())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .isActive(patient.isActive())
                .build();
    }
}
