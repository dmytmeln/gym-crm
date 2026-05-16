package com.gym.crm.dao;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeDao {

    Trainee save(Trainee entity);

    Optional<Trainee> findById(Long id);

    Optional<Trainee> findByUsername(String username);

    List<Trainee> findAll();

    List<Trainer> findTraineeAvailableTrainers(String traineeUsername);

    List<Trainer> findTraineeTrainersByUsernames(List<String> trainerUsernames);

    Trainee update(Trainee entity);

    boolean deleteByUsername(String username);

}
