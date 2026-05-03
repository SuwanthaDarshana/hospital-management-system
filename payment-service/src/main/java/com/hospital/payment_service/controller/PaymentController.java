package com.hospital.payment_service.controller;

import com.hospital.payment_service.dto.*;
import com.hospital.payment_service.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment processing via Stripe mock")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Create a payment intent", description = "Creates a Stripe payment intent for an appointment.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment intent created"),
            @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping("/create-intent")
    public ResponseEntity<StandardResponseDTO<PaymentResponseDTO>> createPaymentIntent(
            @Valid @RequestBody PaymentRequestDTO dto) {
        log.info("POST /create-intent for appointment: {}", dto.getAppointmentId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.<PaymentResponseDTO>builder()
                        .success(true)
                        .message("Payment intent created")
                        .data(paymentService.createPaymentIntent(dto))
                        .build());
    }

    @Operation(summary = "Confirm a payment", description = "Confirms a Stripe payment intent and marks it as completed.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment confirmed"),
            @ApiResponse(responseCode = "404", description = "Payment intent not found")
    })
    @PostMapping("/confirm")
    public ResponseEntity<StandardResponseDTO<PaymentResponseDTO>> confirmPayment(
            @Valid @RequestBody PaymentConfirmDTO dto) {
        log.info("POST /confirm for intent: {}", dto.getPaymentIntentId());
        return ResponseEntity.ok(
                StandardResponseDTO.<PaymentResponseDTO>builder()
                        .success(true)
                        .message("Payment confirmed")
                        .data(paymentService.confirmPayment(dto))
                        .build());
    }

    @Operation(summary = "Refund a payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment refunded"),
            @ApiResponse(responseCode = "404", description = "Payment not found")
    })
    @PostMapping("/{id}/refund")
    public ResponseEntity<StandardResponseDTO<PaymentResponseDTO>> refundPayment(@PathVariable Long id) {
        log.info("POST /{}/refund", id);
        return ResponseEntity.ok(
                StandardResponseDTO.<PaymentResponseDTO>builder()
                        .success(true)
                        .message("Payment refunded")
                        .data(paymentService.refundPayment(id))
                        .build());
    }

    @Operation(summary = "Get all payments (admin)", description = "Returns all payments across all patients. Admin only.")
    @GetMapping
    public ResponseEntity<StandardResponseDTO<List<PaymentResponseDTO>>> getAll() {
        return ResponseEntity.ok(
                StandardResponseDTO.<List<PaymentResponseDTO>>builder()
                        .success(true)
                        .message("Payments retrieved")
                        .data(paymentService.getAllPayments())
                        .build());
    }

    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseDTO<PaymentResponseDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                StandardResponseDTO.<PaymentResponseDTO>builder()
                        .success(true)
                        .message("Payment retrieved")
                        .data(paymentService.getPaymentById(id))
                        .build());
    }

    @Operation(summary = "Get payments by appointment")
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<StandardResponseDTO<List<PaymentResponseDTO>>> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(
                StandardResponseDTO.<List<PaymentResponseDTO>>builder()
                        .success(true)
                        .message("Payments retrieved")
                        .data(paymentService.getPaymentsByAppointment(appointmentId))
                        .build());
    }

    @Operation(summary = "Get payments by patient")
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<StandardResponseDTO<List<PaymentResponseDTO>>> getByPatient(
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                StandardResponseDTO.<List<PaymentResponseDTO>>builder()
                        .success(true)
                        .message("Payments retrieved")
                        .data(paymentService.getPaymentsByPatient(patientId))
                        .build());
    }
}
