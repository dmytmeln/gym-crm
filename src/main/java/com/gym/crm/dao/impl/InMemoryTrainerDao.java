package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTrainerDao implements TrainerDao {

    private Storage storage;
    private final AtomicLong idCounter = new AtomicLong(1);

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Trainer create(Trainer entity) {
        Long newId = idCounter.getAndIncrement();
        Trainer trainerWithId = entity.toBuilder()
                .userId(newId)
                .build();
        storage.save(Namespace.TRAINER, newId, trainerWithId);
        return trainerWithId;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return storage.findById(Namespace.TRAINER, id);
    }

    @Override
    public List<Trainer> findAll() {
        return storage.findAll(Namespace.TRAINER);
    }

    @Override
    public Trainer update(Trainer entity) {
        Long id = entity.getUserId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }
        storage.save(Namespace.TRAINER, id, entity);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        return storage.delete(Namespace.TRAINER, id);
    }

}
