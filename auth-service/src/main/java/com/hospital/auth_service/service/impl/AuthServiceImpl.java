package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.config.RabbitConfig;
import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.entity.RefreshToken;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.exception.ResourceNotFoundException;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.AuthService;
import com.hospital.auth_service.service.RefreshTokenService;
import com.hospital.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final RabbitTemplate rabbitTemplate;
        private final RefreshTokenService refreshTokenService;

        // ─── Helper ──────────────────────────────────────────────────────────────

        private AuthResponseDTO buildAuthResponse(User user) {
                String accessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
                log.info("Auth response built for user: {} with role: {}", user.getEmail(), user.getRole());
                return AuthResponseDTO.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken.getToken())
                                .tokenType("Bearer")
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .id(String.valueOf(user.getId()))
                                .build();
        }

        // ─── Register Doctor ──────────────────────────────────────────────────────

        @Override
        @Transactional
        public AuthResponseDTO registerDoctor(DoctorRegisterRequestDTO dto) {
                log.info("Registering new doctor with email: {}", dto.getEmail());

                if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                        log.warn("Doctor registration failed — email already exists: {}", dto.getEmail());
                        throw new AccessDeniedException("Email already exists");
                }

                User user = User.builder()
                                .email(dto.getEmail())
                                .password(passwordEncoder.encode(dto.getPassword()))
                                .role(Role.DOCTOR)
                                .build();

                userRepository.save(user);
                log.info("Doctor user saved with id: {}", user.getId());

                DoctorCreatedEvent event = DoctorCreatedEvent.builder()
                                .authUserId(user.getId())
                                .email(user.getEmail())
                                .firstName(dto.getFirstName())
                                .lastName(dto.getLastName())
                                .phone(dto.getPhone())
                                .specialization(dto.getSpecialization())
                                .role(Role.DOCTOR.name())
                                .build();

                rabbitTemplate.convertAndSend(
                                RabbitConfig.DOCTOR_EXCHANGE,
                                RabbitConfig.DOCTOR_ROUTING_KEY,
                                event);
                log.info("RabbitMQ: Doctor registration event sent for {}", user.getEmail());

                return buildAuthResponse(user);
        }

        // ─── Register Patient ─────────────────────────────────────────────────────

        @Override
        @Transactional
        public AuthResponseDTO registerPatient(PatientRegisterRequestDTO dto) {
                log.info("Registering new patient with email: {}", dto.getEmail());

                if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                        log.warn("Patient registration failed — email already exists: {}", dto.getEmail());
                        throw new AccessDeniedException("Email already exists");
                }

                User user = User.builder()
                                .email(dto.getEmail())
                                .password(passwordEncoder.encode(dto.getPassword()))
                                .role(Role.PATIENT)
                                .build();

                userRepository.save(user);
                log.info("Patient user saved with id: {}", user.getId());

                PatientCreatedEvent event = PatientCreatedEvent.builder()
                                .authUserId(user.getId())
                                .email(user.getEmail())
                                .firstName(dto.getFirstName())
                                .lastName(dto.getLastName())
                                .phone(dto.getPhone())
                                .address(dto.getAddress())
                                .gender(dto.getGender())
                                .dateOfBirth(dto.getDateOfBirth())
                                .bloodGroup(dto.getBloodGroup())
                                .isActive(true)
                                .build();

                rabbitTemplate.convertAndSend(
                                RabbitConfig.PATIENT_EXCHANGE,
                                RabbitConfig.PATIENT_ROUTING_KEY,
                                event);
                log.info("RabbitMQ: Patient registration event sent for {}", user.getEmail());

                return buildAuthResponse(user);
        }

        // ─── Login ────────────────────────────────────────────────────────────────

        @Override
        public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
                log.info("Login attempt for email: {}", loginRequestDTO.getEmail());

                User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                                .orElseThrow(() -> {
                                        log.warn("Login failed — user not found: {}", loginRequestDTO.getEmail());
                                        return new AccessDeniedException("Invalid credentials");
                                });

                if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                        log.warn("Login failed — invalid password for email: {}", loginRequestDTO.getEmail());
                        throw new AccessDeniedException("Invalid credentials");
                }

                log.info("Login successful for email: {}", user.getEmail());
                return buildAuthResponse(user);
        }

        // ─── Refresh Token ────────────────────────────────────────────────────────

        @Override
        @Transactional
        public AuthResponseDTO refreshToken(RefreshTokenRequestDTO dto) {
                log.info("Refresh token request received");

                RefreshToken existingToken = refreshTokenService.verifyRefreshToken(dto.getRefreshToken());

                // Re-fetch the user to avoid detached-entity issues after token deletion
                Long userId = existingToken.getUser().getId();
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                // Revoke old token and issue a new one (token rotation)
                refreshTokenService.revokeAllUserTokens(user);

                String newAccessToken = jwtUtil.generateAccessToken(user.getEmail(), user.getRole().name());
                RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

                log.info("Tokens rotated successfully for user: {}", user.getEmail());

                return AuthResponseDTO.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken.getToken())
                                .tokenType("Bearer")
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .id(String.valueOf(user.getId()))
                                .build();
        }

        // ─── Logout ───────────────────────────────────────────────────────────────

        @Override
        @Transactional
        public void logout(RefreshTokenRequestDTO dto) {
                log.info("Logout request received");

                RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(dto.getRefreshToken());

                // Re-fetch user to avoid detached entity
                Long userId = refreshToken.getUser().getId();
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

                refreshTokenService.revokeAllUserTokens(user);
                log.info("User logged out successfully: {}", user.getEmail());
        }
}

