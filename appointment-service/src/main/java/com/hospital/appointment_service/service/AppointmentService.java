package com.hospital.appointment_service.service;

import com.hospital.appointment_service.dto.AppointmentRequestDTO;
import com.hospital.appointment_service.dto.AppointmentResponseDTO;
import com.hospital.appointment_service.dto.AppointmentStatusUpdateDTO;
import com.hospital.appointment_service.enums.AppointmentStatus;

import java.util.List;

public interface AppointmentService {

    AppointmentResponseDTO bookAppointment(AppointmentRequestDTO request, String patientEmail);

    AppointmentResponseDTO getAppointmentById(Long id);

    List<AppointmentResponseDTO> getAllAppointments();

    List<AppointmentResponseDTO> getAppointmentsByPatientAuthUserId(Long patientAuthUserId);

    List<AppointmentResponseDTO> getAppointmentsByDoctorAuthUserId(Long doctorAuthUserId);

    List<AppointmentResponseDTO> getMyAppointments(String email, String role);

    AppointmentResponseDTO updateAppointmentStatus(Long id, AppointmentStatusUpdateDTO dto, String userEmail, String userRole);

    void cancelAppointment(Long id, String patientEmail, String userRole);

    List<AppointmentResponseDTO> getAppointmentsByStatus(AppointmentStatus status);
}
