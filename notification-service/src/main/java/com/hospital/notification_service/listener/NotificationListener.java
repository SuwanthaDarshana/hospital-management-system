package com.hospital.notification_service.listener;

import com.hospital.notification_service.config.RabbitConfig;
import com.hospital.notification_service.dto.PasswordResetEvent;
import com.hospital.notification_service.dto.PaymentCompletedEvent;
import com.hospital.notification_service.entity.NotificationLog;
import com.hospital.notification_service.enums.NotificationStatus;
import com.hospital.notification_service.enums.NotificationType;
import com.hospital.notification_service.repository.NotificationLogRepository;
import com.hospital.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final EmailService emailService;
    private final NotificationLogRepository logRepository;

    @RabbitListener(queues = RabbitConfig.PASSWORD_RESET_QUEUE)
    public void handlePasswordReset(PasswordResetEvent event) {
        log.info("Received password reset event for: {}", event.getEmail());

        NotificationLog notificationLog = NotificationLog.builder()
                .type(NotificationType.PASSWORD_RESET)
                .recipientEmail(event.getEmail())
                .subject("Hospital Management System — Password Reset")
                .body("Reset link: " + event.getResetLink())
                .status(NotificationStatus.SENT)
                .build();

        try {
            emailService.sendPasswordResetEmail(event.getEmail(), event.getResetLink());
            logRepository.save(notificationLog);
            log.info("Password reset notification processed for: {}", event.getEmail());
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", event.getEmail(), e.getMessage());
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
            logRepository.save(notificationLog);
        }
    }

    @RabbitListener(queues = RabbitConfig.PAYMENT_COMPLETED_QUEUE)
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received payment completed event for: {}", event.getPatientEmail());

        String amountStr = event.getAmount().toPlainString();

        NotificationLog notificationLog = NotificationLog.builder()
                .type(NotificationType.PAYMENT_CONFIRMATION)
                .recipientEmail(event.getPatientEmail())
                .subject("Payment Confirmation — Hospital Management System")
                .body("Payment ID: " + event.getPaymentId() + " | Amount: " + amountStr + " " + event.getCurrency())
                .status(NotificationStatus.SENT)
                .build();

        try {
            emailService.sendPaymentConfirmationEmail(
                    event.getPatientEmail(),
                    String.valueOf(event.getPaymentId()),
                    amountStr,
                    event.getCurrency());
            logRepository.save(notificationLog);
            log.info("Payment confirmation notification processed for: {}", event.getPatientEmail());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email to {}: {}", event.getPatientEmail(), e.getMessage());
            notificationLog.setStatus(NotificationStatus.FAILED);
            notificationLog.setErrorMessage(e.getMessage());
            logRepository.save(notificationLog);
        }
    }
}
