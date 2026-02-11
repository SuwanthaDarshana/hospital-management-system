package com.hospital.auth_service.controller;

import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // Public: Anyone can register as a patient
    @PostMapping("/register/patient")
    public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerPatient(
            @Valid @RequestBody RegisterRequestDTO dto) {
        dto.setRole("PATIENT"); // Force role to PATIENT
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                        .success(true)
                        .message("Patient registered successfully")
                        .data(authService.register(dto))
                        .build());
    }

    // Secured: Only ADMIN can call this via Gateway
    @PostMapping("/register/doctor")
    public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerDoctor(
            @Valid @RequestBody RegisterRequestDTO dto) {
        dto.setRole("DOCTOR"); // Force role to DOCTOR
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                        .success(true)
                        .message("Doctor registered successfully")
                        .data(authService.register(dto))
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(
                StandardResponseDTO.<AuthResponseDTO>builder()
                        .success(true)
                        .message("Login successful")
                        .data(authService.login(dto))
                        .build());
    }
}