package com.gym.crm.service;

import com.gym.crm.entity.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Trainer getTrainer(Long trainerId);

    Trainer getTrainerByUsername(String username);

    List<Trainer> getAllTrainers();

    List<Trainer> getAllTrainersNotAssignedToTrainee(String traineeUsername);

    boolean doesUsernameAndPasswordMatch(String username, String password);

    Trainer updateTrainer(Trainer trainer);

    void updateTrainerPassword(Long trainerId, String newPassword);

    void activateTrainer(Long trainerId);

    void deactivateTrainer(Long trainerId);

}
