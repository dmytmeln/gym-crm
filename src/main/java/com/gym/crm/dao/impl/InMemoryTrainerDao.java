package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryTrainerDao implements TrainerDao {

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Trainer create(Trainer entity) {
        Objects.requireNonNull(entity, "Trainer cannot be null");

        return storage.save(Namespace.TRAINER, entity);
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.findById(Namespace.TRAINER, id);
    }

    @Override
    public List<Trainer> findAll() {
        return storage.findAll(Namespace.TRAINER);
    }

    @Override
    public Trainer update(Trainer entity) {
        Objects.requireNonNull(entity, "Trainer cannot be null");

        Long id = entity.getUserId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }

        storage.update(Namespace.TRAINER, id, entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.delete(Namespace.TRAINER, id);
    }

}
