package com.hospital.staff_service.service.impl;

import com.hospital.staff_service.config.RabbitConfig;
import com.hospital.staff_service.dto.StaffCreatedEvent;
import com.hospital.staff_service.dto.StaffRequestDTO;
import com.hospital.staff_service.dto.StaffResponseDTO;
import com.hospital.staff_service.dto.StaffUpdatedEvent;
import com.hospital.staff_service.entity.Staff;
import com.hospital.staff_service.exception.ResourceNotFoundException;
import com.hospital.staff_service.repository.StaffRepository;
import com.hospital.staff_service.service.StaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffResponseDTO getStaffByAuthUserID(Long authUserId) {
        Staff staff = staffRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with Auth ID: " + authUserId));
        return mapToResponseDTO(staff);
    }

    @Override
    @Transactional
    public StaffResponseDTO updateStaff(Long authUserId, StaffRequestDTO dto) {
        // 1. Fetch existing record
        Staff staff = staffRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with Auth ID: " + authUserId));

        // 2. Track email change for sync
        boolean emailChanged = !staff.getEmail().equalsIgnoreCase(dto.getEmail());

        // 3. Update all fields
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setPhone(dto.getPhone());
        staff.setDepartment(dto.getDepartment());
        staff.setAddress(dto.getAddress());
        staff.setGender(dto.getGender());
        staff.setBloodGroup(dto.getBloodGroup());
        staff.setDateOfBirth(dto.getDateOfBirth());
        staff.setActive(dto.isActive());

        if (emailChanged) {
            staff.setEmail(dto.getEmail());
        }

        // Update role only if explicitly provided
        if (dto.getRole() != null && !dto.getRole().isBlank()) {
            staff.setRole(dto.getRole());
        }

        // 4. Save to DB
        Staff updatedStaff = staffRepository.save(staff);

        // 5. Sync with Auth Service if email or role changed
        boolean roleChanged = dto.getRole() != null && !dto.getRole().isBlank();
        if (emailChanged || roleChanged) {
            StaffUpdatedEvent event = StaffUpdatedEvent.builder()
                    .authUserId(updatedStaff.getAuthUserId())
                    .email(updatedStaff.getEmail())
                    .role(updatedStaff.getRole())
                    .isActive(updatedStaff.isActive())
                    .build();

            rabbitTemplate.convertAndSend(
                    RabbitConfig.STAFF_UPDATE_EXCHANGE,
                    RabbitConfig.STAFF_UPDATE_ROUTING_KEY,
                    event
            );
            log.info("Sync event sent to Auth Service for staff: {}", updatedStaff.getEmail());
        }

        return mapToResponseDTO(updatedStaff);
    }

    @Override
    public List<StaffResponseDTO> searchStaffDynamic(String name, String phone, String email, String department, Boolean isActive) {
        Specification<Staff> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();

            if (name != null && !name.isEmpty()) {
                String pattern = "%" + name.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)
                ));
            }
            if (phone != null && !phone.isEmpty())
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            if (email != null && !email.isEmpty())
                predicates.add(cb.equal(root.get("email"), email));
            if (department != null && !department.isEmpty())
                predicates.add(cb.equal(root.get("department"), department));
            if (isActive != null)
                predicates.add(cb.equal(root.get("isActive"), isActive));

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        return staffRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deactivateStaff(Long authUserId) {
        Staff staff = staffRepository.findByAuthUserId(authUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with Auth ID: " + authUserId));

        staff.setActive(false);
        staffRepository.save(staff);

        // Sync with Auth Service to block login
        StaffUpdatedEvent event = StaffUpdatedEvent.builder()
                .authUserId(staff.getAuthUserId())
                .isActive(false) // This is the 'Kill Switch'
                .build();

        rabbitTemplate.convertAndSend(
                RabbitConfig.STAFF_UPDATE_EXCHANGE,
                RabbitConfig.STAFF_UPDATE_ROUTING_KEY,
                event
        );
        log.info("Staff member {} deactivated and sync event sent.", staff.getEmail());
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
                .role(staff.getRole())
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
