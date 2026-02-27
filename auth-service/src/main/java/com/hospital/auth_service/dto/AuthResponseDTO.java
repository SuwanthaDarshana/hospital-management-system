package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Authentication response containing JWT access token, refresh token, and user info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponseDTO {

    @Schema(description = "Short-lived JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "Long-lived refresh token (UUID)", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;

    @Schema(description = "Access token type", example = "Bearer")
    private String tokenType;

    @Schema(description = "User's email address", example = "admin@hospital.com")
    private String email;

    @Schema(description = "User's role", example = "ADMIN")
    private String role;

    @Schema(description = "User's unique identifier", example = "1")
    private String id;
}
