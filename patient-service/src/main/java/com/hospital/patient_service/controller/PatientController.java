package com.hospital.patient_service.controller;

import com.hospital.patient_service.dto.PatientRequestDTO;
import com.hospital.patient_service.dto.PatientResponseDTO;
import com.hospital.patient_service.dto.StandardResponseDTO;
import com.hospital.patient_service.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<PatientResponseDTO>> getPatientByAuthUserId(@PathVariable Long authUserId){
        return ResponseEntity.ok(StandardResponseDTO.<PatientResponseDTO>builder()
                .success(true)
                .message("Patient fetched successfully")
                .data(patientService.getPatientByAuthUserId(authUserId))
                .build());
    }

    @GetMapping("/dynamic-search")
    public ResponseEntity<StandardResponseDTO<List<PatientResponseDTO>>> filterPatients(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String bloodGroup,
            @RequestParam(required = false) Boolean isActive) {

        return ResponseEntity.ok(StandardResponseDTO.<List<PatientResponseDTO>>builder()
                .success(true)
                .message("Filtered patients fetched successfully")
                .data(patientService.searchPatientsDynamic(name, phone, email, bloodGroup, isActive))
                .build());
    }



    @GetMapping
    public ResponseEntity<StandardResponseDTO<List<PatientResponseDTO>>> getAllPatients(){
        return ResponseEntity.ok(StandardResponseDTO.<List<PatientResponseDTO>>builder()
                .success(true)
                .message("All patients fetched successfully")
                .data(patientService.getAllPatients())
                .build());
    }

    @PutMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<PatientResponseDTO>> updatePatient(
            @PathVariable Long authUserId, @Valid @RequestBody PatientRequestDTO patientRequestDTO) {
        return ResponseEntity.ok(StandardResponseDTO.<PatientResponseDTO>builder()
                .success(true)
                .message("Patient updated successfully")
                .data(patientService.updatePatient(authUserId, patientRequestDTO))
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponseDTO<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(StandardResponseDTO.<Void>builder()
                .success(true)
                .message("Patient deactivated successfully") // Soft delete message
                .build());
    }

}

//TODO: Add delete patient by authUserId