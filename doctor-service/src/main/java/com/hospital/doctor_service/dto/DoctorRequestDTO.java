package com.hospital.doctor_service.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;

@Data
public class DoctorRequestDTO {


    private String phone;

    private String specialization;

    private String availability; // JSON string


}
