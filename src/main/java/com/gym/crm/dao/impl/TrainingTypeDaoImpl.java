package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private final TransactionManager transactionManager;

    @Override
    public Optional<TrainingType> findByName(String name) {
        Objects.requireNonNull(name, "Training type name cannot be null");

        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT tt FROM TrainingType tt WHERE tt.trainingTypeName = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional());
    }

    @Override
    public List<TrainingType> findAll() {
        return transactionManager.executeReturningWithinTx(session -> session
                .createQuery("SELECT tt FROM TrainingType tt", TrainingType.class)
                .getResultList());
    }

}
