package com.gym.crm.service;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;

import java.util.List;

public interface TrainingService {

    Training createTraining(Training training);

    Training getTraining(Long trainingId);

    List<Training> getAllTrainings();

    List<Training> getTrainingsByTraineeCriteria(TraineeTrainingSearchFilter filter);

    List<Training> getTrainingsByTrainerCriteria(TrainerTrainingSearchFilter filter);

}
