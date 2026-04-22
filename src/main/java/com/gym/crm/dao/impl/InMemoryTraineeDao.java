package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class InMemoryTraineeDao implements TraineeDao {

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Trainee create(Trainee entity) {
        Objects.requireNonNull(entity, "Trainee cannot be null");

        return storage.save(Namespace.TRAINEE, entity);
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.findById(Namespace.TRAINEE, id);
    }

    @Override
    public List<Trainee> findAll() {
        return storage.findAll(Namespace.TRAINEE);
    }

    @Override
    public Trainee update(Trainee entity) {
        Objects.requireNonNull(entity, "Trainee cannot be null");

        Long id = entity.getUserId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }

        storage.update(Namespace.TRAINEE, id, entity);

        return entity;
    }

    @Override
    public boolean delete(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");

        return storage.delete(Namespace.TRAINEE, id);
    }

}
