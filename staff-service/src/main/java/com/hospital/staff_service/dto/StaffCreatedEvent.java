package com.hospital.staff_service.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffCreatedEvent {
    private Long authUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private String role;
    private String department;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private boolean isActive;
}
