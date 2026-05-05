package com.hospital.staff_service.controller;

import com.hospital.staff_service.dto.StaffRequestDTO;
import com.hospital.staff_service.dto.StaffResponseDTO;
import com.hospital.staff_service.dto.StandardResponseDTO;
import com.hospital.staff_service.service.StaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/staff")
@RequiredArgsConstructor
@Tag(name = "Staff", description = "Staff management endpoints")
public class StaffController {

    private final StaffService staffService;

    @Operation(summary = "Get all staff members")
    @GetMapping
    public ResponseEntity<StandardResponseDTO<List<StaffResponseDTO>>> getAllStaff() {
        List<StaffResponseDTO> staff = staffService.getAllStaff();
        return ResponseEntity.ok(StandardResponseDTO.<List<StaffResponseDTO>>builder()
                .success(true)
                .message("Staff retrieved successfully")
                .data(staff)
                .build());
    }

    @Operation(summary = "Get staff by Auth User ID")
    @GetMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<StaffResponseDTO>> getStaffByAuthUserId(
            @PathVariable Long authUserId) {
        StaffResponseDTO staff = staffService.getStaffByAuthUserID(authUserId);
        return ResponseEntity.ok(StandardResponseDTO.<StaffResponseDTO>builder()
                .success(true)
                .message("Staff retrieved successfully")
                .data(staff)
                .build());
    }

    @Operation(summary = "Update staff profile")
    @PutMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<StaffResponseDTO>> updateStaff(
            @PathVariable Long authUserId,
            @Valid @RequestBody StaffRequestDTO dto,
            @RequestHeader("X-User-Email") String callerEmail,
            @RequestHeader("X-User-Role")  String callerRole) {
        StaffResponseDTO updated = staffService.updateStaff(authUserId, dto, callerEmail, callerRole);
        return ResponseEntity.ok(StandardResponseDTO.<StaffResponseDTO>builder()
                .success(true)
                .message("Staff updated successfully")
                .data(updated)
                .build());
    }

    @Operation(summary = "Search staff with dynamic filters")
    @GetMapping("/search")
    public ResponseEntity<StandardResponseDTO<List<StaffResponseDTO>>> searchStaff(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean isActive) {
        List<StaffResponseDTO> results = staffService.searchStaffDynamic(name, phone, email, department, isActive);
        return ResponseEntity.ok(StandardResponseDTO.<List<StaffResponseDTO>>builder()
                .success(true)
                .message("Search completed successfully")
                .data(results)
                .build());
    }

    @Operation(summary = "Deactivate staff member (Soft Delete)")
    @DeleteMapping("/{authUserId}")
    public ResponseEntity<StandardResponseDTO<Void>> deactivateStaff(@PathVariable Long authUserId) {
        staffService.deactivateStaff(authUserId);
        return ResponseEntity.ok(StandardResponseDTO.<Void>builder()
                .success(true)
                .message("Staff member deactivated successfully")
                .build());
    }

    @Operation(summary = "Reactivate staff member")
    @PatchMapping("/{authUserId}/activate")
    public ResponseEntity<StandardResponseDTO<Void>> activateStaff(@PathVariable Long authUserId) {
        staffService.activateStaff(authUserId);
        return ResponseEntity.ok(StandardResponseDTO.<Void>builder()
                .success(true)
                .message("Staff member activated successfully")
                .build());
    }
}
