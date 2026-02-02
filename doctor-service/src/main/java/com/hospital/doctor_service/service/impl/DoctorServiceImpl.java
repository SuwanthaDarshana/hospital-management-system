package com.hospital.doctor_service.service.impl;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;
import com.hospital.doctor_service.entity.Doctor;
import com.hospital.doctor_service.exception.ResourceNotFoundException;
import com.hospital.doctor_service.repository.DoctorRepository;
import com.hospital.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;



    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Doctor not found with id: " + id));
        return mapToResponse(doctor);
    }

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO doctorRequestDTO) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Doctor not found with id: " + id));
        doctor.setPhone(doctorRequestDTO.getPhone());
        doctor.setSpecialization(doctorRequestDTO.getSpecialization());
        doctor.setAvailability(doctorRequestDTO.getAvailability());
        doctor.setUpdatedAt(LocalDateTime.now());

        Doctor updated = doctorRepository.save(doctor);
        return mapToResponse(updated);
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Doctor not found with id: " + id));
        doctorRepository.delete(doctor);

    }

    @Override
    public List<DoctorResponseDTO> searchBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Mapper Doctor entity --> Response Dto
    private DoctorResponseDTO mapToResponse(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .availability(doctor.getAvailability())
                .build();
    }
}
