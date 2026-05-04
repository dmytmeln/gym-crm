package com.gym.crm.dto;

import lombok.Builder;

@Builder
public record TrainerCreateDto(
        String firstName,
        String lastName,
        boolean active,
        Long specializationId
) {
}
