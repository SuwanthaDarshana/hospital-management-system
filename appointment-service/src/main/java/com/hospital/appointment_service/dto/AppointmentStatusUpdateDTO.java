package com.hospital.appointment_service.dto;

import com.hospital.appointment_service.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Request body to update appointment status")
public class AppointmentStatusUpdateDTO {

    @NotNull(message = "Status is required")
    @Schema(description = "New appointment status", example = "CONFIRMED")
    private AppointmentStatus status;

    @Schema(description = "Doctor's notes (optional, used when completing an appointment)", example = "Patient prescribed antibiotics.")
    private String notes;
}
