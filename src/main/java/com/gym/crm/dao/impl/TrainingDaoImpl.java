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
import java.util.Optional;

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

    @Override
    public List<Training> findTrainerTrainingsByCriteria(TrainerTrainingSearchFilter filter) {
        return transactionManager.executeReturningWithinTx(session -> trainerTrainingCriteriaBuilder.findTrainings(session, filter));
    }

    @Override
    public List<Training> findTraineeTrainingsByCriteria(TraineeTrainingSearchFilter filter) {
        return transactionManager.executeReturningWithinTx(session -> traineeTrainingCriteriaBuilder.findTrainings(session, filter));
    }

}
