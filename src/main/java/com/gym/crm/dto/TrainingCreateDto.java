package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TrainingCreateDto(
        @NotNull(message = "Trainee is required for training")
        Long traineeId,
        @NotNull(message = "Trainer is required for training")
        Long trainerId,
        @NotNull(message = "Training type is required for training")
        Long trainingTypeId,
        @NotBlank(message = "Training name is required")
        @Size(max = 100, message = "Training name must not exceed 100 characters")
        String trainingName,
        @Positive(message = "Training duration must be a positive number")
        int trainingDuration,
        @NotNull(message = "Training date is required")
        LocalDate trainingDate
) {
}
