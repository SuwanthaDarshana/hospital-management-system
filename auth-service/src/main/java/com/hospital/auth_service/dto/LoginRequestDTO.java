package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Schema(description = "Request body for user login")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequestDTO {

    @Schema(description = "Registered email address", example = "admin@hospital.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Account password", example = "admin123")
    @NotBlank
    private String password;
}
