package com.hospital.appointment_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request body for booking an appointment")
public class AppointmentRequestDTO {

    @NotNull(message = "Patient auth user ID is required")
    @Schema(description = "Patient's auth user ID", example = "3")
    private Long patientAuthUserId;

    @NotBlank(message = "Patient name is required")
    @Schema(description = "Patient's full name", example = "John Doe")
    private String patientName;

    @NotNull(message = "Doctor auth user ID is required")
    @Schema(description = "Doctor's auth user ID", example = "2")
    private Long doctorAuthUserId;

    @NotBlank(message = "Doctor name is required")
    @Schema(description = "Doctor's full name", example = "Dr. Jane Smith")
    private String doctorName;

    @Schema(description = "Doctor's specialization", example = "Cardiology")
    private String doctorSpecialization;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    @Schema(description = "Date of the appointment", example = "2026-05-15")
    private LocalDate appointmentDate;

    @NotBlank(message = "Appointment time is required")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "Time must be in HH:mm format")
    @Schema(description = "Time slot for the appointment in HH:mm format", example = "09:30")
    private String appointmentTime;

    @Size(max = 500, message = "Reason must not exceed 500 characters")
    @Schema(description = "Reason for the appointment", example = "Chest pain and shortness of breath")
    private String reason;
}
