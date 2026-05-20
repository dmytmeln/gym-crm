package com.gym.crm.service;

import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;

import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Trainer getTrainerByUsername(String username);

    List<Training> getTrainerTrainings(TrainerTrainingSearchFilter filter);

    boolean doesUsernameAndPasswordMatch(String username, String password);

    Trainer updateTrainer(Trainer trainer);

    void updateTrainerPassword(LoginChangeDto loginChangeDto);

    void updateActivationStatus(String username, boolean isActive);

}
