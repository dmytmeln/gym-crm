package com.gym.crm.dao.impl;

import com.gym.crm.dao.helper.TraineeTrainingCriteriaBuilder;
import com.gym.crm.dao.helper.TrainerTrainingCriteriaBuilder;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTraineeConfigForDirectFields;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainerConfigForDirectFields;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingConfigForExisting;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingConfigForSaved;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingTypeConfigForDirectFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig({TrainingDaoImpl.class, TrainerTrainingCriteriaBuilder.class, TraineeTrainingCriteriaBuilder.class})
class TrainingDaoImplTest extends AbstractDaoTest<TrainingDaoImpl> {

    private static final long EXISTING_TRAINING_ID = 1L;
    private static final long EXISTING_TRAINEE_ID = 1L;
    private static final long EXISTING_TRAINER_ID = 1L;
    private static final long EXISTING_TRAINING_TYPE_ID = 1L;
    private static final int TRAININGS_COUNT = 2;

    @Test
    void shouldSaveTrainingWithExistingReferences() {
        Trainee existingTrainee = testDbClient.findTraineeSimple(EXISTING_TRAINEE_ID);
        Trainer existingTrainer = testDbClient.findTrainerSimple(EXISTING_TRAINER_ID);
        TrainingType existingTrainingType = testDbClient.findTrainingType(EXISTING_TRAINING_TYPE_ID);
        Training validTraining = Training.builder()
                .trainee(existingTrainee)
                .trainer(existingTrainer)
                .trainingName("Advanced Cardio Session")
                .trainingType(existingTrainingType)
                .trainingDate(LocalDate.of(2025, 2, 1))
                .trainingDuration(45)
                .build();

        Training actual = dao.save(validTraining);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual)
                .usingRecursiveComparison(getTrainingConfigForSaved())
                .isEqualTo(validTraining);
        Training existingTraining = testDbClient.findTraining(actual.getId());
        assertThat(existingTraining)
                .usingRecursiveComparison(getTrainingConfigForSaved())
                .isEqualTo(actual);
        assertThat(existingTraining.getTrainee())
                .usingRecursiveComparison(getTraineeConfigForDirectFields())
                .isEqualTo(existingTrainee);
        assertThat(existingTraining.getTrainer())
                .usingRecursiveComparison(getTrainerConfigForDirectFields())
                .isEqualTo(existingTrainer);
        assertThat(existingTraining.getTrainingType())
                .usingRecursiveComparison(getTrainingTypeConfigForDirectFields())
                .isEqualTo(existingTrainingType);
        assertThat(testDbClient.countTrainings())
                .as("WithAssociationsTrainings count should increase by 1")
                .isEqualTo(TRAININGS_COUNT + 1);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenSavingNullTraining() {
        assertThatThrownBy(() -> dao.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Training cannot be null");

        assertThat(testDbClient.countTrainings())
                .as("Trainings count should remain unchanged")
                .isEqualTo(TRAININGS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSavingTrainingWithId() {
        Trainee existingTrainee = Trainee.builder().id(EXISTING_TRAINEE_ID).build();
        Trainer existingTrainer = Trainer.builder().id(EXISTING_TRAINER_ID).build();
        TrainingType existingTrainingType = TrainingType.builder().id(EXISTING_TRAINING_TYPE_ID).build();
        Training invalidTrainingWithId = Training.builder()
                .id(EXISTING_TRAINING_ID)
                .trainee(existingTrainee)
                .trainer(existingTrainer)
                .trainingName("Test Training")
                .trainingType(existingTrainingType)
                .trainingDate(LocalDate.of(2025, 2, 1))
                .trainingDuration(45)
                .build();

        assertThatThrownBy(() -> dao.save(invalidTrainingWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Training ID must be null for creation.");

        assertThat(testDbClient.countTrainings())
                .as("Trainings count should remain unchanged")
                .isEqualTo(TRAININGS_COUNT);
    }

    @Test
    void shouldFindTrainingsByTrainerCriteriaWithAllFilters() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("marcus.stone")
                .fromDate(LocalDate.of(2025, 1, 1))
                .toDate(LocalDate.of(2025, 1, 31))
                .traineeName("Liam Miller")
                .build();
        Training expected = testDbClient.findTraining(1L);

        List<Training> actual = dao.findTrainerTrainingsByCriteria(filter);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0))
                .usingRecursiveComparison(getTrainingConfigForExisting())
                .isEqualTo(expected);
    }

    @Test
    void shouldFindTrainingsByTrainerCriteriaWithPartialFilters() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("sarah.adams")
                .build();
        Training expected = testDbClient.findTraining(2L);

        List<Training> actual = dao.findTrainerTrainingsByCriteria(filter);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0))
                .usingRecursiveComparison(getTrainingConfigForExisting())
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainingsMatchTrainerCriteria() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("marcus.stone")
                .traineeName("Sophia Wilson")
                .build();

        List<Training> actual = dao.findTrainerTrainingsByCriteria(filter);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldFindTraineeTrainingsByCriteriaWithAllFilters() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("liam.miller")
                .fromDate(LocalDate.of(2025, 1, 1))
                .toDate(LocalDate.of(2025, 1, 31))
                .trainerName("Marcus Stone")
                .trainingTypeName("CARDIO")
                .build();
        Training expected = testDbClient.findTraining(1L);

        List<Training> actual = dao.findTraineeTrainingsByCriteria(filter);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0))
                .usingRecursiveComparison(getTrainingConfigForExisting())
                .isEqualTo(expected);
    }

    @Test
    void shouldFindTraineeTrainingsByCriteriaWithPartialFilters() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("sophia.wilson")
                .trainingTypeName("STRENGTH")
                .build();
        Training expected = testDbClient.findTraining(2L);

        List<Training> actual = dao.findTraineeTrainingsByCriteria(filter);

        assertThat(actual).hasSize(1);
        assertThat(actual.get(0))
                .usingRecursiveComparison(getTrainingConfigForExisting())
                .isEqualTo(expected);
    }

    @Test
    void shouldReturnEmptyListWhenNoTrainingsMatchTraineeCriteria() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username("liam.miller")
                .trainerName("Sarah Adams")
                .build();

        List<Training> actual = dao.findTraineeTrainingsByCriteria(filter);

        assertThat(actual).isEmpty();
    }

}
