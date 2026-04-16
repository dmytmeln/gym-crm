package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainingDao implements TrainingDao {

    private Storage storage;
    private final AtomicLong idCounter = new AtomicLong(1);

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Training create(Training entity) {
        Long newId = idCounter.getAndIncrement();
        Training trainingWithId = entity.toBuilder()
                .id(newId)
                .build();
        storage.save(Namespace.TRAINING, newId, trainingWithId);
        return trainingWithId;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return storage.findById(Namespace.TRAINING, id);
    }

    @Override
    public List<Training> findAll() {
        return storage.findAll(Namespace.TRAINING);
    }

    @Override
    public Training update(Training entity) {
        Long id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }
        storage.save(Namespace.TRAINING, id, entity);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        return storage.delete(Namespace.TRAINING, id);
    }

}
