package com.hospital.staff_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffUpdatedEvent {
    private Long authUserId;
    private String email;
    private String password;
    private String role;
    private boolean isActive;  //check active or deactive
}
