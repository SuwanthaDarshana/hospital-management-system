package com.hospital.doctor_service.service;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {
    List<DoctorResponseDTO> getAllDoctors();
    DoctorResponseDTO getDoctorByAuthUserId(Long authUserId);
    DoctorResponseDTO updateDoctor(Long authUserId,DoctorRequestDTO doctorRequestDTO);
    List<DoctorResponseDTO> searchBySpecialization (String specialization);


}
