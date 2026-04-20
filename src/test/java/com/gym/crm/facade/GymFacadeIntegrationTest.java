package com.gym.crm.facade;

import com.gym.crm.GymCrmApplication;
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
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.factory.TraineeTestFactory;
import com.gym.crm.factory.TrainerTestFactory;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.storage.impl.TraineeNamespaceStorage;
import com.gym.crm.storage.impl.TrainerNamespaceStorage;
import com.gym.crm.storage.impl.TrainingNamespaceStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.traineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerUpdateDto;
import static com.gym.crm.factory.TrainingTestFactory.trainingCreateDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(GymCrmApplication.class)
public class GymFacadeIntegrationTest {

    @Autowired
    private GymFacade gymFacade;

    @Autowired
    private TraineeService traineeService;

    @Autowired
    private TrainerService trainerService;

    @Autowired
    private TrainingService trainingService;

    @Autowired
    private TraineeNamespaceStorage traineeNamespaceStorage;

    @Autowired
    private TrainerNamespaceStorage trainerNamespaceStorage;

    @Autowired
    private TrainingNamespaceStorage trainingNamespaceStorage;

    @BeforeEach
    @AfterEach
    public void resetStorage() {
        trainingNamespaceStorage.clear();
        trainerNamespaceStorage.clear();
        traineeNamespaceStorage.clear();
    }

    @Test
    public void shouldInitializeFacadeAndServicesInSpringContext() {
        assertNotNull(gymFacade);
        assertNotNull(traineeService);
        assertNotNull(trainerService);
        assertNotNull(trainingService);
    }

    @Test
    public void shouldCreateTrainee() {
        TraineeCreateDto traineeDto = traineeCreateDto();

        TraineeCreateResponseDto result = gymFacade.createTrainee(traineeDto);

        assertNotNull(result);
        assertNotNull(result.userId());
        assertNotNull(result.username());
        assertNotNull(result.password());
        assertEquals(TraineeTestFactory.DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(TraineeTestFactory.DEFAULT_LAST_NAME, result.lastName());
        assertEquals(TraineeTestFactory.DEFAULT_ACTIVE, result.active());
        assertEquals(TraineeTestFactory.DEFAULT_ADDRESS, result.address());
        assertEquals(TraineeTestFactory.DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    public void shouldGetTraineeById() {
        TraineeCreateResponseDto created = gymFacade.createTrainee(traineeCreateDto());

        TraineeResponseDto result = gymFacade.getTrainee(created.userId());

        assertNotNull(result);
        assertEquals(created.userId(), result.userId());
        assertEquals(created.username(), result.username());
        assertEquals(TraineeTestFactory.DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(TraineeTestFactory.DEFAULT_LAST_NAME, result.lastName());
        assertEquals(TraineeTestFactory.DEFAULT_ACTIVE, result.active());
        assertEquals(TraineeTestFactory.DEFAULT_ADDRESS, result.address());
        assertEquals(TraineeTestFactory.DEFAULT_DATE_OF_BIRTH, result.dateOfBirth());
    }

    @Test
    public void shouldUpdateTrainee() {
        TraineeCreateResponseDto created = gymFacade.createTrainee(traineeCreateDto());
        TraineeUpdateDto updateDto = traineeUpdateDto();

        TraineeResponseDto result = gymFacade.updateTrainee(created.userId(), updateDto);

        assertNotNull(result);
        assertEquals(created.userId(), result.userId());
        assertEquals(created.username(), result.username());
        assertEquals(updateDto.firstName(), result.firstName());
        assertEquals(updateDto.lastName(), result.lastName());
        assertFalse(result.active());
        assertEquals(updateDto.address(), result.address());
        assertEquals(updateDto.dateOfBirth(), result.dateOfBirth());
    }

    @Test
    public void shouldDeleteTrainee() {
        TraineeCreateResponseDto created = gymFacade.createTrainee(traineeCreateDto());

        boolean result = gymFacade.deleteTrainee(created.userId());

        assertTrue(result);
        assertThrows(EntityNotFoundException.class, () -> gymFacade.getTrainee(created.userId()));
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        boolean result = gymFacade.deleteTrainee(NON_EXISTENT_TRAINEE_ID);

        assertFalse(result);
    }

    @Test
    public void shouldGetAllTrainees() {
        TraineeCreateResponseDto created = gymFacade.createTrainee(traineeCreateDto());

        List<TraineeResponseDto> result = gymFacade.getAllTrainees();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(trainee ->
                trainee.userId().equals(created.userId()) &&
                        trainee.username().equals(created.username())
        ));
    }

    @Test
    public void shouldReturnEmptyListWhenNoTraineesExist() {
        List<TraineeResponseDto> result = gymFacade.getAllTrainees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldCreateTrainer() {
        TrainerCreateDto trainerDto = trainerCreateDto();

        TrainerCreateResponseDto result = gymFacade.createTrainer(trainerDto);

        assertNotNull(result);
        assertNotNull(result.userId());
        assertNotNull(result.username());
        assertNotNull(result.password());
        assertEquals(TrainerTestFactory.DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(TrainerTestFactory.DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(TrainerTestFactory.DEFAULT_SPECIALIZATION, result.specialization().getTrainingTypeName());
    }

    @Test
    public void shouldGetTrainerById() {
        TrainerCreateResponseDto created = gymFacade.createTrainer(trainerCreateDto());

        TrainerResponseDto result = gymFacade.getTrainer(created.userId());

        assertNotNull(result);
        assertEquals(created.userId(), result.userId());
        assertEquals(created.username(), result.username());
        assertEquals(TrainerTestFactory.DEFAULT_FIRST_NAME, result.firstName());
        assertEquals(TrainerTestFactory.DEFAULT_LAST_NAME, result.lastName());
        assertTrue(result.active());
        assertEquals(TrainerTestFactory.DEFAULT_SPECIALIZATION, result.specialization().getTrainingTypeName());
    }

    @Test
    public void shouldUpdateTrainer() {
        TrainerCreateResponseDto created = gymFacade.createTrainer(trainerCreateDto());
        TrainerUpdateDto updateDto = trainerUpdateDto();

        TrainerResponseDto result = gymFacade.updateTrainer(created.userId(), updateDto);

        assertNotNull(result);
        assertEquals(created.userId(), result.userId());
        assertEquals(created.username(), result.username());
        assertEquals(updateDto.firstName(), result.firstName());
        assertEquals(updateDto.lastName(), result.lastName());
        assertFalse(result.active());
        assertEquals(updateDto.specialization().getTrainingTypeName(), result.specialization().getTrainingTypeName());
    }

    @Test
    public void shouldGetAllTrainers() {
        TrainerCreateResponseDto created = gymFacade.createTrainer(trainerCreateDto());

        List<TrainerResponseDto> result = gymFacade.getAllTrainers();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(trainer ->
                trainer.userId().equals(created.userId()) &&
                        trainer.username().equals(created.username())
        ));
    }

    @Test
    public void shouldReturnEmptyListWhenNoTrainersExist() {
        List<TrainerResponseDto> result = gymFacade.getAllTrainers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldCreateTraining() {
        TraineeCreateResponseDto createdTrainee = gymFacade.createTrainee(traineeCreateDto());
        TrainerCreateResponseDto createdTrainer = gymFacade.createTrainer(trainerCreateDto());
        TrainingCreateDto trainingDto = trainingCreateDto(createdTrainee.userId(), createdTrainer.userId());

        TrainingResponseDto result = gymFacade.createTraining(trainingDto);

        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(createdTrainee.userId(), result.traineeId());
        assertEquals(createdTrainer.userId(), result.trainerId());
        assertEquals(trainingDto.trainingName(), result.trainingName());
        assertEquals(trainingDto.trainingDuration(), result.trainingDuration());
        assertEquals(trainingDto.trainingType().getTrainingTypeName(), result.trainingType().getTrainingTypeName());
        assertEquals(trainingDto.trainingDate(), result.trainingDate());
    }

    @Test
    public void shouldGetTrainingById() {
        TraineeCreateResponseDto createdTrainee = gymFacade.createTrainee(traineeCreateDto());
        TrainerCreateResponseDto createdTrainer = gymFacade.createTrainer(trainerCreateDto());
        TrainingResponseDto created = gymFacade.createTraining(trainingCreateDto(createdTrainee.userId(), createdTrainer.userId()));

        TrainingResponseDto result = gymFacade.getTraining(created.id());

        assertNotNull(result);
        assertEquals(created.id(), result.id());
        assertEquals(created.traineeId(), result.traineeId());
        assertEquals(created.trainerId(), result.trainerId());
        assertEquals(created.trainingName(), result.trainingName());
        assertEquals(created.trainingDuration(), result.trainingDuration());
        assertEquals(created.trainingType().getTrainingTypeName(), result.trainingType().getTrainingTypeName());
        assertEquals(created.trainingDate(), result.trainingDate());
    }

    @Test
    public void shouldGetAllTrainings() {
        TraineeCreateResponseDto createdTrainee = gymFacade.createTrainee(traineeCreateDto());
        TrainerCreateResponseDto createdTrainer = gymFacade.createTrainer(trainerCreateDto());
        TrainingResponseDto created = gymFacade.createTraining(trainingCreateDto(createdTrainee.userId(), createdTrainer.userId()));

        List<TrainingResponseDto> result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertTrue(result.stream().anyMatch(training ->
                training.id().equals(created.id()) &&
                        training.traineeId().equals(createdTrainee.userId()) &&
                        training.trainerId().equals(createdTrainer.userId())
        ));
    }

    @Test
    public void shouldReturnEmptyListWhenNoTrainingsExist() {
        List<TrainingResponseDto> result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
