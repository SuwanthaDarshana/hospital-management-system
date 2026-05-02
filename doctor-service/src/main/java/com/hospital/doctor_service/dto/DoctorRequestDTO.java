package com.hospital.doctor_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Schema(description = "Request DTO for updating doctor information. All fields are optional — only provided fields are updated.")
public class DoctorRequestDTO {

    // Admin-only fields
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Doctor's first name", example = "John")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Doctor's last name", example = "Doe")
    private String lastName;

    @Email(message = "Invalid email format")
    @Schema(description = "Doctor's email address", example = "john.doe@hospital.com")
    private String email;

    @Schema(description = "Doctor's role in the system", example = "DOCTOR")
    private String role;

    // Admin & Doctor fields
    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be valid (10 digits)")
    @Schema(description = "Doctor's phone number", example = "0770000000")
    private String phone;

    @Schema(description = "Doctor's medical specialization", example = "Cardiology")
    private String specialization;

    @Schema(description = "Doctor's availability schedule in JSON format",
            example = "{\"monday\": \"9:00-17:00\", \"tuesday\": \"9:00-17:00\"}")
    private String availability;

    @Pattern(regexp = "^(NOT_SET|AVAILABLE|NOT_AVAILABLE)$", message = "availabilityStatus must be NOT_SET, AVAILABLE, or NOT_AVAILABLE")
    @Schema(description = "Doctor's availability status", example = "AVAILABLE")
    private String availabilityStatus;

    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "New password (leave blank to keep existing)", example = "securePassword123")
    private String password;
}

