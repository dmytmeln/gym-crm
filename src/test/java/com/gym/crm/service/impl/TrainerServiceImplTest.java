package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.ProfileCredentialService;
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
import static com.gym.crm.factory.TrainerTestFactory.defaultTrainer;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithId;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithoutCredentials;
import static com.gym.crm.factory.TrainingTypeTestFactory.strength;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @Mock
    private TrainerDao dao;

    @Mock
    private ProfileCredentialService credentialService;

    private TrainerService service;

    @BeforeEach
    public void setUp() {
        TrainerServiceImpl implementation = new TrainerServiceImpl();
        implementation.setTrainerDao(dao);
        implementation.setCredentialService(credentialService);
        service = implementation;
    }

    @Test
    public void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer trainerWithoutCredentials = trainerWithoutCredentials();
        Trainer createdTrainer = trainerWithId(DEFAULT_TRAINER_ID);
        when(credentialService.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(credentialService.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(dao.create(any(Trainer.class))).thenReturn(createdTrainer);

        Trainer result = service.createTrainer(trainerWithoutCredentials);

        assertCredentialsWereGeneratedForTrainer();
        assertEquals(createdTrainer, result);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTrainer() {
        assertThrows(NullPointerException.class, () -> service.createTrainer(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainer() {
        Trainer existingTrainer = trainerWithId(DEFAULT_TRAINER_ID);
        Trainer updateRequest = defaultTrainer()
                .userId(DEFAULT_TRAINER_ID)
                .firstName("Michael")
                .lastName("Coach")
                .specialization(strength())
                .build();
        Trainer updatedTrainer = defaultTrainer()
                .userId(DEFAULT_TRAINER_ID)
                .firstName("Michael")
                .lastName("Coach")
                .specialization(strength())
                .build();
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(updatedTrainer);

        Trainer result = service.updateTrainer(updateRequest);

        verify(dao).findById(DEFAULT_TRAINER_ID);
        assertMergedTrainerHasCorrectFields("Michael", "Coach", strength());
        assertEquals(updatedTrainer, result);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainer() {
        Trainer updateRequest = trainerWithId(DEFAULT_TRAINER_ID)
                .toBuilder()
                .firstName("Michael")
                .lastName("Coach")
                .build();
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateRequest));
        verify(dao).findById(DEFAULT_TRAINER_ID);
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingNullTrainer() {
        assertThrows(NullPointerException.class, () -> service.updateTrainer(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldReturnTrainerWhenGettingExistingTrainer() {
        Trainer existingTrainer = trainerWithId(DEFAULT_TRAINER_ID);
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.of(existingTrainer));

        Trainer result = service.getTrainer(DEFAULT_TRAINER_ID);

        assertEquals(existingTrainer, result);
        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    public void shouldThrowEntityNotFoundWhenGettingUnknownTrainer() {
        when(dao.findById(DEFAULT_TRAINER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getTrainer(DEFAULT_TRAINER_ID));

        verify(dao).findById(DEFAULT_TRAINER_ID);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTrainerId() {
        assertThrows(NullPointerException.class, () -> service.getTrainer(null));

        verifyNoInteractions(dao, credentialService);
    }

    @Test
    public void shouldReturnAllTrainers() {
        Trainer trainer1 = trainerWithId(DEFAULT_TRAINER_ID);
        Trainer trainer2 = trainerWithId(SECONDARY_TRAINER_ID);
        List<Trainer> trainers = List.of(trainer1, trainer2);
        when(dao.findAll()).thenReturn(trainers);

        List<Trainer> result = service.getAllTrainers();

        assertNotNull(result);
        assertEquals(trainers, result);
    }

    @Test
    public void shouldReturnEmptyTrainers() {
        when(dao.findAll()).thenReturn(List.of());

        List<Trainer> result = service.getAllTrainers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    private void assertCredentialsWereGeneratedForTrainer() {
        verify(credentialService).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(credentialService).generatePassword();
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).create(trainerCaptor.capture());
        Trainer trainerToCreate = trainerCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, trainerToCreate.getUsername());
        assertEquals(DEFAULT_PASSWORD, trainerToCreate.getPassword());
        assertEquals(DEFAULT_FIRST_NAME, trainerToCreate.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, trainerToCreate.getLastName());
    }

    private void assertMergedTrainerHasCorrectFields(String firstName, String lastName, TrainingType specialization) {
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        Trainer trainerToUpdate = trainerCaptor.getValue();
        assertEquals(DEFAULT_TRAINER_ID, trainerToUpdate.getUserId());
        assertEquals(DEFAULT_USERNAME, trainerToUpdate.getUsername());
        assertEquals(firstName, trainerToUpdate.getFirstName());
        assertEquals(lastName, trainerToUpdate.getLastName());
        assertEquals(specialization, trainerToUpdate.getSpecialization());
    }

}
