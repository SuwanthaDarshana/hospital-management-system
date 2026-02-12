package com.hospital.auth_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorCreatedEvent {
    private Long authUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialization;
    private String role;
}
