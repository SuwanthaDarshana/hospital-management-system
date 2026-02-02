package com.hospital.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequestDTO {

    @Email
    @NotBlank
    private String email;

    private String username;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    private String phone;

    private String specialization;

    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank
    private String role;
}
