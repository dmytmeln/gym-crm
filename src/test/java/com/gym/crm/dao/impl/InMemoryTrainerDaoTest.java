package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.storage.impl.InMemoryStorage;
import com.gym.crm.storage.impl.TraineeNamespaceStorage;
import com.gym.crm.storage.impl.TrainerNamespaceStorage;
import com.gym.crm.storage.impl.TrainingNamespaceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.NON_EXISTENT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.trainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryTrainerDaoTest {

    private TrainerDao dao;

    @BeforeEach
    public void setUp() {
        TraineeNamespaceStorage traineeNamespaceStorage = new TraineeNamespaceStorage();
        TrainerNamespaceStorage trainerNamespaceStorage = new TrainerNamespaceStorage();
        TrainingNamespaceStorage trainingNamespaceStorage = new TrainingNamespaceStorage();

        InMemoryStorage storage = new InMemoryStorage();
        storage.setNamespaceStorages(List.of(
                traineeNamespaceStorage,
                trainerNamespaceStorage,
                trainingNamespaceStorage
        ));

        InMemoryTrainerDao implementation = new InMemoryTrainerDao();
        implementation.setStorage(storage);
        dao = implementation;

        trainerNamespaceStorage.clear();
    }

    @Test
    public void shouldCreateTrainerWithGeneratedId() {
        Trainer trainer = trainer();

        Trainer result = dao.create(trainer);

        assertNotNull(result);
        assertNotNull(result.getUserId());
        assertTrue(result.getUserId() > 0, "ID must be positive");
        assertEquals(DEFAULT_USERNAME, result.getUsername());
        assertEquals(DEFAULT_FIRST_NAME, result.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, result.getLastName());
    }

    @Test
    public void shouldGenerateSequentialIds() {
        Trainer trainer1 = trainer();
        Trainer trainer2 = trainer();

        Trainer result1 = dao.create(trainer1);
        Trainer result2 = dao.create(trainer2);

        assertTrue(result2.getUserId() > result1.getUserId());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenCreatingNullTrainer() {
        assertThrows(NullPointerException.class, () -> dao.create(null));
    }

    @Test
    public void shouldFindTrainerByIdAfterCreation() {
        Trainer trainer = trainer();
        Trainer created = dao.create(trainer);

        Optional<Trainer> result = dao.findById(created.getUserId());

        assertTrue(result.isPresent());
        assertEquals(created.getUserId(), result.get().getUserId());
        assertEquals(DEFAULT_USERNAME, result.get().getUsername());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenTrainerNotFound() {
        Optional<Trainer> result = dao.findById(NON_EXISTENT_TRAINER_ID);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThrows(NullPointerException.class, () -> dao.findById(null));
    }

    @Test
    public void shouldFindAllTrainers() {
        Trainer trainer1 = trainer();
        Trainer trainer2 = trainer();
        dao.create(trainer1);
        dao.create(trainer2);

        List<Trainer> result = dao.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void shouldReturnEmptyListWhenNoTrainers() {
        List<Trainer> result = dao.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldUpdateExistingTrainer() {
        Trainer trainer = trainer();
        Trainer created = dao.create(trainer);
        Trainer updated = trainer(created.getUserId(), DEFAULT_USERNAME, "Johnny", DEFAULT_LAST_NAME, "Yoga");

        Trainer result = dao.update(updated);

        assertEquals(created.getUserId(), result.getUserId());
        assertEquals("Johnny", result.getFirstName());
        assertEquals("Yoga", result.getSpecialization().getTrainingTypeName());
        assertEquals(DEFAULT_USERNAME, result.getUsername());

        Optional<Trainer> found = dao.findById(created.getUserId());
        assertTrue(found.isPresent());
        assertEquals("Johnny", found.get().getFirstName());
        assertEquals(DEFAULT_USERNAME, found.get().getUsername());
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Trainer trainer = trainer();

        assertThrows(IllegalArgumentException.class, () -> dao.update(trainer));
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenUpdatingNullTrainer() {
        assertThrows(NullPointerException.class, () -> dao.update(null));
    }

    @Test
    public void shouldDeleteExistingTrainer() {
        Trainer trainer = trainer();
        Trainer created = dao.create(trainer);

        boolean result = dao.delete(created.getUserId());

        assertTrue(result);
        assertFalse(dao.findById(created.getUserId()).isPresent());
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentTrainer() {
        boolean result = dao.delete(NON_EXISTENT_TRAINER_ID);

        assertFalse(result);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        assertThrows(NullPointerException.class, () -> dao.delete(null));
    }

}
