package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeUpdateDto(
        @NotBlank(message = "Trainee first name is required.")
        @Size(max = 100, message = "Trainee first name must not exceed 100 characters.")
        String firstName,
        @NotBlank(message = "Trainee last name is required.")
        @Size(max = 100, message = "Trainee last name must not exceed 100 characters.")
        String lastName,
        boolean active,
        @Size(max = 100, message = "Address must not exceed 100 characters.")
        String address,
        LocalDate dateOfBirth
) {
}
