package com.hospital.staff_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffRequestDTO {


    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Staff's first name", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Staff's last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Doctor's email address", example = "john.doe@hospital.com")
    private String email;

    @Schema(description = "Doctor's phone number", example = "0770000000")
    private String phone;

    @Schema(description = "Department must be include", example = "0770000000")
    private String department; // e.g RECEPTION, PHARMACY, LAB

    @Schema(description = "Staff's role in the system", example = "STAFF")
    private String role;       // e.g STAFF

    @JsonProperty("isActive")
    @Builder.Default
    private boolean isActive = true;

    @Size(max = 255, message = "Address is too long")
    private String address;

    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format (e.g., A+, O-)")
    private String bloodGroup;



}
