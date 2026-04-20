package com.gym.crm.storage.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.storage.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.trainee;
import static com.gym.crm.factory.TraineeTestFactory.traineeWithUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryNamespaceStorageTest {

    private InMemoryNamespaceStorage<Trainee> storage;

    /**
     * Test-specific implementation of InMemoryNamespaceStorage.
     * Used to test the abstract class logic without coupling to production implementations.
     */
    private static class TestNamespaceStorage extends InMemoryNamespaceStorage<Trainee> {
        public TestNamespaceStorage() {
            super(Namespace.TRAINEE);
        }

        @Override
        protected Trainee setId(Trainee entity, Long id) {
            return entity.toBuilder()
                    .userId(id)
                    .build();
        }
    }

    @BeforeEach
    public void setUp() {
        storage = new TestNamespaceStorage();
    }

    @Test
    public void shouldSaveEntitySuccessfully() {
        Trainee trainee = trainee();

        Trainee saved = storage.save(trainee);
        Optional<Trainee> result = storage.findById(saved.getUserId());

        assertNotNull(saved.getUserId());
        assertTrue(saved.getUserId() > 0, "ID must be positive");
        assertTrue(result.isPresent());
        assertEquals(saved.getUsername(), result.get().getUsername());
    }

    @Test
    public void shouldFindByIdWhenEntityExists() {
        Trainee trainee = trainee();
        Trainee saved = storage.save(trainee);

        Optional<Trainee> result = storage.findById(saved.getUserId());

        assertTrue(result.isPresent());
        assertEquals(DEFAULT_USERNAME, result.get().getUsername());
        assertEquals(DEFAULT_FIRST_NAME, result.get().getFirstName());
    }

    @Test
    public void shouldReturnEmptyOptionalWhenEntityNotFound() {
        Optional<Trainee> result = storage.findById(NON_EXISTENT_TRAINEE_ID);

        assertFalse(result.isPresent());
    }

    @Test
    public void shouldFindAllEntitiesWhenStorageHasData() {
        storage.save(traineeWithUsername("john.doe"));
        storage.save(traineeWithUsername("jane.smith"));

        List<Trainee> result = storage.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void shouldReturnEmptyListWhenStorageIsEmpty() {
        List<Trainee> result = storage.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldDeleteEntityAndReturnTrueWhenExists() {
        Trainee trainee = trainee();
        Trainee saved = storage.save(trainee);

        boolean result = storage.delete(saved.getUserId());

        assertTrue(result);
        assertFalse(storage.findById(saved.getUserId()).isPresent());
    }

    @Test
    public void shouldReturnFalseWhenDeletingNonExistentEntity() {
        boolean result = storage.delete(999L);

        assertFalse(result);
    }

    @Test
    public void shouldClearStorageAndResetIdCounter() {
        storage.save(traineeWithUsername("john.doe"));
        storage.save(traineeWithUsername("jane.smith"));

        storage.clear();

        List<Trainee> result = storage.findAll();
        Trainee savedAfterClear = storage.save(trainee());
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertEquals(1L, savedAfterClear.getUserId());
    }

    @Test
    public void shouldReturnCorrectNamespace() {
        Namespace<Trainee> namespace = storage.getNamespace();

        assertNotNull(namespace);
        assertEquals(Namespace.TRAINEE, namespace);
    }

}
