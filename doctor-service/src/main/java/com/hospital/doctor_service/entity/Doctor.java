package com.hospital.doctor_service.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.PostLoad;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String specialization;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String availability;

    @Builder.Default
    private String availabilityStatus = "NOT_SET";

    @PostLoad
    private void fillDefaults() {
        if (availabilityStatus == null) availabilityStatus = "NOT_SET";
    }

    private String role;

    @Column(nullable = false, unique = true)
    private Long authUserId;   // <-- Link to Auth Service User


    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();





}
