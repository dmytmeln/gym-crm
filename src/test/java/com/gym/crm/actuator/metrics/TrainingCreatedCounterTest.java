package com.gym.crm.actuator.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TrainingCreatedCounterTest {

    private MeterRegistry registry;
    private TrainingCreatedCounter counter;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        counter = new TrainingCreatedCounter(registry);
    }

    @Test
    void shouldIncrementTrainingCreatedType() {
        counter.increment("cardio");

        double count = registry.counter("gym_crm_trainings_created_total", "training_type", "cardio").count();
        assertEquals(1.0, count);
    }

    @Test
    void shouldIncrementMultipleTimesForSameTrainingType() {
        counter.increment("yoga");
        counter.increment("yoga");

        double count = registry.counter("gym_crm_trainings_created_total", "training_type", "yoga").count();
        assertEquals(2.0, count);
    }

    @Test
    void shouldCountIndependentTrainingTypes() {
        counter.increment("cardio");
        counter.increment("yoga");
        counter.increment("yoga");

        double cardioCount = registry.counter("gym_crm_trainings_created_total", "training_type", "cardio").count();
        double yogaCount = registry.counter("gym_crm_trainings_created_total", "training_type", "yoga").count();
        assertEquals(1.0, cardioCount);
        assertEquals(2.0, yogaCount);
    }

}
