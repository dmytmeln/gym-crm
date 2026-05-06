package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainingService;
import com.gym.crm.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;
    private TrainingTypeDao trainingTypeDao;

    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTrainingTypeDao(TrainingTypeDao trainingTypeDao) {
        this.trainingTypeDao = trainingTypeDao;
    }

    @Override
    @Transaction
    public Training createTraining(Training training) {
        Objects.requireNonNull(training, "Training cannot be null");
        Objects.requireNonNull(training.getTrainee(), "Trainee cannot be null");
        Objects.requireNonNull(training.getTrainer(), "Trainer cannot be null");
        Objects.requireNonNull(training.getTrainingType(), "Training type cannot be null");
        log.info("Creating training: {}", training.getTrainingName());

        Trainee trainee = traineeDao.findById(training.getTrainee().getId())
                .orElseThrow(() -> EntityNotFoundException.forId("Trainee", training.getTrainee().getId()));
        Trainer trainer = trainerDao.findById(training.getTrainer().getId())
                .orElseThrow(() -> EntityNotFoundException.forId("Trainer", training.getTrainer().getId()));
        TrainingType trainingType = trainingTypeDao.findById(training.getTrainingType().getId())
                .orElseThrow(() -> EntityNotFoundException.forId("TrainingType", training.getTrainingType().getId()));

        Training trainingWithAssociations = training.toBuilder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        Training createdTraining = trainingDao.save(trainingWithAssociations);
        log.info("Training created with ID: {} for trainee ID: {} and trainer ID: {}",
                createdTraining.getId(), createdTraining.getTrainee().getId(), createdTraining.getTrainer().getId());

        return createdTraining;
    }

    @Override
    public Training getTraining(Long trainingId) {
        Objects.requireNonNull(trainingId, "Training ID cannot be null");

        return trainingDao.findById(trainingId)
                .orElseThrow(() -> EntityNotFoundException.forId("Training", trainingId));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }

    @Override
    public List<Training> getTrainingsByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");

        return trainingDao.findAllByTraineeCriteria(filter);
    }

    @Override
    public List<Training> getTrainingsByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");

        return trainingDao.findAllByTrainerCriteria(filter);
    }

}
