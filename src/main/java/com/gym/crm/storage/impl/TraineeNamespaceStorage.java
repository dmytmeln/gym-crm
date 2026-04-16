package com.gym.crm.storage.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.storage.Namespace;
import org.springframework.stereotype.Component;

@Component
public class TraineeNamespaceStorage extends InMemoryNamespaceStorage<Trainee> {

    public TraineeNamespaceStorage() {
        super(Namespace.TRAINEE);
    }

}
