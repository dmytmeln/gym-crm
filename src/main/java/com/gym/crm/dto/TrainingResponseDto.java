package com.gym.crm.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TrainingResponseDto(
        Long id,
        Long traineeId,
        Long trainerId,
        String trainingTypeName,
        String trainingName,
        int trainingDuration,
        LocalDate trainingDate
) {
}
