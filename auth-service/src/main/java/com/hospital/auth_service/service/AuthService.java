package com.hospital.auth_service.service;

import com.hospital.auth_service.dto.*;

public interface AuthService {
    AuthResponseDTO registerDoctor(DoctorRegisterRequestDTO dto);

    AuthResponseDTO registerPatient(PatientRegisterRequestDTO dto);

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);
}
