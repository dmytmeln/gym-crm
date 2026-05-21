package com.gym.crm.service;

import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;

import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);

    List<TrainingType> getAllTrainingTypes();

}
