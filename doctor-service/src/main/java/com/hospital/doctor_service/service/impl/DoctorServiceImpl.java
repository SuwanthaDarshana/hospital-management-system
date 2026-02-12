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
    public DoctorResponseDTO getDoctorByAuthUserId(Long authUserId) {
        Doctor doctor = doctorRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        return mapToResponse(doctor);
    }

    // ========================= UPDATE =========================

    @Override
    public DoctorResponseDTO updateDoctor(Long authUserId, DoctorRequestDTO dto) {
        Doctor doctor = doctorRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        // 1. Get Security Context Data
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = auth.getName(); // Extracted from JWT by Gateway
        String role = getCurrentUserRole();

        // 2. OWNERSHIP CHECK: Doctors can only update their own profile
        if ("DOCTOR".equals(role)) {
            if (!doctor.getEmail().equalsIgnoreCase(currentUserEmail)) {
                throw new RuntimeException("Access Denied: You can only update your own profile.");
            }
        }

        // 3. Build Update Event
        DoctorUpdatedEvent event = DoctorUpdatedEvent.builder()
                .authUserId(doctor.getAuthUserId())
                .build();

        boolean sendToAuth = false;

        // ========== ADMIN LOGIC ==========
        if ("ADMIN".equals(role)) {
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
            event.setPassword(dto.getPassword());
            sendToAuth = true;
        }
        // ========== DOCTOR LOGIC ==========
        else if ("DOCTOR".equals(role)) {
            if (dto.getPhone() != null) doctor.setPhone(dto.getPhone());
            if (dto.getSpecialization() != null) doctor.setSpecialization(dto.getSpecialization());
            if (dto.getAvailability() != null) doctor.setAvailability(dto.getAvailability());

            event.setPhone(dto.getPhone());
            event.setSpecialization(dto.getSpecialization());
            event.setAvailability(dto.getAvailability());

            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                event.setPassword(dto.getPassword());
                sendToAuth = true;
            }
            // Sync phone/specialization if needed in Auth DB
            if (dto.getPhone() != null) sendToAuth = true;
        }

        doctor.setUpdatedAt(LocalDateTime.now());
        doctorRepository.save(doctor);

        if (sendToAuth) {
            rabbitTemplate.convertAndSend(
                    RabbitConfig.DOCTOR_UPDATE_EXCHANGE,
                    RabbitConfig.DOCTOR_UPDATE_ROUTING_KEY,
                    event
            );
        }

        return mapToResponse(doctor);
    }

    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_ANONYMOUS")
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
                .authUserId(doctor.getAuthUserId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .specialization(doctor.getSpecialization())
                .availability(doctor.getAvailability())
                .build();
    }
}
