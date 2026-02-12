package com.hospital.patient_service.service;

import com.hospital.patient_service.dto.PatientRequestDTO;
import com.hospital.patient_service.dto.PatientResponseDTO;

import java.util.List;

public interface PatientService {

    PatientResponseDTO getPatientByAuthUserId(Long authUserId);
    List<PatientResponseDTO> getAllPatients();
    PatientResponseDTO updatePatient(Long authUserId, PatientRequestDTO patientRequestDTO);
    void deletePatient(Long id);
    List<PatientResponseDTO> searchPatientsDynamic(String name, String phone, String email, String bloodGroup, Boolean isActive);


}
