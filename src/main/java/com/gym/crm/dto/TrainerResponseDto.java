package com.gym.crm.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record TrainerResponseDto(
        Long id,
        Long userId,
        String username,
        String firstName,
        String lastName,
        boolean active,
        String specializationName
) {
}
