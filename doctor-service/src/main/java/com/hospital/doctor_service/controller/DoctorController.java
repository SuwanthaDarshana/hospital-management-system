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

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                        .success(true)
                        .message("Doctor fetched successfully")
                        .data(doctorService.getDoctorById(id))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponseDTO<DoctorResponseDTO>> updateDoctor(@PathVariable Long id, @Validated @RequestBody DoctorRequestDTO doctorRequestDTO) {
        return ResponseEntity.ok(
                StandardResponseDTO.<DoctorResponseDTO>builder()
                .success(true)
                .message("Doctor updated successfully")
                .data(doctorService.updateDoctor(id, doctorRequestDTO))
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
