package com.gym.crm.factory;

import com.gym.crm.entity.TrainingType;

public class TrainingTypeTestFactory {

    public static final String STRENGTH = "Strength";

    public static TrainingType buildStrength() {
        return new TrainingType(STRENGTH);
    }

    public static TrainingType buildCustom(String typeName) {
        return new TrainingType(typeName);
    }

}
