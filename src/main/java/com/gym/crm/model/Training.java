package com.gym.crm.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
@Builder(toBuilder = true)
public class Training {

    private final Long id;
    private final Long traineeId;
    private final Long trainerId;
    private final String trainingName;
    private final TrainingType trainingType;
    private final int trainingDuration;
    private final LocalDate trainingDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Training training)) return false;
        return id.equals(training.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
