package com.gym.crm.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record TraineeCreateDto(
        String username,
        String firstName,
        String lastName,
        String password,
        boolean active,
        String address,
        LocalDate dateOfBirth
) {
}
