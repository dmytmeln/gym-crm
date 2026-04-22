package com.gym.crm.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder(toBuilder = true)
public record TraineeResponseDto(
        Long userId,
        String username,
        String firstName,
        String lastName,
        boolean active,
        String address,
        LocalDate dateOfBirth
) {
}
