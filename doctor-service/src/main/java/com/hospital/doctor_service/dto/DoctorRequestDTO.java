package com.hospital.doctor_service.dto;

import lombok.*;

@Data
public class DoctorRequestDTO {

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @Email(message = "Email must be valid")
    private String email;

    private String phone;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    private String availability; // JSON string


}
