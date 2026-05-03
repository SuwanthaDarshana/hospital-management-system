package com.hospital.payment_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Service
public class MockStripeService {

    public String createPaymentIntent(BigDecimal amount, String currency) {
        String paymentIntentId = "pi_mock_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        log.info("[MOCK STRIPE] Created payment intent: {} for {} {}", paymentIntentId, amount, currency.toUpperCase());
        return paymentIntentId;
    }

    public String buildClientSecret(String paymentIntentId) {
        return paymentIntentId + "_secret_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    public boolean confirmPaymentIntent(String paymentIntentId) {
        log.info("[MOCK STRIPE] Payment intent confirmed: {}", paymentIntentId);
        return true;
    }

    public boolean refundPaymentIntent(String paymentIntentId) {
        log.info("[MOCK STRIPE] Payment intent refunded: {}", paymentIntentId);
        return true;
    }
}
