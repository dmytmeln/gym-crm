package com.gym.crm.service;

import com.gym.crm.model.Training;

import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);

    Training getTraining(Long trainingId);

    List<Training> getAllTrainings();

}
