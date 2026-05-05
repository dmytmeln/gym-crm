package com.gym.crm.dao;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TrainingDao {

    Training save(Training training);

    Optional<Training> findById(Long id);

    List<Training> findAll();

    List<Training> findAllByTraineeCriteria(TraineeTrainingSearchFilter filter);

    List<Training> findAllByTrainerCriteria(TrainerTrainingSearchFilter filter);

}
