//package com.hospital.auth_service.dto;
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;
//import lombok.*;
//
//import java.time.LocalDate;
//
//@Schema(description = "Request body for user registration")
//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//public class RegisterRequestDTO {
//
//    @Schema(description = "User's email address", example = "john.doe@hospital.com")
//    @Email
//    @NotBlank
//    private String email;
//
//    @Schema(description = "User's first name", example = "John")
//    @NotBlank
//    private String firstName;
//
//    @Schema(description = "User's last name", example = "Doe")
//    @NotBlank
//    private String lastName;
//
//    @Schema(description = "User role (auto-set by endpoint: PATIENT or DOCTOR)", example = "PATIENT", accessMode = Schema.AccessMode.READ_ONLY)
//    @NotBlank
//    private String role;
//
//    @Schema(description = "Password — minimum 6 characters", example = "secret123")
//    @Size(min = 6, message = "Password must be at least 6 characters long")
//    private String password;
//
//    @Schema(description = "Contact phone number", example = "+94771234567")
//    private String phone;
//
//    // Doctor specific fields
//    @Schema(description = "Doctor's specialization (Doctor registration only)", example = "Cardiology")
//    private String specialization;
//
//    // Patient specific fields
//    @Schema(description = "Patient's home address (Patient registration only)", example = "123 Main St, Colombo")
//    private String address;
//
//    @Schema(description = "Patient's gender (Patient registration only)", example = "Male")
//    private String gender;
//
//    @Schema(description = "Patient's date of birth (Patient registration only)", example = "1990-05-15")
//    private LocalDate dateOfBirth;
//
//    @Schema(description = "Patient's blood group (Patient registration only)", example = "O+")
//    private String bloodGroup;
//
//    @Schema(description = "Whether the account is active", example = "true")
//    private boolean isActive;
//}
