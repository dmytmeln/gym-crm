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
import static com.gym.crm.factory.TraineeTestFactory.traineeWithId;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithId;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.SECONDARY_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.training;
import static com.gym.crm.factory.TrainingTestFactory.trainingWithId;
import static com.gym.crm.factory.TrainingTestFactory.trainingWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingDao dao;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    private TrainingService service;

    @BeforeEach
    public void setUp() {
        TrainingServiceImpl implementation = new TrainingServiceImpl();
        implementation.setTrainingDao(dao);
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainerDao(trainerDao);
        service = implementation;
    }

    @Test
    public void shouldCreateTrainingWhenParticipantsExist() {
        Training training = training();
        Training trainingWithId = trainingWithId(DEFAULT_TRAINING_ID);
        Trainee existingTrainee = traineeWithId(DEFAULT_TRAINEE_ID);
        Trainer existingTrainer = trainerWithId(DEFAULT_TRAINER_ID);
        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(dao.create(training)).thenReturn(trainingWithId);

        Training result = service.createTraining(training);

        assertEquals(trainingWithId, result);
        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao).findById(DEFAULT_TRAINER_ID);
        verify(dao).create(training);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTraining() {
        assertThrows(NullPointerException.class, () -> service.createTraining(null));

        verifyNoInteractions(dao, traineeDao, trainerDao);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenTraineeDoesNotExist() {
        Training training = trainingWithoutId(DEFAULT_TRAINEE_ID, DEFAULT_TRAINER_ID);
        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao, never()).findById(DEFAULT_TRAINER_ID);
        verify(dao, never()).create(training);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenTrainerDoesNotExist() {
        Training training = trainingWithoutId(DEFAULT_TRAINEE_ID, DEFAULT_TRAINER_ID);
        Trainee existingTrainee = traineeWithId(DEFAULT_TRAINEE_ID);
        when(traineeDao.findById(DEFAULT_TRAINEE_ID)).thenReturn(Optional.of(existingTrainee));
        when(trainerDao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.createTraining(training));

        verify(traineeDao).findById(DEFAULT_TRAINEE_ID);
        verify(trainerDao).findById(DEFAULT_TRAINER_ID);
        verify(dao, never()).create(training);
    }

    @Test
    public void shouldReturnTrainingWhenGettingExistingTraining() {
        Training trainingWithId = trainingWithId(DEFAULT_TRAINING_ID);
        when(dao.findById(DEFAULT_TRAINING_ID)).thenReturn(Optional.of(trainingWithId));

        Training result = service.getTraining(DEFAULT_TRAINING_ID);

        assertEquals(trainingWithId, result);
        verify(dao).findById(DEFAULT_TRAINING_ID);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenGettingUnknownTraining() {
        when(dao.findById(DEFAULT_TRAINING_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getTraining(DEFAULT_TRAINING_ID));

        verify(dao).findById(DEFAULT_TRAINING_ID);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTrainingId() {
        assertThrows(NullPointerException.class, () -> service.getTraining(null));

        verifyNoInteractions(dao, traineeDao, trainerDao);
    }

    @Test
    public void shouldReturnAllTrainings() {
        Training training1 = trainingWithId(DEFAULT_TRAINING_ID);
        Training training2 = trainingWithId(SECONDARY_TRAINING_ID);
        List<Training> trainings = List.of(training1, training2);
        when(dao.findAll()).thenReturn(trainings);

        List<Training> result = service.getAllTrainings();

        assertNotNull(result);
        assertEquals(trainings, result);
    }

    @Test
    public void shouldReturnEmptyTrainings() {
        when(dao.findAll()).thenReturn(List.of());

        List<Training> result = service.getAllTrainings();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

}
