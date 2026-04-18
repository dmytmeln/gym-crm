package com.gym.crm.dto;

import com.gym.crm.entity.TrainingType;
import lombok.Builder;

@Builder
public record TrainerResponseDto(
        Long userId,
        String username,
        String firstName,
        String lastName,
        boolean active,
        TrainingType specialization
) {
}
