package com.hospital.auth_service.service;

import com.hospital.auth_service.dto.ForgotPasswordRequestDTO;
import com.hospital.auth_service.dto.ResetPasswordRequestDTO;

public interface PasswordResetService {
    void forgotPassword(ForgotPasswordRequestDTO dto);
    void resetPassword(ResetPasswordRequestDTO dto);
}
