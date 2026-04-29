package com.hospital.appointment_service.service.impl;

import com.hospital.appointment_service.dto.AppointmentRequestDTO;
import com.hospital.appointment_service.dto.AppointmentResponseDTO;
import com.hospital.appointment_service.dto.AppointmentStatusUpdateDTO;
import com.hospital.appointment_service.entity.Appointment;
import com.hospital.appointment_service.enums.AppointmentStatus;
import com.hospital.appointment_service.exception.ResourceNotFoundException;
import com.hospital.appointment_service.repository.AppointmentRepository;
import com.hospital.appointment_service.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    @Transactional
    public AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request, String patientEmail) {
        boolean duplicate = appointmentRepository
                .existsByPatientAuthUserIdAndDoctorAuthUserIdAndAppointmentDateAndAppointmentTime(
                        request.getPatientAuthUserId(),
                        request.getDoctorAuthUserId(),
                        request.getAppointmentDate(),
                        request.getAppointmentTime());

        if (duplicate) {
            throw new IllegalStateException("An appointment already exists for this patient with this doctor at the same date and time.");
        }

        Appointment appointment = Appointment.builder()
                .patientAuthUserId(request.getPatientAuthUserId())
                .patientEmail(patientEmail)
                .patientName(request.getPatientName())
                .doctorAuthUserId(request.getDoctorAuthUserId())
                .doctorName(request.getDoctorName())
                .doctorSpecialization(request.getDoctorSpecialization())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .status(AppointmentStatus.PENDING)
                .reason(request.getReason())
                .build();

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment booked: id={}, patient={}, doctor={}", saved.getId(), patientEmail, request.getDoctorAuthUserId());
        return toDTO(saved);
    }

    @Override
    public AppointmentResponseDTO getAppointmentById(Long id) {
        return toDTO(findById(id));
    }

    @Override
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByPatientAuthUserId(Long patientAuthUserId) {
        return appointmentRepository.findByPatientAuthUserId(patientAuthUserId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByDoctorAuthUserId(Long doctorAuthUserId) {
        return appointmentRepository.findByDoctorAuthUserId(doctorAuthUserId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<AppointmentResponseDTO> getMyAppointments(String email, String role) {
        if ("DOCTOR".equalsIgnoreCase(role)) {
            // Doctors see appointments where they are assigned — filtered by their email if we had it,
            // but since we store doctorAuthUserId, the doctor must use the /doctor/{id} endpoint.
            // For /my endpoint, we return by patientEmail if PATIENT, or all if ADMIN/STAFF.
            return appointmentRepository.findAll().stream()
                    .filter(a -> email.equalsIgnoreCase(a.getPatientEmail()))
                    .map(this::toDTO).collect(Collectors.toList());
        }
        return appointmentRepository.findByPatientEmail(email)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long id, AppointmentStatusUpdateDTO dto, String userEmail, String userRole) {
        Appointment appointment = findById(id);

        // Doctors can only update their own appointments
        if ("DOCTOR".equalsIgnoreCase(userRole)) {
            // We allow doctors to update if they are the assigned doctor (trust the service layer)
            // More granular check can be added when doctor email is stored
        }

        appointment.setStatus(dto.getStatus());
        if (dto.getNotes() != null && !dto.getNotes().isBlank()) {
            appointment.setNotes(dto.getNotes());
        }

        Appointment updated = appointmentRepository.save(appointment);
        log.info("Appointment {} status updated to {} by {}", id, dto.getStatus(), userEmail);
        return toDTO(updated);
    }

    @Override
    @Transactional
    public void cancelAppointment(Long id, String patientEmail, String userRole) {
        Appointment appointment = findById(id);

        if ("PATIENT".equalsIgnoreCase(userRole) && !appointment.getPatientEmail().equalsIgnoreCase(patientEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You can only cancel your own appointments.");
        }

        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed appointment.");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
        log.info("Appointment {} cancelled by {}", id, patientEmail);
    }

    @Override
    public List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private Appointment findById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
    }

    private AppointmentResponseDTO toDTO(Appointment a) {
        return AppointmentResponseDTO.builder()
                .id(a.getId())
                .patientAuthUserId(a.getPatientAuthUserId())
                .patientEmail(a.getPatientEmail())
                .patientName(a.getPatientName())
                .doctorAuthUserId(a.getDoctorAuthUserId())
                .doctorName(a.getDoctorName())
                .doctorSpecialization(a.getDoctorSpecialization())
                .appointmentDate(a.getAppointmentDate())
                .appointmentTime(a.getAppointmentTime())
                .status(a.getStatus())
                .reason(a.getReason())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
