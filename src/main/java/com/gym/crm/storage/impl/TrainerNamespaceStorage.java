package com.gym.crm.storage.impl;

import com.gym.crm.model.Trainer;
import com.gym.crm.storage.Namespace;
import org.springframework.stereotype.Component;

@Component
public class TrainerNamespaceStorage extends InMemoryNamespaceStorage<Trainer> {

    public TrainerNamespaceStorage() {
        super(Namespace.TRAINER);
    }

    @Override
    protected Trainer setId(Trainer entity, Long id) {
        return entity.toBuilder()
                .userId(id)
                .build();
    }

}
