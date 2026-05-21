package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.helper.TraineeTrainingCriteriaBuilder;
import com.gym.crm.dao.helper.TrainerTrainingCriteriaBuilder;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;
import com.gym.crm.transaction.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final TransactionManager transactionManager;
    private final TrainerTrainingCriteriaBuilder trainerTrainingCriteriaBuilder;
    private final TraineeTrainingCriteriaBuilder traineeTrainingCriteriaBuilder;

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
    public List<Training> findTrainerTrainingsByCriteria(TrainerTrainingSearchFilter filter) {
        return transactionManager.executeReturningWithinTx(session -> trainerTrainingCriteriaBuilder.findTrainings(session, filter));
    }

    @Override
    public List<Training> findTraineeTrainingsByCriteria(TraineeTrainingSearchFilter filter) {
        return transactionManager.executeReturningWithinTx(session -> traineeTrainingCriteriaBuilder.findTrainings(session, filter));
    }

}
