package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.config.RabbitConfig;
import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.AuthService;
import com.hospital.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final JwtUtil jwtUtil;
        private final PasswordEncoder passwordEncoder;
        private final RabbitTemplate rabbitTemplate;

        @Override
        @Transactional
        public AuthResponseDTO registerDoctor(DoctorRegisterRequestDTO dto) {

                if (userRepository.findByEmail(dto.getEmail()).isPresent())
                        throw new AccessDeniedException("Email already exists");

                User user = User.builder()
                                .email(dto.getEmail())
                                .password(passwordEncoder.encode(dto.getPassword()))
                                .role(Role.DOCTOR)
                                .build();

                userRepository.save(user);

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
                System.out.println(" RabbitMQ: Doctor registration event sent for " + user.getEmail());

                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

                return AuthResponseDTO.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .id(String.valueOf(user.getId()))
                                .build();
        }

        @Override
        @Transactional
        public AuthResponseDTO registerPatient(PatientRegisterRequestDTO dto) {

                if (userRepository.findByEmail(dto.getEmail()).isPresent())
                        throw new AccessDeniedException("Email already exists");

                User user = User.builder()
                                .email(dto.getEmail())
                                .password(passwordEncoder.encode(dto.getPassword()))
                                .role(Role.PATIENT)
                                .build();

                userRepository.save(user);

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
                System.out.println("RabbitMQ: Patient registration event sent for " + user.getEmail());

                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

                return AuthResponseDTO.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .id(String.valueOf(user.getId()))
                                .build();
        }

        @Override
        public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
                User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                                .orElseThrow(() -> new AccessDeniedException("Invalid credentials"));

                if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
                        throw new AccessDeniedException("Invalid credentials");
                }
                String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

                return AuthResponseDTO.builder()
                                .token(token)
                                .email(user.getEmail())
                                .role(user.getRole().name())
                                .id(String.valueOf(user.getId())) // get id from user
                                .build();
        }
}
