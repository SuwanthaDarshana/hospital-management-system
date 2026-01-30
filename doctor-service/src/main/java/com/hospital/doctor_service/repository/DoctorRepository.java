package com.hospital.doctor_service.repository;

import com.hospital.doctor_service.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findBySpecializationContainingIgnoreCase(String specialization);
}
