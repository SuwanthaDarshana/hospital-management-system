package com.hospital.staff_service.dto;

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


    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Staff's first name", example = "John")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Staff's last name", example = "Doe")
    private String lastName;

    @Email(message = "Invalid email format")
    @Schema(description = "Doctor's email address", example = "john.doe@hospital.com")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10}$", message = "Phone number must be valid (10 digits)")
    @Schema(description = "Doctor's phone number", example = "0770000000")
    private String phone;

    @Schema(description = "Department must be include", example = "0770000000")
    private String department; // e.g RECEPTION, PHARMACY, LAB

    @Schema(description = "Staff's role in the system", example = "STAFF")
    private String role;       // e.g STAFF

    private boolean isActive = true;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address is too long")
    private String address;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")   //future date not allowed
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format (e.g., A+, O-)")
    private String bloodGroup;



}
