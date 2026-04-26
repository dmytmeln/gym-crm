package com.gym.crm.dto;

import com.gym.crm.model.TrainingType;
import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TrainingResponseDto(
        Long id,
        Long traineeId,
        Long trainerId,
        String trainingName,
        TrainingType trainingType,
        int trainingDuration,
        LocalDate trainingDate
) {
}
