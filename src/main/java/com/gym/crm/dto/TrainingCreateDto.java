package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingCreateDto(
        @NotNull
        Long traineeId,
        @NotNull
        Long trainerId,
        @NotNull
        Long trainingTypeId,
        @NotBlank
        @Size(max = 100)
        String trainingName,
        @Positive
        int trainingDuration,
        @NotNull
        LocalDate trainingDate
) {
}
