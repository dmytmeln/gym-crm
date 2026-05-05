package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record TrainerUpdateDto(
        @NotBlank(message = "Trainer first name is required.")
        @Size(max = 100, message = "Trainer first name must not exceed 100 characters.")
        String firstName,
        @NotBlank(message = "Trainer last name is required.")
        @Size(max = 100, message = "Trainer last name must not exceed 100 characters.")
        String lastName,
        boolean active,
        @NotNull(message = "Specialization is required for trainer.")
        Long specializationId
) {
}
