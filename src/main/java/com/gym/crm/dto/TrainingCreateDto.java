package com.gym.crm.dto;

import com.gym.crm.model.TrainingType;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingCreateDto(
        Long traineeId,
        Long trainerId,
        String trainingName,
        TrainingType trainingType,
        int trainingDuration,
        LocalDate trainingDate
) {
}
