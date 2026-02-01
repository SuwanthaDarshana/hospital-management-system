package com.hospital.auth_service.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StandardResponseDTO<T>{
    private boolean success;
    private String message;
    private T data;
}
