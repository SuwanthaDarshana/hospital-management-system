package com.hospital.auth_service.controller;

import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration, login, token refresh, and logout")
public class AuthController {

        private final AuthService authService;

        // ── Register Patient ──────────────────────────────────────────────────────

        @Operation(summary = "Register a new patient", description = "Publicly accessible. Registers a new patient account.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Patient registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or email already exists")
        })
        @PostMapping("/register/patient")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerPatient(
                        @Valid @RequestBody PatientRegisterRequestDTO dto) {
                log.info("POST /register/patient called for email: {}", dto.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Patient registered successfully")
                                                .data(authService.registerPatient(dto))
                                                .build());
        }

        // ── Register Doctor ───────────────────────────────────────────────────────

        @Operation(summary = "Register a new doctor", description = "Restricted to ADMIN role. Registers a new doctor account.")
        @ApiResponses({
                        @ApiResponse(responseCode = "201", description = "Doctor registered successfully"),
                        @ApiResponse(responseCode = "400", description = "Validation error or email already exists"),
                        @ApiResponse(responseCode = "403", description = "Access denied — Admin only")
        })
        @PostMapping("/register/doctor")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> registerDoctor(
                        @Valid @RequestBody DoctorRegisterRequestDTO dto) {
                log.info("POST /register/doctor called for email: {}", dto.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED)
                                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Doctor registered successfully")
                                                .data(authService.registerDoctor(dto))
                                                .build());
        }

        // ── Login ─────────────────────────────────────────────────────────────────

        @Operation(summary = "User login",
                   description = "Authenticate with email and password to receive an access token and a refresh token.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Login successful — access & refresh tokens returned"),
                        @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
        @PostMapping("/login")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> login(
                        @Valid @RequestBody LoginRequestDTO dto) {
                log.info("POST /login called for email: {}", dto.getEmail());
                return ResponseEntity.ok(
                                StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Login successful")
                                                .data(authService.login(dto))
                                                .build());
        }

        // ── Refresh Token ─────────────────────────────────────────────────────────

        @Operation(summary = "Refresh access token",
                   description = "Exchange a valid refresh token for a new access token and a new refresh token (token rotation).")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tokens refreshed successfully"),
                        @ApiResponse(responseCode = "401", description = "Refresh token invalid or expired")
        })
        @PostMapping("/refresh")
        public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> refresh(
                        @Valid @RequestBody RefreshTokenRequestDTO dto) {
                log.info("POST /refresh called");
                return ResponseEntity.ok(
                                StandardResponseDTO.<AuthResponseDTO>builder()
                                                .success(true)
                                                .message("Token refreshed successfully")
                                                .data(authService.refreshToken(dto))
                                                .build());
        }

        // ── Logout ────────────────────────────────────────────────────────────────

        @Operation(summary = "Logout user",
                   description = "Invalidates the provided refresh token, effectively logging the user out.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Logged out successfully"),
                        @ApiResponse(responseCode = "401", description = "Refresh token invalid or already expired")
        })
        @PostMapping("/logout")
        public ResponseEntity<StandardResponseDTO<Void>> logout(
                        @Valid @RequestBody RefreshTokenRequestDTO dto) {
                log.info("POST /logout called");
                authService.logout(dto);
                return ResponseEntity.ok(
                                StandardResponseDTO.<Void>builder()
                                                .success(true)
                                                .message("Logged out successfully")
                                                .build());
        }
}