package com.hospital.doctor_service.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class DoctorRequestDTO {



    // Admin can update these fields
    private String firstName;
    private String lastName;

    @Email(message = "Invalid email format")
    private String email;

    
    private String role;

    // Both Admin and Doctor can update these
    private String phone;
    private String specialization;
    private String availability; // JSON string
    private String password;


}
