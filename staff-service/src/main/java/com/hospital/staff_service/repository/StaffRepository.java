package com.hospital.staff_service.repository;


import com.hospital.staff_service.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> , JpaSpecificationExecutor<Staff> {

    Optional<Staff> findByAuthUserId(Long authUserId);


    boolean existsByAuthUserId(Long authUserId);
    boolean existsByEmail(String email);

    Optional<Staff> findByPhone(String phone);
    Optional<Staff> findByEmail(String email);

    // Quick search for names
    List<Staff> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);


}
