package com.gym.crm.service;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;

import java.util.List;

public interface TraineeService {

    Trainee createTrainee(Trainee trainee);

    Trainee getTraineeByUsername(String username);

    List<Trainer> getAvailableTrainers(String username);

    List<Training> getTraineeTrainings(TraineeTrainingSearchFilter filter);

    boolean doesUsernameAndPasswordMatch(String username, String password);

    Trainee updateTrainee(Trainee trainee);

    Trainee updateTraineeTrainers(String username, List<String> trainerUsernames);

    void updateTraineePassword(LoginChangeDto loginChangeDto);

    void updateActivationStatus(String username, boolean isActive);

    boolean deleteTraineeByUsername(String username);

}
