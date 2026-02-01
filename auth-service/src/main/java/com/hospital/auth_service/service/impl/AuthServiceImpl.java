package com.hospital.auth_service.service.impl;

import com.hospital.auth_service.dto.AuthResponseDTO;
import com.hospital.auth_service.dto.LoginRequestDTO;
import com.hospital.auth_service.dto.RegisterRequestDTO;
import com.hospital.auth_service.entity.Role;
import com.hospital.auth_service.entity.User;
import com.hospital.auth_service.repository.UserRepository;
import com.hospital.auth_service.service.AuthService;
import com.hospital.auth_service.util.JwtUtil;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;




    @Override
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.findByEmail(registerRequestDTO.getEmail()).isPresent())
                throw new BadRequestException("Email already exists");
        //This is the same as above but good for production
        //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");

        User user = User.builder()
                .email(registerRequestDTO.getEmail())
                .username(registerRequestDTO.getUsername())
                .password(passwordEncoder.encode(registerRequestDTO.getPassword()))
                .role(Role.valueOf(registerRequestDTO.getRole()))
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .username(user.getUsername())
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
                .username(user.getUsername())
                .build();
    }
}
