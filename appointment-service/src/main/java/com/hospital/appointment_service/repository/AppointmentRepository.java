package com.hospital.appointment_service.repository;

import com.hospital.appointment_service.entity.Appointment;
import com.hospital.appointment_service.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientAuthUserId(Long patientAuthUserId);

    List<Appointment> findByPatientEmail(String email);

    List<Appointment> findByDoctorAuthUserId(Long doctorAuthUserId);

    List<Appointment> findByStatus(AppointmentStatus status);

    List<Appointment> findByDoctorAuthUserIdAndAppointmentDate(Long doctorAuthUserId, LocalDate date);

    List<Appointment> findByPatientAuthUserIdAndStatus(Long patientAuthUserId, AppointmentStatus status);

    List<Appointment> findByDoctorAuthUserIdAndStatus(Long doctorAuthUserId, AppointmentStatus status);

    boolean existsByPatientAuthUserIdAndDoctorAuthUserIdAndAppointmentDateAndAppointmentTime(
            Long patientAuthUserId, Long doctorAuthUserId, LocalDate date, String time);
}
