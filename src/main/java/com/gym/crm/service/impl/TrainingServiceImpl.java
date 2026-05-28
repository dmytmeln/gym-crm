package com.gym.crm.service.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.repository.TraineeRepository;
import com.gym.crm.repository.TrainerRepository;
import com.gym.crm.repository.TrainingRepository;
import com.gym.crm.repository.TrainingTypeRepository;
import com.gym.crm.service.TrainingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.gym.crm.entity.EntityType.TRAINEE;
import static com.gym.crm.entity.EntityType.TRAINER;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    @Transactional
    public Training createTraining(Training training) {
        Objects.requireNonNull(training, "Training cannot be null");
        Objects.requireNonNull(training.getTrainee(), "Trainee cannot be null");
        Objects.requireNonNull(training.getTrainer(), "Trainer cannot be null");
        log.info("Creating training: {}", training.getTrainingName());

        String traineeUsername = training.getTrainee().getUser().getUsername();
        String trainerUsername = training.getTrainer().getUser().getUsername();
        Trainee trainee = traineeRepository.findByUsername(traineeUsername)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINEE, traineeUsername));
        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> EntityNotFoundException.forUsername(TRAINER, trainerUsername));
        TrainingType trainingType = trainer.getSpecialization();

        Training trainingWithAssociations = training.toBuilder()
                .trainee(trainee)
                .trainer(trainer)
                .trainingType(trainingType)
                .build();

        Training createdTraining = trainingRepository.save(trainingWithAssociations);
        log.info("Training created with ID: {} for trainee ID: {} and trainer ID: {}",
                createdTraining.getId(), createdTraining.getTrainee().getId(), createdTraining.getTrainer().getId());

        return createdTraining;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeRepository.findAll();
    }

}
