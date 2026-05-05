package com.gym.crm.facade;

import com.gym.crm.dto.PasswordUpdateDto;
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
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final Validator validator;

    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;

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
        validate(traineeCreateDto);

        Trainee trainee = traineeMapper.toEntity(traineeCreateDto);
        Trainee createdTrainee = traineeService.createTrainee(trainee);

        return traineeMapper.toCreateResponseDto(createdTrainee);
    }

    public TraineeResponseDto getTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        Trainee trainee = traineeService.getTrainee(traineeId);
        return traineeMapper.toDto(trainee);
    }

    public TraineeResponseDto getTraineeByUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");

        Trainee trainee = traineeService.getTraineeByUsername(username);
        return traineeMapper.toDto(trainee);
    }

    public List<TraineeResponseDto> getAllTrainees() {
        List<Trainee> trainees = traineeService.getAllTrainees();

        return traineeMapper.toDtoList(trainees);
    }

    public boolean doesTraineeUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");

        return traineeService.doesUsernameAndPasswordMatch(username, password);
    }

    public TraineeResponseDto updateTrainee(Long traineeId, TraineeUpdateDto traineeUpdateDto) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(traineeUpdateDto, "TraineeUpdateDto cannot be null");
        validate(traineeUpdateDto);

        Trainee trainee = traineeMapper.toEntity(traineeUpdateDto, traineeId);
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);

        return traineeMapper.toDto(updatedTrainee);
    }

    public TraineeResponseDto updateTraineeTrainers(Long traineeId, List<Long> trainerIds) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(trainerIds, "Trainer IDs cannot be null");

        Trainee trainee = traineeService.updateTraineeTrainers(traineeId, trainerIds);

        return traineeMapper.toDto(trainee);
    }

    public void updateTraineePassword(Long traineeId, PasswordUpdateDto passwordUpdateDto) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");
        Objects.requireNonNull(passwordUpdateDto, "PasswordUpdateDto cannot be null");
        validate(passwordUpdateDto);

        traineeService.updateTraineePassword(traineeId, passwordUpdateDto.password());
    }

    public void activateTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        traineeService.activateTrainee(traineeId);
    }

    public void deactivateTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        traineeService.deactivateTrainee(traineeId);
    }

    public boolean deleteTrainee(Long traineeId) {
        Objects.requireNonNull(traineeId, "Trainee ID cannot be null");

        return traineeService.deleteTrainee(traineeId);
    }

    public boolean deleteTraineeByUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");

        return traineeService.deleteTraineeByUsername(username);
    }

    public TrainerCreateResponseDto createTrainer(TrainerCreateDto trainerCreateDto) {
        Objects.requireNonNull(trainerCreateDto, "TrainerCreateDto cannot be null");
        validate(trainerCreateDto);

        Trainer trainer = trainerMapper.toEntity(trainerCreateDto);
        Trainer createdTrainer = trainerService.createTrainer(trainer);

        return trainerMapper.toCreateResponseDto(createdTrainer);
    }

    public TrainerResponseDto getTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        Trainer trainer = trainerService.getTrainer(trainerId);
        return trainerMapper.toDto(trainer);
    }

    public TrainerResponseDto getTrainerByUsername(String username) {
        Objects.requireNonNull(username, "Username cannot be null");

        Trainer trainer = trainerService.getTrainerByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    public List<TrainerResponseDto> getAllTrainers() {
        List<Trainer> trainers = trainerService.getAllTrainers();

        return trainerMapper.toDtoList(trainers);
    }

    public List<TrainerResponseDto> getAllTrainersNotAssignedToTrainee(String traineeUsername) {
        Objects.requireNonNull(traineeUsername, "Trainee username cannot be null");

        List<Trainer> trainers = trainerService.getAllTrainersNotAssignedToTrainee(traineeUsername);
        return trainerMapper.toDtoList(trainers);
    }

    public boolean doesTrainerUsernameAndPasswordMatch(String username, String password) {
        Objects.requireNonNull(username, "Username cannot be null");
        Objects.requireNonNull(password, "Password cannot be null");

        return trainerService.doesUsernameAndPasswordMatch(username, password);
    }

    public TrainerResponseDto updateTrainer(Long trainerId, TrainerUpdateDto trainerUpdateDto) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        Objects.requireNonNull(trainerUpdateDto, "TrainerUpdateDto cannot be null");
        validate(trainerUpdateDto);

        Trainer trainer = trainerMapper.toEntity(trainerUpdateDto, trainerId);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);

        return trainerMapper.toDto(updatedTrainer);
    }

    public void updateTrainerPassword(Long trainerId, PasswordUpdateDto passwordUpdateDto) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");
        Objects.requireNonNull(passwordUpdateDto, "PasswordUpdateDto cannot be null");
        validate(passwordUpdateDto);

        trainerService.updateTrainerPassword(trainerId, passwordUpdateDto.password());
    }

    public void activateTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        trainerService.activateTrainer(trainerId);
    }

    public void deactivateTrainer(Long trainerId) {
        Objects.requireNonNull(trainerId, "Trainer ID cannot be null");

        trainerService.deactivateTrainer(trainerId);
    }

    public TrainingResponseDto createTraining(TrainingCreateDto trainingCreateDto) {
        Objects.requireNonNull(trainingCreateDto, "TrainingCreateDto cannot be null");
        validate(trainingCreateDto);

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

    public List<TrainingResponseDto> getTrainingsByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        validate(filter);

        List<Training> trainings = trainingService.getTrainingsByTraineeCriteria(filter);
        return trainingMapper.toDtoList(trainings);
    }

    public List<TrainingResponseDto> getTrainingsByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        validate(filter);

        List<Training> trainings = trainingService.getTrainingsByTrainerCriteria(filter);
        return trainingMapper.toDtoList(trainings);
    }

    private <T> void validate(T dto) {
        Set<ConstraintViolation<T>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ValidationException(violations);
        }
    }

}
