package com.gym.crm.entity;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Getter
@ToString(callSuper = true)
@SuperBuilder
public class Trainer extends User {

    private final Long userId;
    private final TrainingType specialization;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer trainer)) return false;
        return getUsername().equals(trainer.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

}
