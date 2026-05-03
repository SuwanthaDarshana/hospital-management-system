package com.hospital.payment_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "Appointment ID is required")
    private Long appointmentId;

    @NotNull(message = "Patient ID is required")
    private Long patientId;

    @Email(message = "Invalid patient email")
    private String patientEmail;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter code")
    private String currency;

    private String description;
}
