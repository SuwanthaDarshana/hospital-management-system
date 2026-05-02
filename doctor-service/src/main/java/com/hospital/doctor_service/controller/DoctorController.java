package com.hospital.doctor_service.controller;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;
import com.hospital.doctor_service.dto.StandardResponseDTO;
import com.hospital.doctor_service.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctor Management", description = "APIs for managing doctors in the hospital system")
public class DoctorController {

    private final DoctorService doctorService;


    @Operation(summary = "Get all doctors", description = "Retrieves a list of all doctors in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of doctors",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @GetMapping
    public ResponseEntity<StandardResponseDTO<List<DoctorResponseDTO>>> getAllDoctors() {
        return ResponseEntity.ok(
                StandardResponseDTO.<List<DoctorResponseDTO>>builder()
                        .success(true)
                        .message("Doctors fetched successfully")
                        .data(doctorService.getAllDoctors())
                        .build()
        );

    }

    @Operation(summary = "Get doctor by Auth User ID", description = "Retrieves a specific doctor by their authentication user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the doctor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication"),
            @ApiResponse(responseCode = "404", description = "Doctor not found with the given auth user ID")
    })
    @GetMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> getDoctorByAuthUserId(
            @Parameter(description = "Authentication User ID of the doctor", required = true, example = "1")
            @PathVariable Long authUserId) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                        .success(true)
                        .message("Doctor fetched successfully")
                        .data(doctorService.getDoctorByAuthUserId(authUserId))
                        .build()
        );
    }

    @Operation(summary = "Update doctor", description = "Updates doctor information. Admin can update all fields, doctors can update specific fields.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the doctor",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing authentication"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Doctor not found with the given auth user ID")
    })
    @PutMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> updateDoctor(
            @Parameter(description = "Authentication User ID of the doctor to update", required = true, example = "1")
            @PathVariable Long authUserId,
            @Validated @RequestBody DoctorRequestDTO doctorRequestDTO) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                .success(true)
                .message("Doctor updated successfully")
                .data(doctorService.updateDoctor(authUserId, doctorRequestDTO))
                .build()
        );
    }



    @Operation(summary = "Update availability status", description = "Doctors set their own; admins can set any doctor.")
    @PatchMapping("/{authUserId}/availability")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> updateAvailability(
            @PathVariable Long authUserId,
            @RequestParam String status,
            @RequestHeader("X-User-Email") String callerEmail,
            @RequestHeader("X-User-Role")  String callerRole) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                        .success(true)
                        .message("Availability status updated")
                        .data(doctorService.updateAvailabilityStatus(authUserId, status, callerEmail, callerRole))
                        .build()
        );
    }

    @Operation(summary = "Search doctors by specialization", description = "Search for doctors by their specialization")
    @GetMapping("/search")
    public ResponseEntity<StandardResponseDTO<List<DoctorResponseDTO>>> searchDoctors(
            @Parameter(description = "Specialization to search for", required = true, example = "Cardiology")
            @RequestParam String specialization){
        return ResponseEntity.ok(
                StandardResponseDTO.<List<DoctorResponseDTO>>builder()
                        .success(true)
                        .message("Doctors filtered by specialization")
                        .data(doctorService.searchBySpecialization(specialization))
                        .build()
        );
    }



}
