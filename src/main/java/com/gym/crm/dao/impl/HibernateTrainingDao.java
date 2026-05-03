package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingEntityDao;
import com.gym.crm.dao.helper.TransactionManager;
import com.gym.crm.entity.Training;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HibernateTrainingDao implements TrainingEntityDao {

    private final TransactionManager transactionManager;

    @Override
    public Training save(Training entity) {
        Objects.requireNonNull(entity, "Training cannot be null");
        if (entity.getId() != null) {
            throw new IllegalArgumentException("Training ID must be null for creation.");
        }

        transactionManager.executeWithinTx(session -> session.persist(entity));

        return entity;
    }

    @Override
    public Optional<Training> findById(Long id) {
        Objects.requireNonNull(id, "Training ID cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery(
                        "SELECT tr FROM Training tr JOIN FETCH tr.trainee JOIN FETCH tr.trainer JOIN FETCH tr.trainingType WHERE tr.id = :id",
                        Training.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    @Override
    public List<Training> findAll() {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT tr FROM Training tr JOIN FETCH tr.trainee JOIN FETCH tr.trainer JOIN FETCH tr.trainingType", Training.class)
                .list());
    }

}
