package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao;
    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

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

    @Override
    public Training createTraining(Training training) {
        Objects.requireNonNull(training, "Training cannot be null");
        ensureTrainingParticipantsExist(training);

        return trainingDao.create(training);
    }

    @Override
    public Training getTraining(Long trainingId) {
        Objects.requireNonNull(trainingId, "Training ID cannot be null");

        return trainingDao.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training", trainingId));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }

    private void ensureTrainingParticipantsExist(Training training) {
        if (traineeDao.findById(training.getTraineeId()).isEmpty()) {
            throw new EntityNotFoundException("Trainee", training.getTraineeId());
        }

        if (trainerDao.findById(training.getTrainerId()).isEmpty()) {
            throw new EntityNotFoundException("Trainer", training.getTrainerId());
        }
    }

}
