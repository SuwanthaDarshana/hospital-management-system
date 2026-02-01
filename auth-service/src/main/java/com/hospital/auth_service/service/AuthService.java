package com.hospital.auth_service.service;

import com.hospital.auth_service.dto.AuthResponseDTO;
import com.hospital.auth_service.dto.LoginRequestDTO;
import com.hospital.auth_service.dto.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO registerRequestDTO);
    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);

}
