package com.hospital.notification_service.service;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetLink);
    void sendPaymentConfirmationEmail(String toEmail, String paymentId, String amount, String currency);
}
