package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    private static final String TRAINEE_USERNAME = "liam.miller";
    private static final String TRAINER_USERNAME = "billy.harrington";
    private static final String TRAINING_TYPE_NAME = "STRENGTH";
    private static final Long DEFAULT_TRAINING_TYPE_ID = 1L;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    private TrainingService service;

    @BeforeEach
    void setUp() {
        TrainingServiceImpl implementation = new TrainingServiceImpl();
        implementation.setTrainingDao(trainingDao);
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainerDao(trainerDao);
        implementation.setTrainingTypeDao(trainingTypeDao);
        service = implementation;
    }

    @Test
    void shouldCreateTrainingWhenParticipantsExist() {
        Training training = buildTrainingWithUsernames();
        TrainingType trainingType = TrainingType.builder().id(DEFAULT_TRAINING_TYPE_ID).trainingTypeName(TRAINING_TYPE_NAME).build();
        Trainee existingTrainee = Trainee.builder().id(1L).build();
        Trainer existingTrainer = Trainer.builder().id(2L).specialization(trainingType).build();
        Training expected = Training.builder()
                .id(1L)
                .trainee(existingTrainee)
                .trainer(existingTrainer)
                .trainingType(trainingType)
                .build();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.of(existingTrainer));
        when(trainingDao.save(any(Training.class))).thenReturn(expected);

        Training actual = service.createTraining(training);

        assertEquals(expected, actual);
        verify(traineeDao).findByUsername(TRAINEE_USERNAME);
        verify(trainerDao).findByUsername(TRAINER_USERNAME);
        verify(trainingDao).save(any(Training.class));
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTraining() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTraining(null));

        assertEquals("Training cannot be null", exception.getMessage());
        verifyNoInteractions(trainingDao, traineeDao, trainerDao, trainingTypeDao);
    }

    @Test
    void shouldThrowNullPointerWhenTraineeIsNull() {
        Training training = Training.builder()
                .trainer(buildTrainerWithUsername())
                .trainingName("Test")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTraining(training));

        assertEquals("Trainee cannot be null", exception.getMessage());
        verifyNoInteractions(trainingDao, traineeDao, trainerDao, trainingTypeDao);
    }

    @Test
    void shouldThrowNullPointerWhenTrainerIsNull() {
        Training training = Training.builder()
                .trainee(buildTraineeWithUsername())
                .trainingName("Test")
                .trainingDate(LocalDate.now())
                .trainingDuration(60)
                .build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTraining(training));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(trainingDao, traineeDao, trainerDao, trainingTypeDao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenTraineeDoesNotExist() {
        Training training = buildTrainingWithUsernames();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        assertEquals("Trainee not found with username: " + TRAINEE_USERNAME, exception.getMessage());
        verify(traineeDao).findByUsername(TRAINEE_USERNAME);
        verify(trainerDao, never()).findByUsername(TRAINER_USERNAME);
        verify(trainingDao, never()).save(any());
    }

    @Test
    void shouldThrowEntityNotFoundWhenTrainerDoesNotExist() {
        Training training = buildTrainingWithUsernames();
        Trainee existingTrainee = Trainee.builder().id(1L).build();

        when(traineeDao.findByUsername(TRAINEE_USERNAME)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findByUsername(TRAINER_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        assertEquals("Trainer not found with username: " + TRAINER_USERNAME, exception.getMessage());
        verify(traineeDao).findByUsername(TRAINEE_USERNAME);
        verify(trainerDao).findByUsername(TRAINER_USERNAME);
        verify(trainingDao, never()).save(any());
    }

    @Test
    void shouldReturnAllTrainingTypes() {
        TrainingType type1 = TrainingType.builder().id(1L).trainingTypeName("Cardio").build();
        TrainingType type2 = TrainingType.builder().id(2L).trainingTypeName("Yoga").build();
        List<TrainingType> expected = List.of(type1, type2);

        when(trainingTypeDao.findAll()).thenReturn(expected);

        List<TrainingType> actual = service.getAllTrainingTypes();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingTypeDao).findAll();
    }

    @Test
    void shouldReturnEmptyTrainingTypes() {
        List<TrainingType> expected = List.of();

        when(trainingTypeDao.findAll()).thenReturn(expected);

        List<TrainingType> actual = service.getAllTrainingTypes();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingTypeDao).findAll();
    }

    private Training buildTrainingWithUsernames() {
        return Training.builder()
                .trainee(buildTraineeWithUsername())
                .trainer(buildTrainerWithUsername())
                .trainingName("Morning Workout")
                .trainingDate(LocalDate.of(2026, 4, 15))
                .trainingDuration(60)
                .build();
    }

    private Trainee buildTraineeWithUsername() {
        User user = User.builder().username(TRAINEE_USERNAME).build();

        return Trainee.builder()
                .id(1L)
                .user(user)
                .build();
    }

    private Trainer buildTrainerWithUsername() {
        User user = User.builder().username(TRAINER_USERNAME).build();
        TrainingType specialization = TrainingType.builder().id(DEFAULT_TRAINING_TYPE_ID).trainingTypeName(TRAINING_TYPE_NAME).build();

        return Trainer.builder()
                .id(2L)
                .user(user)
                .specialization(specialization)
                .build();
    }

}
