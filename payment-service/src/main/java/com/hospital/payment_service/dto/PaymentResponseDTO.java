package com.hospital.payment_service.dto;

import com.hospital.payment_service.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private Long appointmentId;
    private Long patientId;
    private String patientEmail;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String stripePaymentIntentId;
    private String clientSecret;
    private String description;
    private LocalDateTime createdAt;
}
