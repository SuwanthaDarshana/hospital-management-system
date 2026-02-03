package com.hospital.auth_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false, unique = true)
    private String email;

//    @Column(nullable = false)
//    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role;

    private boolean enabled = true;
}
