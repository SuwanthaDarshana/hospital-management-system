package com.hospital.payment_service.service;

import com.hospital.payment_service.dto.PaymentConfirmDTO;
import com.hospital.payment_service.dto.PaymentRequestDTO;
import com.hospital.payment_service.dto.PaymentResponseDTO;

import java.util.List;

public interface PaymentService {
    PaymentResponseDTO createPaymentIntent(PaymentRequestDTO dto);
    PaymentResponseDTO confirmPayment(PaymentConfirmDTO dto);
    PaymentResponseDTO refundPayment(Long paymentId);
    PaymentResponseDTO getPaymentById(Long id);
    List<PaymentResponseDTO> getAllPayments();
    List<PaymentResponseDTO> getPaymentsByAppointment(Long appointmentId);
    List<PaymentResponseDTO> getPaymentsByPatient(Long patientId);
}
