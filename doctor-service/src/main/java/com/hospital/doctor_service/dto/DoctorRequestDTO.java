package com.hospital.doctor_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.Email;

@Data
@Schema(description = "Request DTO for creating or updating doctor information")
public class DoctorRequestDTO {

    //Admin can update this
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Doctor's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Doctor's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Doctor's email address", example = "john.doe@hospital.com")
    private String email;

    @Schema(description = "Doctor's role in the system", example = "DOCTOR")
    private String role;


    //Both admin and doctor can update these
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be valid (10)")
    @Schema(description = "Doctor's phone number", example = "0770000000")
    private String phone;

    @NotBlank(message = "Specialization is required")
    @Schema(description = "Doctor's medical specialization", example = "Cardiology")
    private String specialization;

    @NotBlank(message = "Availability is required")
    @Schema(description = "Doctor's availability schedule in JSON format", example = "{\"monday\": \"9:00-17:00\", \"tuesday\": \"9:00-17:00\"}")
    private String availability;

    @NotBlank(message = "Password is required")
    @Schema(description = "Doctor's password (for account updates)", example = "securePassword123")
    private String password;


}
