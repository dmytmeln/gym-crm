package com.gym.crm.dto.filter;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
public class TrainingSearchFilter {
    @NotBlank
    private String username;
    private LocalDate fromDate;
    private LocalDate toDate;
}
