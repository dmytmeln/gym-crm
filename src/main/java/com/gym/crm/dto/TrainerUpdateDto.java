package com.gym.crm.dto;

import com.gym.crm.entity.TrainingType;
import lombok.Builder;

@Builder
public record TrainerUpdateDto(
        String username,
        String firstName,
        String lastName,
        String password,
        boolean active,
        TrainingType specialization
) {
}
