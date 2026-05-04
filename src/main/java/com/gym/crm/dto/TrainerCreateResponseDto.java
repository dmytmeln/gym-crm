package com.gym.crm.dto;

import lombok.Builder;

@Builder
public record TrainerCreateResponseDto(
        Long id,
        Long userId,
        String username,
        String firstName,
        String lastName,
        String password,
        boolean active,
        String specializationName
) {
}
