package com.hospital.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotBlank
    private String role;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    private String phone;

    //Doctor specific fields
    private String specialization;

    //Patient specific fields
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private boolean isActive;




}
