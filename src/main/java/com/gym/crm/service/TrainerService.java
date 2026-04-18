package com.gym.crm.service;

import com.gym.crm.entity.Trainer;

import java.util.List;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Trainer updateTrainer(Trainer trainer);

    Trainer getTrainer(Long trainerId);

    List<Trainer> getAllTrainers();

}
