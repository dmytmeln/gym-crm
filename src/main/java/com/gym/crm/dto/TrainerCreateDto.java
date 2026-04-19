package com.gym.crm.dto;

import com.gym.crm.entity.TrainingType;
import lombok.Builder;

@Builder
public record TrainerCreateDto(
        String firstName,
        String lastName,
        boolean active,
        TrainingType specialization
) {
}
