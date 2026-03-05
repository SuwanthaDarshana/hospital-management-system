package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Schema(description = "Request body for staff registration (Admin only)")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffRegisterRequestDTO {

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
    private String phone;

    // Fixed as "STAFF" but passed from frontend for flexibility
    @Schema(description = "Role Is required")
    @NotBlank(message = "Role is required")
    private String role;

    // Staff Specific Field
    @Schema(description = "Department",example = "RECEPTION" )
    @NotBlank(message = "Department is required")
    private String department; // e.g., RECEPTION, BILLING, PHARMACY

    @Schema(description = "Staff's home address", example = "123 Main St, Colombo")
    private String address;

    @Schema(description = "Staff's gender", example = "Female")
    private String gender;

    @Schema(description = "Staff's date of birth (yyyy-MM-dd)", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Staff's blood group", example = "O+")
    private String bloodGroup;
}
