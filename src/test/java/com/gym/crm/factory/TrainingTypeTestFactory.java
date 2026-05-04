package com.gym.crm.factory;

import com.gym.crm.entity.TrainingType;

public class TrainingTypeTestFactory {

    public static final String STRENGTH = "STRENGTH";
    public static final String CARDIO = "CARDIO";
    public static final String YOGA = "YOGA";

    public static TrainingType buildStrength() {
        return TrainingType.builder().trainingTypeName(STRENGTH).build();
    }

    public static TrainingType buildCardio() {
        return TrainingType.builder().trainingTypeName(CARDIO).build();
    }

    public static TrainingType buildCustom(String typeName) {
        return TrainingType.builder().trainingTypeName(typeName).build();
    }

}
