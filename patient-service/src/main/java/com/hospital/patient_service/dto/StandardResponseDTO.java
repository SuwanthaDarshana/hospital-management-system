package com.hospital.patient_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponseDTO<T> {

    @Schema(description = "Response status", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Patient fetched successfully")
    private String message;

    @Schema(description = "Response data payload")
    private T data;

}
