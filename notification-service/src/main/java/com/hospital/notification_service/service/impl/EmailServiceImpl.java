package com.hospital.notification_service.service.impl;

import com.hospital.notification_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String fromEmail;

    @Value("${notification.mail.mock:true}")
    private boolean mockMode;

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        String subject = "Hospital Management System — Password Reset";
        String body = buildPasswordResetHtml(toEmail, resetLink);
        send(toEmail, subject, body);
    }

    @Override
    public void sendPaymentConfirmationEmail(String toEmail, String paymentId, String amount, String currency) {
        String subject = "Payment Confirmation — Hospital Management System";
        String body = buildPaymentConfirmationHtml(toEmail, paymentId, amount, currency);
        send(toEmail, subject, body);
    }

    private void send(String toEmail, String subject, String htmlBody) {
        if (mockMode) {
            log.info("[MOCK EMAIL] To: {} | Subject: {} | Body snippet: {}",
                    toEmail, subject, htmlBody.substring(0, Math.min(120, htmlBody.length())));
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to: {} | Subject: {}", toEmail, subject);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    private String buildPasswordResetHtml(String email, String resetLink) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                  <div style="background-color: #1a73e8; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Hospital Management System</h1>
                  </div>
                  <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; border: 1px solid #e0e0e0;">
                    <h2 style="color: #333;">Password Reset Request</h2>
                    <p style="color: #555;">We received a request to reset the password for <strong>%s</strong>.</p>
                    <p style="color: #555;">Click the button below to reset your password. This link expires in <strong>15 minutes</strong>.</p>
                    <div style="text-align: center; margin: 30px 0;">
                      <a href="%s" style="background-color: #1a73e8; color: white; padding: 14px 28px;
                         text-decoration: none; border-radius: 6px; font-size: 16px;">Reset Password</a>
                    </div>
                    <p style="color: #888; font-size: 13px;">If you did not request a password reset, please ignore this email.</p>
                    <p style="color: #888; font-size: 12px;">Or copy this link: %s</p>
                  </div>
                </body>
                </html>
                """.formatted(email, resetLink, resetLink);
    }

    private String buildPaymentConfirmationHtml(String email, String paymentId, String amount, String currency) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                  <div style="background-color: #1a73e8; padding: 20px; border-radius: 8px 8px 0 0;">
                    <h1 style="color: white; margin: 0;">Hospital Management System</h1>
                  </div>
                  <div style="background-color: #f9f9f9; padding: 30px; border-radius: 0 0 8px 8px; border: 1px solid #e0e0e0;">
                    <h2 style="color: #2e7d32;">Payment Confirmed</h2>
                    <p style="color: #555;">Dear <strong>%s</strong>, your payment has been successfully processed.</p>
                    <table style="width: 100%%; border-collapse: collapse; margin: 20px 0;">
                      <tr style="background-color: #e8f5e9;">
                        <td style="padding: 10px; border: 1px solid #ddd;"><strong>Payment ID</strong></td>
                        <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr>
                        <td style="padding: 10px; border: 1px solid #ddd;"><strong>Amount</strong></td>
                        <td style="padding: 10px; border: 1px solid #ddd;">%s %s</td>
                      </tr>
                    </table>
                    <p style="color: #888; font-size: 13px;">Thank you for your payment. Please keep this as your receipt.</p>
                  </div>
                </body>
                </html>
                """.formatted(email, paymentId, amount, currency.toUpperCase());
    }
}
