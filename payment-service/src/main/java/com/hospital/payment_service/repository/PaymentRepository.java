package com.hospital.payment_service.repository;

import com.hospital.payment_service.entity.Payment;
import com.hospital.payment_service.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAppointmentId(Long appointmentId);
    List<Payment> findByPatientId(Long patientId);
    Optional<Payment> findByStripePaymentIntentId(String paymentIntentId);
    List<Payment> findByPatientIdAndStatus(Long patientId, PaymentStatus status);
}
