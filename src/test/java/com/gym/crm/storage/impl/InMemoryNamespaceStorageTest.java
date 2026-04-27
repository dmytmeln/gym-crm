package com.gym.crm.storage.impl;

import com.gym.crm.model.Trainee;
import com.gym.crm.storage.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.NON_EXISTENT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTrainee;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryNamespaceStorageTest {

    private InMemoryNamespaceStorage<Trainee> storage;

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
    void setUp() {
        storage = new TestNamespaceStorage();
    }

    @Test
    void shouldSaveEntitySuccessfully() {
        Trainee trainee = buildTrainee();

        Trainee result = storage.save(trainee);
        Optional<Trainee> found = storage.findById(result.getUserId());

        assertNotNull(result.getUserId());
        assertTrue(result.getUserId() > 0, "ID must be positive");
        assertTrue(found.isPresent());
        assertEquals(result.getUsername(), found.get().getUsername());
    }

    @Test
    void shouldFindByIdWhenEntityExists() {
        Trainee trainee = buildTrainee();
        Trainee prepared = storage.save(trainee);

        Optional<Trainee> result = storage.findById(prepared.getUserId());

        assertTrue(result.isPresent());
        assertEquals(DEFAULT_USERNAME, result.get().getUsername());
        assertEquals(DEFAULT_FIRST_NAME, result.get().getFirstName());
    }

    @Test
    void shouldReturnEmptyOptionalWhenEntityNotFound() {
        Optional<Trainee> result = storage.findById(NON_EXISTENT_TRAINEE_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindAllEntitiesWhenStorageHasData() {
        storage.save(buildTraineeWithUsername("liam.miller"));
        storage.save(buildTraineeWithUsername("sophia.wilson"));

        List<Trainee> result = storage.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenStorageIsEmpty() {
        List<Trainee> result = storage.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteEntityAndReturnTrueWhenExists() {
        Trainee trainee = buildTrainee();
        Trainee prepared = storage.save(trainee);

        boolean result = storage.delete(prepared.getUserId());

        assertTrue(result);
        assertFalse(storage.findById(prepared.getUserId()).isPresent());
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentEntity() {
        boolean result = storage.delete(999L);

        assertFalse(result);
    }

    @Test
    void shouldReturnCorrectNamespace() {
        Namespace<Trainee> result = storage.getNamespace();

        assertNotNull(result);
        assertEquals(Namespace.TRAINEE, result);
    }

}
