package com.gym.crm.storage;

import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
import lombok.Getter;

@Getter
public class Namespace<T> {

    public static final Namespace<Trainee> TRAINEE = new Namespace<>(Trainee.class);
    public static final Namespace<Trainer> TRAINER = new Namespace<>(Trainer.class);
    public static final Namespace<Training> TRAINING = new Namespace<>(Training.class);

    private final Class<T> entityClass;

    private Namespace(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

}
