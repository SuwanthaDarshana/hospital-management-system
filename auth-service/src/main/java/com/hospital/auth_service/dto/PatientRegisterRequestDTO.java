package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Schema(description = "Request body for patient registration")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientRegisterRequestDTO {

    @Schema(description = "Patient's email address", example = "jane.doe@gmail.com")
    @Email
    @NotBlank()
    private String email;

    @Schema(description = "Patient's first name", example = "Jane")
    @NotBlank
    private String firstName;

    @Schema(description = "Patient's last name", example = "Doe")
    @NotBlank
    private String lastName;

    @Schema(description = "Password — minimum 6 characters", example = "secret123")
    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @Schema(description = "Contact phone number", example = "+94771234567")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be valid (10 digits)")
    private String phone;

    @Schema(description = "Patient's home address", example = "123 Main St, Colombo")
    @NotBlank(message = "Address is required")
    private String address;

    @Schema(description = "Patient's gender", example = "Female")
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Schema(description = "Patient's date of birth (yyyy-MM-dd)", example = "1990-05-15")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Schema(description = "Patient's blood group", example = "O+")
    private String bloodGroup;
}
