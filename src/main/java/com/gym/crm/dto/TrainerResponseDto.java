package com.gym.crm.dto;

import com.gym.crm.model.TrainingType;
import lombok.Builder;

@Builder(toBuilder = true)
public record TrainerResponseDto(
        Long userId,
        String username,
        String firstName,
        String lastName,
        boolean active,
        TrainingType specialization
) {
}
