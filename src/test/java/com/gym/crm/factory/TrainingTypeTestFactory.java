package com.gym.crm.factory;

import com.gym.crm.entity.TrainingType;

public class TrainingTypeTestFactory {

    public static final String STRENGTH = "Strength";

    public static TrainingType strength() {
        return new TrainingType(STRENGTH);
    }

    public static TrainingType custom(String typeName) {
        return new TrainingType(typeName);
    }

}
