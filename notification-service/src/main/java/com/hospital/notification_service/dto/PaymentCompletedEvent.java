package com.hospital.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private Long paymentId;
    private Long appointmentId;
    private String patientEmail;
    private BigDecimal amount;
    private String currency;
    private String stripePaymentIntentId;
}
