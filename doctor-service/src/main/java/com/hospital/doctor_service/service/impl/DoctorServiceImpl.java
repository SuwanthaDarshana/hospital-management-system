package com.hospital.doctor_service.service.impl;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;
import com.hospital.doctor_service.entity.Doctor;
import com.hospital.doctor_service.repository.DoctorRepository;
import com.hospital.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorResponseDTO createDoctor(DoctorRequestDTO doctorRequestDTO){
        Doctor doctor = Doctor.builder()
                .firstName(doctorRequestDTO.getFirstName())
                .lastName(doctorRequestDTO.getLastName())
                .email(doctorRequestDTO.getEmail())
                .phone(doctorRequestDTO.getPhone())
                .specialization(doctorRequestDTO.getSpecialization())
                .availability(doctorRequestDTO.getAvailability())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        Doctor saved = doctorRepository.save(doctor);
        return mapToResponse(saved);
    }

    @Override
    public List<DoctorRequestDTO> getAllDoctors() {
        return List.of();
    }

    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        return null;
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO) {
        return null;
    }

    @Override
    public void deleteDoctor(Long id) {

    }

    @Override
    public List<DoctorResponseDTO> searchBySpecialization(String specialization) {
        return List.of();
    }
}
