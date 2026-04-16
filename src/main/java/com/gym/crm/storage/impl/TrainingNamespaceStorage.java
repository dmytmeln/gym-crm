package com.gym.crm.storage.impl;

import com.gym.crm.entity.Training;
import com.gym.crm.storage.Namespace;
import org.springframework.stereotype.Component;

@Component
public class TrainingNamespaceStorage extends InMemoryNamespaceStorage<Training> {

    public TrainingNamespaceStorage() {
        super(Namespace.TRAINING);
    }

}
