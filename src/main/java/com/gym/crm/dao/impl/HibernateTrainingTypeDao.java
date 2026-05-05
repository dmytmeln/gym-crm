package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dao.helper.TransactionManager;
import com.gym.crm.entity.TrainingType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class HibernateTrainingTypeDao implements TrainingTypeDao {

    private final TransactionManager transactionManager;

    @Override
    public Optional<TrainingType> findById(Long id) {
        Objects.requireNonNull(id, "Training type ID cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT tt FROM TrainingType tt WHERE tt.id = :id", TrainingType.class)
                .setParameter("id", id)
                .uniqueResultOptional());
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        Objects.requireNonNull(name, "Training type name cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT tt FROM TrainingType tt WHERE tt.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional());
    }

}
