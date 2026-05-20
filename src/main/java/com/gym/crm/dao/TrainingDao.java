package com.gym.crm.dao;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;

import java.util.List;

public interface TrainingDao {

    Training save(Training training);

    List<Training> findTrainerTrainingsByCriteria(TrainerTrainingSearchFilter filter);

    List<Training> findTraineeTrainingsByCriteria(TraineeTrainingSearchFilter filter);

}
