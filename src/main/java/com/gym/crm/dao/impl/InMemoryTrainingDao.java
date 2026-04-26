package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryTrainingDao implements TrainingDao {

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Training create(Training entity) {
        Objects.requireNonNull(entity, "Training cannot be null");

        return storage.save(Namespace.TRAINING, entity);
    }

    @Override
    public Optional<Training> findById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.findById(Namespace.TRAINING, id);
    }

    @Override
    public List<Training> findAll() {
        return storage.findAll(Namespace.TRAINING);
    }

    @Override
    public Training update(Training entity) {
        Objects.requireNonNull(entity, "Training cannot be null");

        Long id = entity.getId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }

        storage.update(Namespace.TRAINING, id, entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.delete(Namespace.TRAINING, id);
    }

}
