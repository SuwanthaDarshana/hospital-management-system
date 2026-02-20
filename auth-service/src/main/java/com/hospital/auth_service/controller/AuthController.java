package com.hospital.auth_service.controller;

import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

        private final AuthService authService;

        // Public: Anyone can register as a patient
        @Operation(summary = "Register a new patient", description = "Publicly accessible. Registers a new patient account.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Patient registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
        })
        @PostMapping("/register/patient")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerPatient(
                        @Valid @RequestBody PatientRegisterRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Patient registered successfully")
                                                .data(authService.registerPatient(dto))
                                                .build());
        }

        // Secured: Only ADMIN can call this via Gateway
        @Operation(summary = "Register a new doctor", description = "Restricted to ADMIN role. Registers a new doctor account.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Doctor registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or email already exists"),
                        @ApiResponse(responseCode = "403", description = "Access denied — Admin only")
        })
        @PostMapping("/register/doctor")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerDoctor(
                        @Valid @RequestBody DoctorRegisterRequestDTO dto) {
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Doctor registered successfully")
                                                .data(authService.registerDoctor(dto))
                                                .build());
        }

        @Operation(summary = "User login", description = "Authenticate with email and password to receive a JWT token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned"),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
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