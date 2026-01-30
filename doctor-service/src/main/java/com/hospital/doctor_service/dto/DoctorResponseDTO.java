package com.hospital.doctor_service.dto;


import lombok.*;

@Data
@Builder
public class DoctorResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private String availability;
}
