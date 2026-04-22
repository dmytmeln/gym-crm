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
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerUpdateDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(GymCrmApplication.class)
class GymFacadeIntegrationTest {

    @Autowired
    private GymFacade facade;

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
    void resetStorage() {
        clearNamespaceStorage(trainingNamespaceStorage);
        clearNamespaceStorage(trainerNamespaceStorage);
        clearNamespaceStorage(traineeNamespaceStorage);
    }

    private void clearNamespaceStorage(Object storageInstance) {
        Map<?, ?> storageMap = (Map<?, ?>) ReflectionTestUtils.getField(storageInstance, "storage");
        if (storageMap != null) {
            storageMap.clear();
        }
    }

    @Test
    void shouldInitializeFacadeAndServicesInSpringContext() {
        assertNotNull(facade);
        assertNotNull(traineeService);
        assertNotNull(trainerService);
        assertNotNull(trainingService);
    }

    @Test
    void shouldCreateTrainee() {
        TraineeCreateDto traineeDto = buildTraineeCreateDto();

        TraineeCreateResponseDto actual = facade.createTrainee(traineeDto);

        assertNotNull(actual);
        assertNotNull(actual.userId());
        assertNotNull(actual.username());
        assertNotNull(actual.password());
        assertEquals(traineeDto.firstName(), actual.firstName());
        assertEquals(traineeDto.lastName(), actual.lastName());
        assertEquals(traineeDto.active(), actual.active());
        assertEquals(traineeDto.address(), actual.address());
        assertEquals(traineeDto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldGetTraineeById() {
        TraineeCreateDto traineeDto = buildTraineeCreateDto();
        TraineeCreateResponseDto created = facade.createTrainee(traineeDto);

        TraineeResponseDto actual = facade.getTrainee(created.userId());

        assertNotNull(actual);
        assertEquals(created.userId(), actual.userId());
        assertEquals(created.username(), actual.username());
        assertEquals(traineeDto.firstName(), actual.firstName());
        assertEquals(traineeDto.lastName(), actual.lastName());
        assertEquals(traineeDto.active(), actual.active());
        assertEquals(traineeDto.address(), actual.address());
        assertEquals(traineeDto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldUpdateTrainee() {
        TraineeCreateResponseDto created = facade.createTrainee(buildTraineeCreateDto());
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();

        TraineeResponseDto actual = facade.updateTrainee(created.userId(), updateDto);

        assertNotNull(actual);
        assertEquals(created.userId(), actual.userId());
        assertEquals(created.username(), actual.username());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
        assertEquals(updateDto.address(), actual.address());
        assertEquals(updateDto.dateOfBirth(), actual.dateOfBirth());
    }

    @Test
    void shouldDeleteTrainee() {
        TraineeCreateResponseDto created = facade.createTrainee(buildTraineeCreateDto());

        boolean actual = facade.deleteTrainee(created.userId());

        assertTrue(actual);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> facade.getTrainee(created.userId()));

        assertEquals("Trainee not found with id: " + created.userId(), exception.getMessage());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        boolean actual = facade.deleteTrainee(NON_EXISTENT_TRAINEE_ID);

        assertFalse(actual);
    }

    @Test
    void shouldGetAllTrainees() {
        TraineeCreateDto traineeDto = buildTraineeCreateDto();
        TraineeCreateResponseDto created = facade.createTrainee(traineeDto);

        List<TraineeResponseDto> actual = facade.getAllTrainees();

        assertNotNull(actual);
        assertEquals(1, actual.size());

        TraineeResponseDto trainee = actual.get(0);
        assertEquals(created.userId(), trainee.userId());
        assertEquals(created.username(), trainee.username());
        assertEquals(traineeDto.firstName(), trainee.firstName());
        assertEquals(traineeDto.lastName(), trainee.lastName());
    }

    @Test
    void shouldReturnEmptyListWhenNoTraineesExist() {
        List<TraineeResponseDto> actual = facade.getAllTrainees();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldCreateTrainer() {
        TrainerCreateDto trainerDto = buildTrainerCreateDto();

        TrainerCreateResponseDto actual = facade.createTrainer(trainerDto);

        assertNotNull(actual);
        assertNotNull(actual.userId());
        assertNotNull(actual.username());
        assertNotNull(actual.password());
        assertEquals(trainerDto.firstName(), actual.firstName());
        assertEquals(trainerDto.lastName(), actual.lastName());
        assertEquals(trainerDto.active(), actual.active());
        assertEquals(trainerDto.specialization().getTrainingTypeName(), actual.specialization().getTrainingTypeName());
    }

    @Test
    void shouldGetTrainerById() {
        TrainerCreateDto trainerDto = buildTrainerCreateDto();
        TrainerCreateResponseDto created = facade.createTrainer(trainerDto);

        TrainerResponseDto actual = facade.getTrainer(created.userId());

        assertNotNull(actual);
        assertEquals(created.userId(), actual.userId());
        assertEquals(created.username(), actual.username());
        assertEquals(trainerDto.firstName(), actual.firstName());
        assertEquals(trainerDto.lastName(), actual.lastName());
        assertEquals(trainerDto.active(), actual.active());
        assertEquals(trainerDto.specialization().getTrainingTypeName(), actual.specialization().getTrainingTypeName());
    }

    @Test
    void shouldUpdateTrainer() {
        TrainerCreateResponseDto created = facade.createTrainer(buildTrainerCreateDto());
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();

        TrainerResponseDto actual = facade.updateTrainer(created.userId(), updateDto);

        assertNotNull(actual);
        assertEquals(created.userId(), actual.userId());
        assertEquals(created.username(), actual.username());
        assertEquals(updateDto.firstName(), actual.firstName());
        assertEquals(updateDto.lastName(), actual.lastName());
        assertEquals(updateDto.active(), actual.active());
        assertEquals(updateDto.specialization().getTrainingTypeName(), actual.specialization().getTrainingTypeName());
    }

    @Test
    void shouldGetAllTrainers() {
        TrainerCreateDto trainerDto = buildTrainerCreateDto();
        TrainerCreateResponseDto created = facade.createTrainer(trainerDto);

        List<TrainerResponseDto> actual = facade.getAllTrainers();

        assertNotNull(actual);
        assertEquals(1, actual.size());

        TrainerResponseDto trainer = actual.get(0);
        assertEquals(created.userId(), trainer.userId());
        assertEquals(created.username(), trainer.username());
        assertEquals(trainerDto.firstName(), trainer.firstName());
        assertEquals(trainerDto.lastName(), trainer.lastName());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainersExist() {
        List<TrainerResponseDto> actual = facade.getAllTrainers();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldCreateTraining() {
        TraineeCreateResponseDto createdTrainee = facade.createTrainee(buildTraineeCreateDto());
        TrainerCreateResponseDto createdTrainer = facade.createTrainer(buildTrainerCreateDto());
        TrainingCreateDto trainingDto = buildTrainingCreateDto(createdTrainee.userId(), createdTrainer.userId());

        TrainingResponseDto actual = facade.createTraining(trainingDto);

        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(trainingDto.traineeId(), actual.traineeId());
        assertEquals(trainingDto.trainerId(), actual.trainerId());
        assertEquals(trainingDto.trainingName(), actual.trainingName());
        assertEquals(trainingDto.trainingDuration(), actual.trainingDuration());
        assertEquals(trainingDto.trainingType().getTrainingTypeName(), actual.trainingType().getTrainingTypeName());
        assertEquals(trainingDto.trainingDate(), actual.trainingDate());
    }

    @Test
    void shouldGetTrainingById() {
        TraineeCreateResponseDto createdTrainee = facade.createTrainee(buildTraineeCreateDto());
        TrainerCreateResponseDto createdTrainer = facade.createTrainer(buildTrainerCreateDto());
        TrainingCreateDto trainingDto = buildTrainingCreateDto(createdTrainee.userId(), createdTrainer.userId());
        TrainingResponseDto created = facade.createTraining(trainingDto);

        TrainingResponseDto actual = facade.getTraining(created.id());

        assertNotNull(actual);
        assertEquals(created.id(), actual.id());
        assertEquals(trainingDto.traineeId(), actual.traineeId());
        assertEquals(trainingDto.trainerId(), actual.trainerId());
        assertEquals(trainingDto.trainingName(), actual.trainingName());
        assertEquals(trainingDto.trainingDuration(), actual.trainingDuration());
        assertEquals(trainingDto.trainingType().getTrainingTypeName(), actual.trainingType().getTrainingTypeName());
        assertEquals(trainingDto.trainingDate(), actual.trainingDate());
    }

    @Test
    void shouldGetAllTrainings() {
        TraineeCreateResponseDto createdTrainee = facade.createTrainee(buildTraineeCreateDto());
        TrainerCreateResponseDto createdTrainer = facade.createTrainer(buildTrainerCreateDto());
        TrainingCreateDto trainingDto = buildTrainingCreateDto(createdTrainee.userId(), createdTrainer.userId());
        TrainingResponseDto created = facade.createTraining(trainingDto);

        List<TrainingResponseDto> actual = facade.getAllTrainings();

        assertNotNull(actual);
        assertEquals(1, actual.size());

        TrainingResponseDto training = actual.get(0);
        assertEquals(created.id(), training.id());
        assertEquals(trainingDto.traineeId(), training.traineeId());
        assertEquals(trainingDto.trainerId(), training.trainerId());
        assertEquals(trainingDto.trainingName(), training.trainingName());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainingsExist() {
        List<TrainingResponseDto> actual = facade.getAllTrainings();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

}
