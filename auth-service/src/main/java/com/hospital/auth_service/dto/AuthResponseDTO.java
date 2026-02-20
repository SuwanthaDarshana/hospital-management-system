package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Authentication response containing JWT token and user info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "User's email address", example = "admin@hospital.com")
    private String email;

    @Schema(description = "User's role", example = "ADMIN")
    private String role;

    @Schema(description = "User's unique identifier", example = "1")
    private String id;
}
