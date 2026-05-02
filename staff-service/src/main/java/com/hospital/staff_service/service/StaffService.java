package com.hospital.staff_service.service;

import com.hospital.staff_service.dto.StaffRequestDTO;
import com.hospital.staff_service.dto.StaffResponseDTO;

import java.util.List;

public interface StaffService {
    List<StaffResponseDTO> getAllStaff();
    StaffResponseDTO getStaffByAuthUserID(Long authUserId);
    StaffResponseDTO updateStaff(Long authUserId, StaffRequestDTO dto, String callerEmail, String callerRole);
    List<StaffResponseDTO> searchStaffDynamic(String name, String phone, String email, String department, Boolean isActive);
    void deactivateStaff(Long authUserId);
}
