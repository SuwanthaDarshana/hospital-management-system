package com.hospital.doctor_service.controller;

import com.hospital.doctor_service.dto.DoctorRequestDTO;
import com.hospital.doctor_service.dto.DoctorResponseDTO;
import com.hospital.doctor_service.dto.StandardResponseDTO;
import com.hospital.doctor_service.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;


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

    @GetMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> getDoctorByAuthUserId(@PathVariable Long authUserId) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                        .success(true)
                        .message("Doctor fetched successfully")
                        .data(doctorService.getDoctorByAuthUserId(authUserId))
                        .build()
        );
    }

    @PutMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> updateDoctor(@PathVariable Long authUserId, @Validated @RequestBody DoctorRequestDTO doctorRequestDTO) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                .success(true)
                .message("Doctor updated successfully")
                .data(doctorService.updateDoctor(authUserId, doctorRequestDTO))
                .build()
        );
    }



    @GetMapping("/search")
    public ResponseEntity<StandardResponseDTO<List<DoctorResponseDTO>>> searchDoctors(@RequestParam String specialization){
        return ResponseEntity.ok(
                StandardResponseDTO.<List<DoctorResponseDTO>>builder()
                        .success(true)
                        .message("Doctors filtered by specialization")
                        .data(doctorService.searchBySpecialization(specialization))
                        .build()
        );
    }



}
