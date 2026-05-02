package com.hospital.doctor_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@Schema(description = "Response DTO containing doctor information")
public class DoctorResponseDTO {

    @Schema(description = "Unique identifier of the doctor", example = "1")
    private Long id;

    @Schema(description = "Authentication user ID linked to this doctor", example = "101")
    private Long authUserId;

    @Schema(description = "Doctor's first name", example = "John")
    private String firstName;

    @Schema(description = "Doctor's last name", example = "Doe")
    private String lastName;

    @Schema(description = "Doctor's email address", example = "john.doe@hospital.com")
    private String email;

    @Schema(description = "Doctor's phone number", example = "0770000000")
    private String phone;

    @Schema(description = "Doctor's medical specialization", example = "Cardiology")
    private String specialization;

    @Schema(description = "Doctor's availability schedule in JSON format", example = "{\"monday\": \"9:00-17:00\", \"tuesday\": \"9:00-17:00\"}")
    private String availability;

    @Schema(description = "Doctor's availability status", example = "AVAILABLE", allowableValues = {"NOT_SET", "AVAILABLE", "NOT_AVAILABLE"})
    private String availabilityStatus;
}
