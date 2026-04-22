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
import static com.gym.crm.factory.TrainerTestFactory.buildTrainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTrainerDaoTest {

    private TrainerDao dao;

    @BeforeEach
    void setUp() {
        TraineeNamespaceStorage traineeNamespaceStorage = new TraineeNamespaceStorage();
        TrainerNamespaceStorage trainerNamespaceStorage = new TrainerNamespaceStorage();
        TrainingNamespaceStorage trainingNamespaceStorage = new TrainingNamespaceStorage();

        InMemoryStorage storage = new InMemoryStorage();
        storage.setNamespaceStorages(List.of(
                traineeNamespaceStorage,
                trainerNamespaceStorage,
                trainingNamespaceStorage));

        InMemoryTrainerDao implementation = new InMemoryTrainerDao();
        implementation.setStorage(storage);
        dao = implementation;
    }

    @Test
    void shouldCreateTrainerWithGeneratedId() {
        Trainer trainer = buildTrainer();

        Trainer actual = dao.create(trainer);

        assertNotNull(actual);
        assertNotNull(actual.getUserId());
        assertTrue(actual.getUserId() > 0, "ID must be positive");
        assertEquals(DEFAULT_USERNAME, actual.getUsername());
        assertEquals(DEFAULT_FIRST_NAME, actual.getFirstName());
        assertEquals(DEFAULT_LAST_NAME, actual.getLastName());
    }

    @Test
    void shouldGenerateSequentialIds() {
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();

        Trainer actual1 = dao.create(trainer1);
        Trainer actual2 = dao.create(trainer2);

        assertTrue(actual2.getUserId() > actual1.getUserId());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.create(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindTrainerByIdAfterCreation() {
        Trainer trainer = buildTrainer();
        Trainer expected = dao.create(trainer);

        Optional<Trainer> actual = dao.findById(expected.getUserId());

        assertTrue(actual.isPresent());
        assertEquals(expected.getUserId(), actual.get().getUserId());
        assertEquals(DEFAULT_USERNAME, actual.get().getUsername());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainerNotFound() {
        Optional<Trainer> actual = dao.findById(NON_EXISTENT_TRAINER_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.findById(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindAllTrainers() {
        Trainer trainer1 = buildTrainer();
        Trainer trainer2 = buildTrainer();
        dao.create(trainer1);
        dao.create(trainer2);

        List<Trainer> actual = dao.findAll();

        assertNotNull(actual);
        int expectedSize = 2;
        assertEquals(expectedSize, actual.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainers() {
        List<Trainer> actual = dao.findAll();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldUpdateExistingTrainer() {
        Trainer trainer = buildTrainer();
        Trainer created = dao.create(trainer);
        String expectedFirstName = "Johnny";
        String expectedSpecialization = "Yoga";
        Trainer updated = buildTrainer(created.getUserId(), DEFAULT_USERNAME, expectedFirstName, DEFAULT_LAST_NAME, expectedSpecialization);

        Trainer actual = dao.update(updated);

        assertEquals(created.getUserId(), actual.getUserId());
        assertEquals(expectedFirstName, actual.getFirstName());
        assertEquals(expectedSpecialization, actual.getSpecialization().getTrainingTypeName());
        assertEquals(DEFAULT_USERNAME, actual.getUsername());

        Optional<Trainer> found = dao.findById(created.getUserId());
        assertTrue(found.isPresent());
        assertEquals(expectedFirstName, found.get().getFirstName());
        assertEquals(DEFAULT_USERNAME, found.get().getUsername());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Trainer trainer = buildTrainer();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> dao.update(trainer));

        assertEquals("Cannot update entity without ID", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.update(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
    }

    @Test
    void shouldDeleteExistingTrainer() {
        Trainer trainer = buildTrainer();
        Trainer created = dao.create(trainer);

        boolean actual = dao.delete(created.getUserId());

        assertTrue(actual);
        assertFalse(dao.findById(created.getUserId()).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainer() {
        boolean actual = dao.delete(NON_EXISTENT_TRAINER_ID);

        assertFalse(actual);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.delete(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

}
