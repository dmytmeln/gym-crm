package com.gym.crm.service;

import com.gym.crm.entity.Trainee;

import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    Trainee getTrainee(Long traineeId);

    Trainee getTraineeByUsername(String username);

    List<Trainee> getAllTrainees();

    boolean doesUsernameAndPasswordMatch(String username, String password);

    Trainee updateTrainee(Trainee trainee);

    Trainee updateTraineeTrainers(Long traineeId, List<Long> trainerIds);

    void updateTraineePassword(Long traineeId, String newPassword);

    void activateTrainee(Long traineeId);

    void deactivateTrainee(Long traineeId);

    boolean deleteTrainee(Long traineeId);

    boolean deleteTraineeByUsername(String username);

}
