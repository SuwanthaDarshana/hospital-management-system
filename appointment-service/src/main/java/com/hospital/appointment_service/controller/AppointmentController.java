package com.hospital.appointment_service.controller;

import com.hospital.appointment_service.dto.AppointmentRequestDTO;
import com.hospital.appointment_service.dto.AppointmentResponseDTO;
import com.hospital.appointment_service.dto.AppointmentStatusUpdateDTO;
import com.hospital.appointment_service.dto.StandardResponseDTO;
import com.hospital.appointment_service.enums.AppointmentStatus;
import com.hospital.appointment_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Service", description = "Manage hospital appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Book a new appointment", description = "Patients can book an appointment with a doctor.")
    public ResponseEntity<StandardResponseDTO<AppointmentResponseDTO>> bookAppointment(
            @Valid @RequestBody AppointmentRequestDTO request,
            @RequestHeader("X-User-Email") String patientEmail) {

        AppointmentResponseDTO response = appointmentService.bookAppointment(request, patientEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(StandardResponseDTO.<AppointmentResponseDTO>builder()
                        .success(true)
                        .message("Appointment booked successfully")
                        .data(response)
                        .build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all appointments", description = "Admin and Staff can view all appointments.")
    public ResponseEntity<StandardResponseDTO<List<AppointmentResponseDTO>>> getAllAppointments() {
        List<AppointmentResponseDTO> list = appointmentService.getAllAppointments();
        return ResponseEntity.ok(StandardResponseDTO.<List<AppointmentResponseDTO>>builder()
                .success(true)
                .message("Appointments retrieved successfully")
                .data(list)
                .build());
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN', 'STAFF')")
    @Operation(summary = "Get my appointments", description = "Returns appointments for the currently authenticated user.")
    public ResponseEntity<StandardResponseDTO<List<AppointmentResponseDTO>>> getMyAppointments(
            @RequestHeader("X-User-Email") String email,
            @RequestHeader("X-User-Role") String role) {

        List<AppointmentResponseDTO> list = appointmentService.getMyAppointments(email, role);
        return ResponseEntity.ok(StandardResponseDTO.<List<AppointmentResponseDTO>>builder()
                .success(true)
                .message("Appointments retrieved successfully")
                .data(list)
                .build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR', 'PATIENT')")
    @Operation(summary = "Get appointment by ID")
    public ResponseEntity<StandardResponseDTO<AppointmentResponseDTO>> getById(@PathVariable Long id) {
        AppointmentResponseDTO dto = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(StandardResponseDTO.<AppointmentResponseDTO>builder()
                .success(true)
                .message("Appointment retrieved successfully")
                .data(dto)
                .build());
    }

    @GetMapping("/patient/{patientAuthUserId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    @Operation(summary = "Get appointments by patient ID", description = "Admin, Staff and Doctors can view a patient's appointments.")
    public ResponseEntity<StandardResponseDTO<List<AppointmentResponseDTO>>> getByPatient(
            @PathVariable Long patientAuthUserId) {

        List<AppointmentResponseDTO> list = appointmentService.getAppointmentsByPatientAuthUserId(patientAuthUserId);
        return ResponseEntity.ok(StandardResponseDTO.<List<AppointmentResponseDTO>>builder()
                .success(true).message("Patient appointments retrieved").data(list).build());
    }

    @GetMapping("/doctor/{doctorAuthUserId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'DOCTOR')")
    @Operation(summary = "Get appointments by doctor ID")
    public ResponseEntity<StandardResponseDTO<List<AppointmentResponseDTO>>> getByDoctor(
            @PathVariable Long doctorAuthUserId) {

        List<AppointmentResponseDTO> list = appointmentService.getAppointmentsByDoctorAuthUserId(doctorAuthUserId);
        return ResponseEntity.ok(StandardResponseDTO.<List<AppointmentResponseDTO>>builder()
                .success(true).message("Doctor appointments retrieved").data(list).build());
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get appointments by status", description = "Filter appointments by PENDING, CONFIRMED, CANCELLED, or COMPLETED.")
    public ResponseEntity<StandardResponseDTO<List<AppointmentResponseDTO>>> getByStatus(
            @PathVariable AppointmentStatus status) {

        List<AppointmentResponseDTO> list = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(StandardResponseDTO.<List<AppointmentResponseDTO>>builder()
                .success(true).message("Appointments filtered by status").data(list).build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    @Operation(summary = "Update appointment status", description = "Doctors and Admins can confirm, cancel, or complete an appointment.")
    public ResponseEntity<StandardResponseDTO<AppointmentResponseDTO>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentStatusUpdateDTO dto,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        AppointmentResponseDTO updated = appointmentService.updateAppointmentStatus(id, dto, userEmail, userRole);
        return ResponseEntity.ok(StandardResponseDTO.<AppointmentResponseDTO>builder()
                .success(true).message("Appointment status updated").data(updated).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Cancel appointment", description = "Patients can cancel their own appointments. Admins can cancel any.")
    public ResponseEntity<StandardResponseDTO<Void>> cancelAppointment(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String userEmail,
            @RequestHeader("X-User-Role") String userRole) {

        appointmentService.cancelAppointment(id, userEmail, userRole);
        return ResponseEntity.ok(StandardResponseDTO.<Void>builder()
                .success(true).message("Appointment cancelled successfully").build());
    }
}
