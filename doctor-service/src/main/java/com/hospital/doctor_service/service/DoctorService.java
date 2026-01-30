package com.hospital.doctor_service.service;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;

import java.util.List;

public interface DoctorService {
    DoctorResponseDTO createDoctor(DoctorRequestDTO doctorRequestDTO);
    List<DoctorRequestDTO> getAllDoctors();
    DoctorResponseDTO getDoctorById(Long id);
    DoctorResponseDTO updateDoctor(Long id,DoctorRequestDTO doctorRequestDTO);
    void deleteDoctor (Long id);
    List<DoctorResponseDTO> searchBySpecialization (String specialization);

}
