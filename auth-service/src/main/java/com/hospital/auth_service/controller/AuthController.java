package com.hospital.auth_service.controller;

import com.hospital.auth_service.dto.AuthResponseDTO;
import com.hospital.auth_service.dto.LoginRequestDTO;
import com.hospital.auth_service.dto.RegisterRequestDTO;
import com.hospital.auth_service.dto.StandardResponseDTO;
import com.hospital.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<StandardResponseDTO<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.<AuthResponseDTO>builder()
                        .success(true)
                        .message("User registered successfully")
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
