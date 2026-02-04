package com.hospital.patient_service.service;

import com.hospital.patient_service.dto.PatientRequestDTO;
import com.hospital.patient_service.dto.PatientResponseDTO;

import java.util.List;

public interface PatientService {
    PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO);
    PatientResponseDTO getPatientById(Long id);
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO getPatientByEmail(String email);
    PatientResponseDTO getPatientByPhoneNumber(String phoneNumber);
    PatientResponseDTO updatePatient(Long id, PatientRequestDTO patientRequestDTO);
    void deletePatient(Long id);


}
