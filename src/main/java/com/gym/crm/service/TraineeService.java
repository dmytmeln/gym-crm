package com.gym.crm.service;

import com.gym.crm.entity.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    Trainee updateTrainee(Trainee trainee);

    boolean deleteTrainee(Long traineeId);

    Trainee getTrainee(Long traineeId);

    List<Trainee> getAllTrainees();

}
