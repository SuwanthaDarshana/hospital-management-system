package com.hospital.doctor_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorCreatedEvent {
    private String email;
    private String username;
}
