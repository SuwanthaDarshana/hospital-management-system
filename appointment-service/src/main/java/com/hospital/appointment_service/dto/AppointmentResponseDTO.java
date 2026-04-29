package com.hospital.appointment_service.dto;

import com.hospital.appointment_service.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Appointment details response")
public class AppointmentResponseDTO {

    private Long id;
    private Long patientAuthUserId;
    private String patientEmail;
    private String patientName;
    private Long doctorAuthUserId;
    private String doctorName;
    private String doctorSpecialization;
    private LocalDate appointmentDate;
    private String appointmentTime;
    private AppointmentStatus status;
    private String reason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
