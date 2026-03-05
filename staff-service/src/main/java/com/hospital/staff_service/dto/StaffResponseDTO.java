package com.hospital.staff_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "Response DTO containing staff information")
public class StaffResponseDTO {

    @Schema(description = "Unique identifier of the staff", example = "1")
    private Long id;

    @Schema(description = "Authentication user ID linked to this staff", example = "101")
    private Long authUserId;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    @Schema(description = "Staff department", example = "RECEPTION")
    private String department;

    private String address;
    private String gender;
    private LocalDate dateOfBirth;
    private String bloodGroup;
    private boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
