package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.storage.Namespace;
import com.gym.crm.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryTraineeDao implements TraineeDao {

    private final AtomicLong idCounter = new AtomicLong(1);

    private Storage storage;

    @Autowired
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public Trainee create(Trainee entity) {
        Long newId = idCounter.getAndIncrement();
        Trainee traineeWithId = entity.toBuilder()
                .userId(newId)
                .build();
        storage.save(Namespace.TRAINEE, newId, traineeWithId);
        return traineeWithId;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return storage.findById(Namespace.TRAINEE, id);
    }

    @Override
    public List<Trainee> findAll() {
        return storage.findAll(Namespace.TRAINEE);
    }

    @Override
    public Trainee update(Trainee entity) {
        Long id = entity.getUserId();
        if (id == null) {
            throw new IllegalArgumentException("Cannot update entity without ID");
        }
        storage.save(Namespace.TRAINEE, id, entity);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        return storage.delete(Namespace.TRAINEE, id);
    }

}
