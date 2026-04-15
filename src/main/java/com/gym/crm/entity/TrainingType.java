package com.gym.crm.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@AllArgsConstructor
@ToString
public class TrainingType {

    private final String trainingTypeName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainingType that)) return false;
        return trainingTypeName.equals(that.trainingTypeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trainingTypeName);
    }

}
