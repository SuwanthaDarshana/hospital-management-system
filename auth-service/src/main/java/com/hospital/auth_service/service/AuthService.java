package com.hospital.auth_service.service;

import com.hospital.auth_service.dto.*;

public interface AuthService {
    AuthResponseDTO registerDoctor(DoctorRegisterRequestDTO dto);

    AuthResponseDTO registerPatient(PatientRegisterRequestDTO dto);

    AuthResponseDTO registerStaff(StaffRegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);

    AuthResponseDTO refreshToken(RefreshTokenRequestDTO dto);

    void logout(RefreshTokenRequestDTO dto);
}
