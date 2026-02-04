package com.hospital.patient_service.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientResponseDTO {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phoneNumber;
    String address;
    String gender;
    LocalDate dateOfBirth;
    String bloodGroup;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
