package com.gym.crm.dao.impl;

import com.gym.crm.dao.TrainingDao;
import com.gym.crm.model.Training;
import com.gym.crm.storage.impl.InMemoryStorage;
import com.gym.crm.storage.impl.TraineeNamespaceStorage;
import com.gym.crm.storage.impl.TrainerNamespaceStorage;
import com.gym.crm.storage.impl.TrainingNamespaceStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_NAME;
import static com.gym.crm.factory.TrainingTestFactory.NON_EXISTENT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.buildTraining;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithoutId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryTrainingDaoTest {

    private TrainingDao dao;

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

        InMemoryTrainingDao implementation = new InMemoryTrainingDao();
        implementation.setStorage(storage);
        dao = implementation;
    }

    @Test
    void shouldCreateTrainingWithGeneratedId() {
        Training training = buildTraining();

        Training actual = dao.create(training);

        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(actual.getId() > 0, "ID must be positive");
        assertEquals(DEFAULT_TRAINEE_ID, actual.getTraineeId());
        assertEquals(DEFAULT_TRAINER_ID, actual.getTrainerId());
        assertEquals(DEFAULT_TRAINING_NAME, actual.getTrainingName());
    }

    @Test
    void shouldGenerateSequentialIds() {
        Training training1 = buildTrainingWithoutId(10L, 20L);
        Training training2 = buildTrainingWithoutId(11L, 21L);

        Training actual1 = dao.create(training1);
        Training actual2 = dao.create(training2);

        assertTrue(actual2.getId() > actual1.getId());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingNullTraining() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.create(null));

        assertEquals("Training cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindTrainingByIdAfterCreation() {
        Training training = buildTraining();
        Training expected = dao.create(training);

        Optional<Training> actual = dao.findById(expected.getId());

        assertTrue(actual.isPresent());
        assertEquals(expected.getId(), actual.get().getId());
        assertEquals(DEFAULT_TRAINING_NAME, actual.get().getTrainingName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenTrainingNotFound() {
        Optional<Training> actual = dao.findById(NON_EXISTENT_TRAINING_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.findById(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    void shouldFindAllTrainings() {
        Training training1 = buildTrainingWithoutId(10L, 20L);
        Training training2 = buildTrainingWithoutId(11L, 21L);
        dao.create(training1);
        dao.create(training2);

        List<Training> actual = dao.findAll();

        assertNotNull(actual);
        int expectedSize = 2;
        assertEquals(expectedSize, actual.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainings() {
        List<Training> actual = dao.findAll();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void shouldUpdateExistingTraining() {
        Training training = buildTraining();
        Training created = dao.create(training);
        String expectedTrainingName = "Evening Workout";
        Training updated = buildTraining(created.getId(), expectedTrainingName);

        Training actual = dao.update(updated);

        assertEquals(created.getId(), actual.getId());
        assertEquals(expectedTrainingName, actual.getTrainingName());

        Optional<Training> found = dao.findById(created.getId());
        assertTrue(found.isPresent());
        assertEquals(expectedTrainingName, found.get().getTrainingName());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingWithoutId() {
        Training training = buildTraining();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> dao.update(training));

        assertEquals("Cannot update entity without ID", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTraining() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.update(null));

        assertEquals("Training cannot be null", exception.getMessage());
    }

    @Test
    void shouldDeleteExistingTraining() {
        Training training = buildTraining();
        Training expected = dao.create(training);

        boolean actual = dao.delete(expected.getId());

        assertTrue(actual);
        assertFalse(dao.findById(expected.getId()).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTraining() {
        boolean actual = dao.delete(NON_EXISTENT_TRAINING_ID);

        assertFalse(actual);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingWithNullId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dao.delete(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

}
