package com.hospital.auth_service.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StaffCreatedEvent {

    private Long authUserId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String department;
    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private String role;

}
