package com.hospital.auth_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffUpdatedEvent {
    private Long authUserId; //find the user id from auth service
    private String email;
    private String password;
    private String role;
    private boolean isActive;
}
