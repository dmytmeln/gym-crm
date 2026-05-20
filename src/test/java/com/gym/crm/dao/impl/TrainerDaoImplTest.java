package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.TrainingType;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTraineeConfigForDirectFields;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainerConfigForExisting;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainerConfigForSaved;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingConfigForDirectFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(TrainerDaoImpl.class)
class TrainerDaoImplTest extends AbstractDaoTest<TrainerDaoImpl> {

    private static final long EXISTING_ID = 1L;
    private static final long EXISTING_USER_ID = 4L;
    private static final long EXISTING_SPECIALIZATION_ID = 1L;
    private static final long NON_EXISTING_ID = 99999L;
    private static final String EXISTING_USERNAME = "marcus.stone";
    private static final String NON_EXISTING_USERNAME = "non.existent";
    private static final int TRAINERS_COUNT = 3;
    private static final int USERS_COUNT = 6;

    @Test
    void shouldSaveTrainerAndUser() {
        TrainingType existingSpecialization = testDbClient.findTrainingType(EXISTING_SPECIALIZATION_ID);
        User validUser = User.builder()
                .firstName("Tony")
                .lastName("Stark")
                .username("tony.stark")
                .password("ironman456")
                .isActive(true)
                .build();
        Trainer validTrainer = Trainer.builder()
                .user(validUser)
                .specialization(existingSpecialization)
                .build();

        Trainer actual = dao.save(validTrainer);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getId()).isNotNull();
        assertThat(actual.getTrainings()).isEmpty();
        assertThat(actual.getTrainees()).isEmpty();
        assertThat(actual)
                .usingRecursiveComparison(getTrainerConfigForSaved())
                .isEqualTo(validTrainer);
        Trainer existingTrainer = testDbClient.findTrainer(actual.getId());
        assertThat(existingTrainer)
                .usingRecursiveComparison(getTrainerConfigForSaved())
                .isEqualTo(actual);
        assertThat(testDbClient.countTrainerTrainings(actual.getId()))
                .as("New trainer should have no trainings in database")
                .isZero();
        assertThat(testDbClient.countTrainerTrainees(actual.getId()))
                .as("New trainer should have no trainees in database")
                .isZero();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenSavingNullTrainer() {
        assertThatThrownBy(() -> dao.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainer cannot be null");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenSavingTrainerWithNullUser() {
        Trainer invalidTrainerWithoutUser = Trainer.builder().build();

        assertThatThrownBy(() -> dao.save(invalidTrainerWithoutUser))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User cannot be null");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSavingTrainerWithId() {
        User validUser = User.builder().build();
        Trainer invalidTrainerWithId = Trainer.builder()
                .id(EXISTING_ID)
                .user(validUser)
                .build();

        assertThatThrownBy(() -> dao.save(invalidTrainerWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainer ID must be null for creation.");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSavingTrainerWithUserId() {
        User invalidUserWithId = User.builder().id(EXISTING_USER_ID).build();
        Trainer validTrainerWithInvalidUser = Trainer.builder().user(invalidUserWithId).build();

        assertThatThrownBy(() -> dao.save(validTrainerWithInvalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID must be null for creation.");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldFindByIdWhenExists() {
        Trainer expected = testDbClient.findTrainer(EXISTING_ID);
        List<Training> expectedTrainings = List.of(buildExistingTraining());
        List<Trainee> expectedTrainees = List.of(buildExistingTrainee());

        Optional<Trainer> actual = dao.findById(EXISTING_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTrainerConfigForExisting())
                .isEqualTo(expected);
        assertThat(testDbClient.findTrainerTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(expectedTrainings);
        assertThat(testDbClient.findTrainerTrainees(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTraineeConfigForDirectFields())
                .isEqualTo(expectedTrainees);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        Optional<Trainer> actual = dao.findById(NON_EXISTING_ID);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThatThrownBy(() -> dao.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainer ID cannot be null");
    }

    @Test
    void shouldFindByUsernameWhenExists() {
        Trainer expected = testDbClient.findTrainer(EXISTING_ID);
        List<Training> expectedTrainings = List.of(buildExistingTraining());
        List<Trainee> expectedTrainees = List.of(buildExistingTrainee());

        Optional<Trainer> actual = dao.findByUsername(EXISTING_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTrainerConfigForExisting())
                .isEqualTo(expected);
        assertThat(testDbClient.findTrainerTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(expectedTrainings);
        assertThat(testDbClient.findTrainerTrainees(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTraineeConfigForDirectFields())
                .isEqualTo(expectedTrainees);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundByUsername() {
        Optional<Trainer> actual = dao.findByUsername(NON_EXISTING_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullUsername() {
        assertThatThrownBy(() -> dao.findByUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainer username cannot be null");
    }

    @Test
    void shouldFindAllTrainers() {
        List<Trainer> expected = List.of(
                Trainer.builder().id(EXISTING_ID).build(),
                Trainer.builder().id(2L).build(),
                Trainer.builder().id(3L).build());

        List<Trainer> actual = dao.findAll();

        assertThat(actual)
                .hasSize(TRAINERS_COUNT)
                .containsAll(expected);
    }

    @Test
    void shouldUpdateTrainer() {
        List<Training> expectedTrainings = List.of(buildExistingTraining());
        List<Trainee> expectedTrainees = List.of(buildExistingTrainee());
        Trainer existingTrainer = testDbClient.findTrainer(EXISTING_ID);
        User updatedUser = existingTrainer.getUser().toBuilder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .isActive(false)
                .build();
        TrainingType newSpecialization = testDbClient.findTrainingType(2L);
        Trainer updatedTrainer = existingTrainer.toBuilder()
                .user(updatedUser)
                .specialization(newSpecialization)
                .build();

        Trainer actual = dao.update(updatedTrainer);

        assertThat(actual)
                .usingRecursiveComparison(getTrainerConfigForExisting())
                .isEqualTo(updatedTrainer);
        Trainer existingTrainerAfterUpdate = testDbClient.findTrainer(EXISTING_ID);
        assertThat(existingTrainerAfterUpdate)
                .usingRecursiveComparison(getTrainerConfigForExisting())
                .isEqualTo(updatedTrainer);
        assertThat(testDbClient.findTrainerTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(expectedTrainings);
        assertThat(testDbClient.findTrainerTrainees(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTraineeConfigForDirectFields())
                .isEqualTo(expectedTrainees);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainer() {
        assertThatThrownBy(() -> dao.update(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainer cannot be null");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingTrainerWithoutId() {
        Trainer invalidTrainerWithoutId = Trainer.builder().build();

        assertThatThrownBy(() -> dao.update(invalidTrainerWithoutId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update entity without ID");
        assertThat(testDbClient.countTrainers())
                .as("Trainers count should remain unchanged")
                .isEqualTo(TRAINERS_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    private Training buildExistingTraining() {
        return Training.builder()
                .id(1L)
                .trainingName("Morning HIIT")
                .trainingDate(LocalDate.of(2025, 1, 15))
                .trainingDuration(60)
                .build();
    }

    private Trainee buildExistingTrainee() {
        return Trainee.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("NYC")
                .build();
    }

}
