package com.gym.crm.dto.filter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = true)
public class TraineeTrainingSearchFilter extends TrainingSearchFilter {
    private String trainerName;
    private String trainingTypeName;
}
