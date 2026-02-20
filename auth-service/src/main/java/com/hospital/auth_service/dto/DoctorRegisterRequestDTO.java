package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Schema(description = "Request body for doctor registration (Admin only)")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorRegisterRequestDTO {

    @Schema(description = "Doctor's email address", example = "dr.smith@hospital.com")
    @Email
    @NotBlank
    private String email;

    @Schema(description = "Doctor's first name", example = "John")
    @NotBlank
    private String firstName;

    @Schema(description = "Doctor's last name", example = "Smith")
    @NotBlank
    private String lastName;

    @Schema(description = "Password — minimum 6 characters", example = "secret123")
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(description = "Contact phone number", example = "+94771234567")
    private String phone;

    @Schema(description = "Doctor's specialization", example = "Cardiology")
    @NotBlank
    private String specialization;

}
