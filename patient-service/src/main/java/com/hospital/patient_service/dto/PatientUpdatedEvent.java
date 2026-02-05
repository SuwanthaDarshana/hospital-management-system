package com.hospital.patient_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientUpdatedEvent {

    private Long authUserId; //find the user id from auth service
    private String email;
    private String password;
}
