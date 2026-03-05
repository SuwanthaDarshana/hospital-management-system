package com.hospital.staff_service.service.impl;

import com.hospital.staff_service.dto.StaffRequestDTO;
import com.hospital.staff_service.dto.StaffResponseDTO;
import com.hospital.staff_service.entity.Staff;
import com.hospital.staff_service.repository.StaffRepository;
import com.hospital.staff_service.service.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

   private final StaffRepository staffRepository;

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponseDTO getStaffByAuthUserID(Long authUserId) {
        return null;
    }

    @Override
    public StaffResponseDTO updateStaff(Long authUserId, StaffRequestDTO staffRequestDTO) {
        return null;
    }

    @Override
    public List<StaffResponseDTO> searchStaffDynamic(String name, String phone, String email, String bloodGroup, Boolean isActive) {
        return List.of();
    }


    // ========================= MAPPER =========================

    private StaffResponseDTO mapToResponseDTO(Staff staff) {
        return StaffResponseDTO.builder()
                .id(staff.getId())
                .authUserId(staff.getAuthUserId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .phone(staff.getPhone())
                .department(staff.getDepartment())
                .address(staff.getAddress())
                .gender(staff.getGender())
                .dateOfBirth(staff.getDateOfBirth())
                .bloodGroup(staff.getBloodGroup())
                .isActive(staff.isActive())
                .createdAt(staff.getCreatedAt())
                .updatedAt(staff.getUpdatedAt())
                .build();
    }

}
