package com.gym.crm.storage.impl;

import com.gym.crm.entity.Trainer;
import com.gym.crm.storage.Namespace;
import org.springframework.stereotype.Component;

@Component
public class TrainerNamespaceStorage extends InMemoryNamespaceStorage<Trainer> {

    public TrainerNamespaceStorage() {
        super(Namespace.TRAINER);
    }

}
