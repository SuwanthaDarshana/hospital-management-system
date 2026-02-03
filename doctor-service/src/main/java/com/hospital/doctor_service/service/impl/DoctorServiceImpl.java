package com.hospital.doctor_service.service.impl;

import com.hospital.doctor_service.config.RabbitConfig;
import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;
import com.hospital.doctor_service.dto.DoctorUpdatedEvent;
import com.hospital.doctor_service.entity.Doctor;
import com.hospital.doctor_service.exception.ResourceNotFoundException;
import com.hospital.doctor_service.repository.DoctorRepository;
import com.hospital.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final RabbitTemplate rabbitTemplate;

    // ========================= READ =========================

    @Override
    public List<DoctorResponseDTO> getAllDoctors() {
        return doctorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorResponseDTO getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return mapToResponse(doctor);
    }

    // ========================= UPDATE =========================

    @Override
    public DoctorResponseDTO updateDoctor(Long id, DoctorRequestDTO dto) {

        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        String role = getCurrentUserRole();

        // Build PATCH-style event for Auth Service
        DoctorUpdatedEvent event = DoctorUpdatedEvent.builder()
                .authUserId(doctor.getAuthUserId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .specialization(dto.getSpecialization())
                .availability(dto.getAvailability())
                .role(dto.getRole())
                .password(dto.getPassword())
                .build();


        boolean sendToAuth = false;

        // ========== ADMIN ==========
        if ("ADMIN".equals(role)) {

            // Admin can update all fields (if provided)
            if (dto.getFirstName() != null) doctor.setFirstName(dto.getFirstName());
            if (dto.getLastName() != null) doctor.setLastName(dto.getLastName());
            if (dto.getEmail() != null) doctor.setEmail(dto.getEmail());
            if (dto.getPhone() != null) doctor.setPhone(dto.getPhone());
            if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
            if (dto.getAvailability() != null) doctor.setAvailability(dto.getAvailability());
            if (dto.getRole() != null) doctor.setRole(dto.getRole());


            event.setFirstName(dto.getFirstName());
            event.setLastName(dto.getLastName());
            event.setEmail(dto.getEmail());
            event.setRole(dto.getRole());
            event.setPassword(dto.getPassword());
            event.setPhone(dto.getPhone());
            event.setSpecialization(dto.getSpecialization());
            event.setAvailability(dto.getAvailability());
            event.setRole(dto.getRole());

            sendToAuth = true;
        }

        // ========== DOCTOR ==========
        else if ("DOCTOR".equals(role)) {

            // Doctor can update only limited fields
            if (dto.getPhone() != null) doctor.setPhone(dto.getPhone());
            if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
            if (dto.getAvailability() != null) doctor.setAvailability(dto.getAvailability());

            event.setPhone(dto.getPhone());
            event.setSpecialization(dto.getSpecialization());
            event.setAvailability(dto.getAvailability());

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                event.setPassword(dto.getPassword());
            }

            sendToAuth = dto.getPassword() != null || dto.getPhone() != null || dto.getSpecialization() != null || dto.getAvailability() != null;
        }

        else {
            throw new RuntimeException("Unauthorized role: " + role);
        }

        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        // ================= Send to Auth Service =================
        if (sendToAuth) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.AUTH_EXCHANGE,
                    RabbitConfig.AUTH_ROUTING_KEY,
                    event
            );
            System.out.println("📤 DoctorUpdatedEvent sent to Auth Service");
        }

        return mapToResponse(doctor);
    }

    // ========================= SECURITY =========================

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElseThrow()
                .replace("ROLE_", "");
    }

    // ========================= SEARCH =========================

    @Override
    public List<DoctorResponseDTO> searchBySpecialization(String specialization) {
        return doctorRepository.findBySpecializationContainingIgnoreCase(specialization)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ========================= MAPPER =========================

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
