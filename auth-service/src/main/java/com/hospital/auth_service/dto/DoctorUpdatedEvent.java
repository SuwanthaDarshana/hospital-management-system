package com.hospital.auth_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorUpdatedEvent {
    private Long authUserId;
    private String email;
    private String password;
    private String role;
}
