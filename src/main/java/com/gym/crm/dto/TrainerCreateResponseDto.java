package com.gym.crm.dto;

import com.gym.crm.model.TrainingType;
import lombok.Builder;

@Builder
public record TrainerCreateResponseDto(
        Long userId,
        String username,
        String firstName,
        String lastName,
        String password,
        boolean active,
        TrainingType specialization
) {
}
