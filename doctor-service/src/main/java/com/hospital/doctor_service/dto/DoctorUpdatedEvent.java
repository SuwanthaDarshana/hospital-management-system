package com.hospital.doctor_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorUpdatedEvent {
    private Long authUserId;
    private String email ;
    private String firstName;
    private String lastName;
    private String phone;
    private String specialization;
    private String availability;
    private String password;
    private String role;
}
