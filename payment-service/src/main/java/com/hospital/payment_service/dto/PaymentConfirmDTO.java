package com.hospital.payment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentConfirmDTO {

    @NotBlank(message = "Payment intent ID is required")
    private String paymentIntentId;
}
