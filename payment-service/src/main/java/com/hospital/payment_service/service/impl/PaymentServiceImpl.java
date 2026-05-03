package com.hospital.payment_service.service.impl;

import com.hospital.payment_service.config.RabbitConfig;
import com.hospital.payment_service.dto.*;
import com.hospital.payment_service.entity.Payment;
import com.hospital.payment_service.enums.PaymentStatus;
import com.hospital.payment_service.exception.ResourceNotFoundException;
import com.hospital.payment_service.repository.PaymentRepository;
import com.hospital.payment_service.service.MockStripeService;
import com.hospital.payment_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MockStripeService stripeService;
    private final RabbitTemplate rabbitTemplate;

    @Override
    @Transactional
    public PaymentResponseDTO createPaymentIntent(PaymentRequestDTO dto) {
        log.info("Creating payment intent for appointment: {}, patient: {}", dto.getAppointmentId(), dto.getPatientId());

        String paymentIntentId = stripeService.createPaymentIntent(dto.getAmount(), dto.getCurrency());
        String clientSecret = stripeService.buildClientSecret(paymentIntentId);

        Payment payment = Payment.builder()
                .appointmentId(dto.getAppointmentId())
                .patientId(dto.getPatientId())
                .patientEmail(dto.getPatientEmail())
                .amount(dto.getAmount())
                .currency(dto.getCurrency().toUpperCase())
                .status(PaymentStatus.PENDING)
                .stripePaymentIntentId(paymentIntentId)
                .stripeClientSecret(clientSecret)
                .description(dto.getDescription())
                .build();

        paymentRepository.save(payment);
        log.info("Payment intent created: {}", paymentIntentId);

        return toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO confirmPayment(PaymentConfirmDTO dto) {
        log.info("Confirming payment intent: {}", dto.getPaymentIntentId());

        Payment payment = paymentRepository.findByStripePaymentIntentId(dto.getPaymentIntentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for intent: " + dto.getPaymentIntentId()));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment is already completed");
        }

        payment.setStatus(PaymentStatus.PROCESSING);
        paymentRepository.save(payment);

        boolean success = stripeService.confirmPaymentIntent(dto.getPaymentIntentId());

        if (success) {
            payment.setStatus(PaymentStatus.COMPLETED);
            paymentRepository.save(payment);
            log.info("Payment completed successfully: {}", payment.getId());

            PaymentCompletedEvent event = PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .appointmentId(payment.getAppointmentId())
                    .patientEmail(payment.getPatientEmail())
                    .amount(payment.getAmount())
                    .currency(payment.getCurrency())
                    .stripePaymentIntentId(payment.getStripePaymentIntentId())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.PAYMENT_COMPLETED_EXCHANGE,
                    RabbitConfig.PAYMENT_COMPLETED_ROUTING_KEY,
                    event);
            log.info("RabbitMQ: Payment completed event published for payment {}", payment.getId());
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Payment confirmation failed");
            paymentRepository.save(payment);
            log.warn("Payment failed for intent: {}", dto.getPaymentIntentId());
        }

        return toDTO(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO refundPayment(Long paymentId) {
        log.info("Refunding payment: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Only completed payments can be refunded");
        }

        stripeService.refundPaymentIntent(payment.getStripePaymentIntentId());
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        log.info("Payment refunded: {}", paymentId);
        return toDTO(payment);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        return toDTO(payment);
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByAppointment(Long appointmentId) {
        return paymentRepository.findByAppointmentId(appointmentId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<PaymentResponseDTO> getPaymentsByPatient(Long patientId) {
        return paymentRepository.findByPatientId(patientId).stream()
                .map(this::toDTO)
                .toList();
    }

    private PaymentResponseDTO toDTO(Payment payment) {
        return PaymentResponseDTO.builder()
                .id(payment.getId())
                .appointmentId(payment.getAppointmentId())
                .patientId(payment.getPatientId())
                .patientEmail(payment.getPatientEmail())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .stripePaymentIntentId(payment.getStripePaymentIntentId())
                .clientSecret(payment.getStripeClientSecret())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
