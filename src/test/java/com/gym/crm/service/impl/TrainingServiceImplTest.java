package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.SECONDARY_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.buildTraining;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithId;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    private TrainingService service;

    @BeforeEach
    void setUp() {
        TrainingServiceImpl implementation = new TrainingServiceImpl();
        implementation.setTrainingDao(trainingDao);
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainerDao(trainerDao);
        service = implementation;
    }

    @Test
    void shouldCreateTrainingWhenParticipantsExist() {
        Training training = buildTraining();
        Training expected = buildTrainingWithId(DEFAULT_TRAINING_ID);
        Trainee existingTrainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(trainingDao.create(training)).thenReturn(expected);

        Training actual = service.createTraining(training);

        assertEquals(expected, actual);
        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao).findById(DEFAULT_TRAINER_ID);
        verify(trainingDao).create(training);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTraining() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTraining(null));

        assertEquals("Training cannot be null", exception.getMessage());
        verifyNoInteractions(trainingDao, traineeDao, trainerDao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenTraineeDoesNotExist() {
        Training training = buildTrainingWithoutId(DEFAULT_TRAINEE_ID, DEFAULT_TRAINER_ID);

        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        assertEquals("Trainee not found with id: " + DEFAULT_TRAINEE_ID, exception.getMessage());
        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao, never()).findById(DEFAULT_TRAINER_ID);
        verify(trainingDao, never()).create(training);
    }

    @Test
    void shouldThrowEntityNotFoundWhenTrainerDoesNotExist() {
        Training training = buildTrainingWithoutId(DEFAULT_TRAINEE_ID, DEFAULT_TRAINER_ID);
        Trainee existingTrainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao).findById(DEFAULT_TRAINER_ID);
        verify(trainingDao, never()).create(training);
    }

    @Test
    void shouldReturnTrainingWhenGettingExistingTraining() {
        Training expected = buildTrainingWithId(DEFAULT_TRAINING_ID);

        when(trainingDao.findById(DEFAULT_TRAINING_ID)).thenReturn(Optional.of(expected));

        Training actual = service.getTraining(DEFAULT_TRAINING_ID);

        assertEquals(expected, actual);
        verify(trainingDao).findById(DEFAULT_TRAINING_ID);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingUnknownTraining() {
        when(trainingDao.findById(DEFAULT_TRAINING_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraining(DEFAULT_TRAINING_ID));

        assertEquals("Training not found with id: " + DEFAULT_TRAINING_ID, exception.getMessage());
        verify(trainingDao).findById(DEFAULT_TRAINING_ID);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainingId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTraining(null));

        assertEquals("Training ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainingDao, traineeDao, trainerDao);
    }

    @Test
    void shouldReturnAllTrainings() {
        Training training1 = buildTrainingWithId(DEFAULT_TRAINING_ID);
        Training training2 = buildTrainingWithId(SECONDARY_TRAINING_ID);
        List<Training> expected = List.of(training1, training2);

        when(trainingDao.findAll()).thenReturn(expected);

        List<Training> actual = service.getAllTrainings();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyTrainings() {
        List<Training> expected = List.of();

        when(trainingDao.findAll()).thenReturn(expected);

        List<Training> actual = service.getAllTrainings();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

}
