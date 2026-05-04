package com.gym.crm.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingCreateDto(
        Long traineeId,
        Long trainerId,
        Long trainingTypeId,
        String trainingName,
        int trainingDuration,
        LocalDate trainingDate
) {
}
