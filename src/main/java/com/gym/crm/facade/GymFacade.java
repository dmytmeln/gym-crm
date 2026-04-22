package com.gym.crm.facade;

import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;

    @Autowired
    public GymFacade(
            TraineeService traineeService,
            TrainerService trainerService,
            TrainingService trainingService
    ) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
    }

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Autowired
    public void setTrainingMapper(TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    public TraineeCreateResponseDto createTrainee(TraineeCreateDto traineeCreateDto) {
        Objects.requireNonNull(traineeCreateDto, "TraineeCreateDto cannot be null");

        Trainee trainee = traineeMapper.toEntity(traineeCreateDto);
        Trainee createdTrainee = traineeService.createTrainee(trainee);

        return traineeMapper.toCreateResponseDto(createdTrainee);
    }

    public TraineeResponseDto updateTrainee(Long traineeId, TraineeUpdateDto traineeUpdateDto) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(traineeUpdateDto, "TraineeUpdateDto cannot be null");

        Trainee trainee = traineeMapper.toEntity(traineeUpdateDto, traineeId);
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);

        return traineeMapper.toDto(updatedTrainee);
    }

    public boolean deleteTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        return traineeService.deleteTrainee(traineeId);
    }

    public TraineeResponseDto getTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        Trainee trainee = traineeService.getTrainee(traineeId);

        return traineeMapper.toDto(trainee);
    }

    public List<TraineeResponseDto> getAllTrainees() {
        List<Trainee> trainees = traineeService.getAllTrainees();

        return traineeMapper.toDtoList(trainees);
    }

    public TrainerCreateResponseDto createTrainer(TrainerCreateDto trainerCreateDto) {
        Objects.requireNonNull(trainerCreateDto, "TrainerCreateDto cannot be null");

        Trainer trainer = trainerMapper.toEntity(trainerCreateDto);
        Trainer createdTrainer = trainerService.createTrainer(trainer);

        return trainerMapper.toCreateResponseDto(createdTrainer);
    }

    public TrainerResponseDto updateTrainer(Long trainerId, TrainerUpdateDto trainerUpdateDto) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        Objects.requireNonNull(trainerUpdateDto, "TrainerUpdateDto cannot be null");

        Trainer trainer = trainerMapper.toEntity(trainerUpdateDto, trainerId);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);

        return trainerMapper.toDto(updatedTrainer);
    }

    public TrainerResponseDto getTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        Trainer trainer = trainerService.getTrainer(trainerId);

        return trainerMapper.toDto(trainer);
    }

    public List<TrainerResponseDto> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();

        return trainerMapper.toDtoList(trainers);
    }

    public TrainingResponseDto createTraining(TrainingCreateDto trainingCreateDto) {
        Objects.requireNonNull(trainingCreateDto, "TrainingCreateDto cannot be null");

        Training training = trainingMapper.toEntity(trainingCreateDto);
        Training createdTraining = trainingService.createTraining(training);

        return trainingMapper.toDto(createdTraining);
    }

    public TrainingResponseDto getTraining(Long trainingId) {
        Objects.requireNonNull(trainingId, "Training ID cannot be null");

        Training training = trainingService.getTraining(trainingId);

        return trainingMapper.toDto(training);
    }

    public List<TrainingResponseDto> getAllTrainings() {
        List<Training> trainings = trainingService.getAllTrainings();

        return trainingMapper.toDtoList(trainings);
    }

}
