package com.gym.crm.dto;

import com.gym.crm.model.TrainingType;
import lombok.Builder;

@Builder
public record TrainerUpdateDto(
        String firstName,
        String lastName,
        String password,
        boolean active,
        TrainingType specialization
) {
}
