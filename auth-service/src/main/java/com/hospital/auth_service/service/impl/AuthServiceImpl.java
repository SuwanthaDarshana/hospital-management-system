package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.config.RabbitConfig;
import com.hospital.auth_service.dto.*;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.AuthService;
import com.hospital.auth_service.util.JwtUtil;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent())
                throw new BadRequestException("Email already exists");
        //This is the same as above but good for production
        //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");

        User user = User.builder()
                .email(registerRequestDTO.getEmail())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(Role.valueOf(registerRequestDTO.getRole()))
                //.username(registerRequestDTO.getRole().equals("DOCTOR") ? registerRequestDTO.getEmail() : registerRequestDTO.getUsername()) // only set username for non-doctors
                .build();

        userRepository.save(user);

        if (user.getRole() == Role.DOCTOR) {
            DoctorCreatedEvent event = DoctorCreatedEvent.builder()
                    .authUserId(user.getId())
                    .email(user.getEmail())
                    .firstName(registerRequestDTO.getFirstName())
                    .lastName(registerRequestDTO.getLastName())
                    .phone(registerRequestDTO.getPhone())
                    .specialization(registerRequestDTO.getSpecialization())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.DOCTOR_EXCHANGE,
                    RabbitConfig.DOCTOR_ROUTING_KEY,
                    event
            );
            System.out.println("✅ RabbitMQ: Doctor registration event sent for " + user.getEmail());
        }else if (user.getRole() == Role.PATIENT) {
            // Send to Patient Service
            PatientCreatedEvent event = PatientCreatedEvent.builder()
                    .authUserId(user.getId())
                    .email(user.getEmail())
                    .firstName(registerRequestDTO.getFirstName())
                    .lastName(registerRequestDTO.getLastName())
                    .phone(registerRequestDTO.getPhone())
                    .address(registerRequestDTO.getAddress())
                    .gender(registerRequestDTO.getGender())
                    .dateOfBirth(registerRequestDTO.getDateOfBirth())
                    .bloodGroup(registerRequestDTO.getBloodGroup())
                    .isActive(true)
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PATIENT_EXCHANGE,
                    RabbitConfig.PATIENT_ROUTING_KEY,
                    event
            );
            System.out.println("✅ RabbitMQ: Patient registration event sent for " + user.getEmail());
        }


        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
//                .username(user.getUsername())
                .id(String.valueOf(user.getId())) // get id from user
                .build();
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
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
