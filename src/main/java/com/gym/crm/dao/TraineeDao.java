package com.gym.crm.dao;

import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {

    Trainee save(Trainee entity);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();

    List<Training> findTrainingsByCriteria(TraineeTrainingSearchFilter filter);

    List<Trainer> findAvailableTrainers(String traineeUsername);

    List<Trainer> findTrainersByUsernames(List<String> usernames);

    Trainee update(Trainee entity);

    boolean deleteByUsername(String username);

}
