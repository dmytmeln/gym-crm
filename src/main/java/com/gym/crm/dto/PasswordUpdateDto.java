package com.gym.crm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record PasswordUpdateDto(
        @NotBlank
        @Size(min = 10)
        String password
) {
}
