package com.gym.crm.dto;

import com.gym.crm.security.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(
        @NotBlank(message = "Username is required")
        @Size(max = 220, message = "Username must not exceed 220 characters")
        String username,
        @NotBlank(message = "Password is required")
        @Size(min = 10, message = "Password must be at least 10 characters long")
        String password,
        @NotNull(message = "Role is required")
        Role role
) {
}
