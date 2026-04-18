package com.gym.crm.service;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

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

    public Training createTraining(Training training) {
        ensureTrainingParticipantsExist(training);

        return trainingDao.create(training);
    }

    public Training getTraining(Long trainingId) {
        return trainingDao.findById(trainingId)
                .orElseThrow(() -> new EntityNotFoundException("Training", trainingId));
    }

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
