package com.hospital.patient_service.service.impl;

import com.hospital.patient_service.dto.PatientRequestDTO;
import com.hospital.patient_service.dto.PatientResponseDTO;
import com.hospital.patient_service.entity.Patient;
import com.hospital.patient_service.exception.ResourceNotFoundException;
import com.hospital.patient_service.repository.PatientRepository;
import com.hospital.patient_service.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;




    @Override
    @Transactional
    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new RuntimeException("Email already registered with another patient");
        }

        if (patientRepository.existsByPhoneNumber(patientRequestDTO.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered with another patient");
        }

        Patient patient = Patient.builder()
                .firstName(patientRequestDTO.getFirstName())
                .lastName(patientRequestDTO.getLastName())
                .email(patientRequestDTO.getEmail())
                .phoneNumber(patientRequestDTO.getPhoneNumber())
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
    public PatientResponseDTO getPatientByEmail(String email) {
        return patientRepository.findByEmail(email)
                .map(this::mapToPatientResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + email));
    }

    @Override
    public PatientResponseDTO getPatientByPhoneNumber(String phoneNumber) {
        return patientRepository.findByPhoneNumber(phoneNumber)
                .map(this::mapToPatientResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with phone number: " + phoneNumber));
    }

    @Override
    @Transactional
    public PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + id));

        patient.setFirstName(patientRequestDTO.getFirstName());
        patient.setLastName(patientRequestDTO.getLastName());
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setPhoneNumber(patientRequestDTO.getPhoneNumber());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setGender(patientRequestDTO.getGender());
        patient.setDateOfBirth(patientRequestDTO.getDateOfBirth());
        patient.setBloodGroup(patientRequestDTO.getBloodGroup());
        patient.setActive(patientRequestDTO.isActive());
        return null;
    }

    @Override
    @Transactional
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
                .phoneNumber(patient.getPhoneNumber())
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
