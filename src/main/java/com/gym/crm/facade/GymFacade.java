package com.gym.crm.facade;

import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.dto.LoginChangeDto;
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
import com.gym.crm.mapper.AuthMapper;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.security.Authenticated;
import com.gym.crm.security.Role;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.BusinessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GymFacade {

    private static final String USERNAME_NULL_MSG = "Username cannot be null";
    private static final String TRAINER_ID_NULL_MSG = "Trainer ID cannot be null";
    private static final String TRAINEE_ID_NULL_MSG = "Trainee ID cannot be null";

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final AuthenticationService authenticationService;
    private final BusinessValidator validator;

    private TraineeMapper traineeMapper;
    private TrainerMapper trainerMapper;
    private TrainingMapper trainingMapper;
    private AuthMapper authMapper;

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

    @Autowired
    public void setAuthMapper(AuthMapper authMapper) {
        this.authMapper = authMapper;
    }

    public void login(LoginRequest loginRequest) {
        Objects.requireNonNull(loginRequest, "LoginRequestDto cannot be null");

        authenticationService.login(authMapper.toDto(loginRequest));
    }

    @Authenticated({Role.TRAINEE, Role.TRAINER})
    public void changePassword(String username, LoginChangeRequest loginChangeRequest) {
        Objects.requireNonNull(loginChangeRequest, "LoginChangeRequest cannot be null");
        LoginChangeDto loginChangeDto = authMapper.toDto(loginChangeRequest);
        validator.validate(loginChangeDto);

        authenticationService.changePassword(loginChangeDto);
    }

    public TraineeCreateResponseDto createTrainee(TraineeCreateDto traineeCreateDto) {
        Objects.requireNonNull(traineeCreateDto, "TraineeCreateDto cannot be null");
        validator.validate(traineeCreateDto);

        Trainee trainee = traineeMapper.toEntity(traineeCreateDto);
        Trainee createdTrainee = traineeService.createTrainee(trainee);

        return traineeMapper.toCreateResponseDto(createdTrainee);
    }

    @Authenticated(Role.TRAINEE)
    public TraineeResponseDto getTrainee(String username, Long traineeId) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);

        Trainee trainee = traineeService.getTrainee(traineeId);
        return traineeMapper.toDto(trainee);
    }

    @Authenticated(Role.TRAINEE)
    public TraineeResponseDto getTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        Trainee trainee = traineeService.getTraineeByUsername(username);
        return traineeMapper.toDto(trainee);
    }

    @Authenticated(Role.TRAINER)
    public List<TraineeResponseDto> getAllTrainees(String username) {
        List<Trainee> trainees = traineeService.getAllTrainees();

        return traineeMapper.toDtoList(trainees);
    }

    @Authenticated(Role.TRAINEE)
    public TraineeResponseDto updateTrainee(String username, Long traineeId, TraineeUpdateDto traineeUpdateDto) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);
        Objects.requireNonNull(traineeUpdateDto, "TraineeUpdateDto cannot be null");
        validator.validate(traineeUpdateDto);

        Trainee trainee = traineeMapper.toEntity(traineeUpdateDto, traineeId);
        Trainee updatedTrainee = traineeService.updateTrainee(trainee);

        return traineeMapper.toDto(updatedTrainee);
    }

    @Authenticated(Role.TRAINEE)
    public TraineeResponseDto updateTraineeTrainers(String username, Long traineeId, List<Long> trainerIds) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);
        Objects.requireNonNull(trainerIds, "Trainer IDs cannot be null");

        Trainee trainee = traineeService.updateTraineeTrainers(traineeId, trainerIds);

        return traineeMapper.toDto(trainee);
    }

    @Authenticated(Role.TRAINEE)
    public void activateTrainee(String username, Long traineeId) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);

        traineeService.activateTrainee(traineeId);
    }

    @Authenticated(Role.TRAINEE)
    public void deactivateTrainee(String username, Long traineeId) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);

        traineeService.deactivateTrainee(traineeId);
    }

    @Authenticated(Role.TRAINEE)
    public boolean deleteTrainee(String username, Long traineeId) {
        Objects.requireNonNull(traineeId, TRAINEE_ID_NULL_MSG);

        return traineeService.deleteTrainee(traineeId);
    }

    @Authenticated(Role.TRAINEE)
    public boolean deleteTraineeByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        return traineeService.deleteTraineeByUsername(username);
    }

    public TrainerCreateResponseDto createTrainer(TrainerCreateDto trainerCreateDto) {
        Objects.requireNonNull(trainerCreateDto, "TrainerCreateDto cannot be null");
        validator.validate(trainerCreateDto);

        Trainer trainer = trainerMapper.toEntity(trainerCreateDto);
        Trainer createdTrainer = trainerService.createTrainer(trainer);

        return trainerMapper.toCreateResponseDto(createdTrainer);
    }

    @Authenticated(Role.TRAINER)
    public TrainerResponseDto getTrainer(String username, Long trainerId) {
        Objects.requireNonNull(trainerId, TRAINER_ID_NULL_MSG);

        Trainer trainer = trainerService.getTrainer(trainerId);
        return trainerMapper.toDto(trainer);
    }

    @Authenticated(Role.TRAINER)
    public TrainerResponseDto getTrainerByUsername(String username) {
        Objects.requireNonNull(username, USERNAME_NULL_MSG);

        Trainer trainer = trainerService.getTrainerByUsername(username);
        return trainerMapper.toDto(trainer);
    }

    @Authenticated(Role.TRAINEE)
    public List<TrainerResponseDto> getAllTrainers(String username) {
        List<Trainer> trainers = trainerService.getAllTrainers();

        return trainerMapper.toDtoList(trainers);
    }

    @Authenticated(Role.TRAINEE)
    public List<TrainerResponseDto> getAllTrainersNotAssignedToTrainee(String username) {
        Objects.requireNonNull(username, "Trainee username cannot be null");

        List<Trainer> trainers = trainerService.getAllTrainersNotAssignedToTrainee(username);
        return trainerMapper.toDtoList(trainers);
    }

    @Authenticated(Role.TRAINER)
    public TrainerResponseDto updateTrainer(String username, Long trainerId, TrainerUpdateDto trainerUpdateDto) {
        Objects.requireNonNull(trainerId, TRAINER_ID_NULL_MSG);
        Objects.requireNonNull(trainerUpdateDto, "TrainerUpdateDto cannot be null");
        validator.validate(trainerUpdateDto);

        Trainer trainer = trainerMapper.toEntity(trainerUpdateDto, trainerId);
        Trainer updatedTrainer = trainerService.updateTrainer(trainer);

        return trainerMapper.toDto(updatedTrainer);
    }

    @Authenticated(Role.TRAINER)
    public void activateTrainer(String username, Long trainerId) {
        Objects.requireNonNull(trainerId, TRAINER_ID_NULL_MSG);

        trainerService.activateTrainer(trainerId);
    }

    @Authenticated(Role.TRAINER)
    public void deactivateTrainer(String username, Long trainerId) {
        Objects.requireNonNull(trainerId, TRAINER_ID_NULL_MSG);

        trainerService.deactivateTrainer(trainerId);
    }

    @Authenticated(Role.TRAINER)
    public TrainingResponseDto createTraining(String username, TrainingCreateDto trainingCreateDto) {
        Objects.requireNonNull(trainingCreateDto, "TrainingCreateDto cannot be null");
        validator.validate(trainingCreateDto);

        Training training = trainingMapper.toEntity(trainingCreateDto);
        Training createdTraining = trainingService.createTraining(training);

        return trainingMapper.toDto(createdTraining);
    }

    @Authenticated({Role.TRAINER, Role.TRAINEE})
    public TrainingResponseDto getTraining(String username, Long trainingId) {
        Objects.requireNonNull(trainingId, "Training ID cannot be null");

        Training training = trainingService.getTraining(trainingId);
        return trainingMapper.toDto(training);
    }

    @Authenticated({Role.TRAINER, Role.TRAINEE})
    public List<TrainingResponseDto> getAllTrainings(String username) {
        List<Training> trainings = trainingService.getAllTrainings();
        return trainingMapper.toDtoList(trainings);
    }

    @Authenticated(Role.TRAINEE)
    public List<TrainingResponseDto> getTrainingsByTraineeCriteria(String username, TraineeTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        validator.validate(filter);

        List<Training> trainings = trainingService.getTrainingsByTraineeCriteria(filter);
        return trainingMapper.toDtoList(trainings);
    }

    @Authenticated(Role.TRAINER)
    public List<TrainingResponseDto> getTrainingsByTrainerCriteria(String username, TrainerTrainingSearchFilter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        validator.validate(filter);

        List<Training> trainings = trainingService.getTrainingsByTrainerCriteria(filter);
        return trainingMapper.toDtoList(trainings);
    }

}
