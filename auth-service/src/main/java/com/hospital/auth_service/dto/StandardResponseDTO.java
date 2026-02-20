package com.hospital.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "Standard API response wrapper")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponseDTO<T> {

    @Schema(description = "Indicates whether the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Human-readable message describing the result", example = "Login successful")
    private String message;

    @Schema(description = "Response payload data")
    private T data;
}
