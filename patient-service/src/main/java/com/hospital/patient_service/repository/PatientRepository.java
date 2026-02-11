package com.hospital.patient_service.repository;

import com.hospital.patient_service.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient,Long>, JpaSpecificationExecutor<Patient> {
    // Change findByPhoneNumber to findByPhone
    Optional<Patient> findByPhone(String phone);

    // Change existsByPhoneNumber to existsByPhone
    boolean existsByPhone(String phone);

    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email);

    Optional<Patient> findByAuthUserId(Long authUserId);
}
