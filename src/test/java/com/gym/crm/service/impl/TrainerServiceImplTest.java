package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.helper.ProfileCredentialGenerator;
import com.gym.crm.service.TrainerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.SECONDARY_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithoutCredentials;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultTrainerBuilder;
import static com.gym.crm.factory.TrainingTypeTestFactory.buildStrength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao dao;

    @Mock
    private ProfileCredentialGenerator generator;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        TrainerServiceImpl implementation = new TrainerServiceImpl();
        implementation.setTrainerDao(dao);
        implementation.setCredentialGenerator(generator);
        service = implementation;
    }

    @Test
    void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer trainerWithoutCredentials = buildTrainerWithoutCredentials();
        Trainer expected = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.create(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(trainerWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).create(trainerCaptor.capture());
        Trainer trainerWithCredentials = trainerCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, trainerWithCredentials.getUsername());
        assertEquals(DEFAULT_PASSWORD, trainerWithCredentials.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, trainerWithCredentials.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, trainerWithCredentials.getLastName());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainer() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer updateRequest = getDefaultTrainerBuilder()
                .userId(DEFAULT_TRAINER_ID)
                .firstName("Elena")
                .lastName("Rodriguez")
                .specialization(buildStrength())
                .build();
        Trainer expected = getDefaultTrainerBuilder()
                .userId(DEFAULT_TRAINER_ID)
                .firstName("Elena")
                .lastName("Rodriguez")
                .specialization(buildStrength())
                .build();

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.updateTrainer(updateRequest);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        TrainingType specialization = buildStrength();
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        Trainer mergedTrainer = trainerCaptor.getValue();
        assertEquals(DEFAULT_TRAINER_ID, mergedTrainer.getUserId());
        assertEquals(DEFAULT_USERNAME, mergedTrainer.getUsername());
        assertEquals("Elena", mergedTrainer.getFirstName());
        assertEquals("Rodriguez", mergedTrainer.getLastName());
        assertEquals(specialization, mergedTrainer.getSpecialization());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainer() {
        Trainer updateRequest = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateRequest));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTrainerWhenGettingExistingTrainer() {
        Trainer expected = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(expected));

        Trainer actual = service.getTrainer(DEFAULT_TRAINER_ID);

        assertEquals(expected, actual);
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingUnknownTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainer(DEFAULT_TRAINER_ID));

        assertEquals("Trainer not found with id: " + DEFAULT_TRAINER_ID, exception.getMessage());
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnAllTrainers() {
        Trainer trainer1 = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer trainer2 = buildTrainerWithId(SECONDARY_TRAINER_ID);
        List<Trainer> expected = List.of(trainer1, trainer2);

        when(dao.findAll()).thenReturn(expected);

        List<Trainer> actual = service.getAllTrainers();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnEmptyTrainers() {
        List<Trainer> expected = List.of();

        when(dao.findAll()).thenReturn(expected);

        List<Trainer> actual = service.getAllTrainers();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

}
