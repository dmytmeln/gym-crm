package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
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

import static com.gym.crm.entity.EntityType.TRAINEE;
import static com.gym.crm.entity.EntityType.TRAINER;

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
        log.info("Creating training: {}", training.getTrainingName());

        String traineeUsername = training.getTrainee().getUser().getUsername();
        String trainerUsername = training.getTrainer().getUser().getUsername();
        Trainee trainee = traineeDao.findByUsername(traineeUsername)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, traineeUsername));
        Trainer trainer = trainerDao.findByUsername(trainerUsername)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINER, trainerUsername));
        TrainingType trainingType = trainer.getSpecialization();

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
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeDao.findAll();
    }

}
