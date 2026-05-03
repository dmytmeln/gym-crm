package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTraineeConfigForExisting;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTraineeConfigForSaved;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainerConfigForDirectFields;
import static com.gym.crm.test.helper.EntityRecursiveComparisonConfigs.getTrainingConfigForDirectFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(HibernateTraineeDao.class)
class HibernateTraineeDaoTest extends AbstractDaoTest<HibernateTraineeDao> {

    private static final long EXISTING_ID = 1L;
    private static final long EXISTING_USER_ID = 1L;
    private static final long NON_EXISTING_ID = 99999L;
    private static final String EXISTING_USERNAME = "liam.miller";
    private static final String NON_EXISTING_USERNAME = "non.existent";
    private static final int TRAINEES_COUNT = 2;
    private static final int USERS_COUNT = 6;

    @Test
    void shouldSaveTraineeAndUser() {
        User validUser = User.builder()
                .firstName("Robert")
                .lastName("Downey")
                .username("robert.downey")
                .password("ironman123")
                .isActive(true)
                .build();
        Trainee validTrainee = Trainee.builder()
                .user(validUser)
                .address("Malibu, CA")
                .dateOfBirth(LocalDate.of(1965, 4, 4))
                .build();

        Trainee actual = dao.save(validTrainee);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getId()).isNotNull();
        assertThat(actual.getTrainings()).isEmpty();
        assertThat(actual.getTrainers()).isEmpty();
        assertThat(actual)
                .usingRecursiveComparison(getTraineeConfigForSaved())
                .isEqualTo(validTrainee);
        Trainee existingTrainee = testDbClient.findTrainee(actual.getId());
        assertThat(existingTrainee)
                .usingRecursiveComparison(getTraineeConfigForSaved())
                .isEqualTo(actual);
        assertThat(testDbClient.countTraineeTrainings(actual.getId()))
                .as("New trainee should have no trainings in database")
                .isZero();
        assertThat(testDbClient.countTraineeTrainers(actual.getId()))
                .as("New trainee should have no trainers in database")
                .isZero();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenSavingNullTrainee() {
        assertThatThrownBy(() -> dao.save(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenSavingTraineeWithNullUser() {
        Trainee invalidTraineeWithoutUser = Trainee.builder().build();

        assertThatThrownBy(() -> dao.save(invalidTraineeWithoutUser))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User cannot be null");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSavingTraineeWithId() {
        User validUser = User.builder().build();
        Trainee invalidTraineeWithId = Trainee.builder()
                .id(EXISTING_ID)
                .user(validUser)
                .build();

        assertThatThrownBy(() -> dao.save(invalidTraineeWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be null for creation.");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenSavingTraineeWithUserId() {
        User invalidUserWithId = User.builder().id(EXISTING_USER_ID).build();
        Trainee validTraineeWithInvalidUser = Trainee.builder().user(invalidUserWithId).build();

        assertThatThrownBy(() -> dao.save(validTraineeWithInvalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID must be null for creation.");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldFindByIdWhenExists() {
        Trainee expected = testDbClient.findTrainee(EXISTING_ID);

        Optional<Trainee> actual = dao.findById(EXISTING_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTraineeConfigForExisting())
                .isEqualTo(expected);
        assertThat(testDbClient.findTraineeTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(actual.get().getTrainings());
        assertThat(testDbClient.findTraineeTrainers(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainerConfigForDirectFields())
                .isEqualTo(actual.get().getTrainers());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        Optional<Trainee> actual = dao.findById(NON_EXISTING_ID);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullId() {
        assertThatThrownBy(() -> dao.findById(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee ID cannot be null");
    }

    @Test
    void shouldFindByUsernameWhenExists() {
        Trainee expected = testDbClient.findTrainee(EXISTING_ID);

        Optional<Trainee> actual = dao.findByUsername(EXISTING_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison(getTraineeConfigForExisting())
                .isEqualTo(expected);
        assertThat(testDbClient.findTraineeTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(actual.get().getTrainings());
        assertThat(testDbClient.findTraineeTrainers(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainerConfigForDirectFields())
                .isEqualTo(actual.get().getTrainers());
    }

    @Test
    void shouldReturnEmptyWhenNotFoundByUsername() {
        Optional<Trainee> actual = dao.findByUsername(NON_EXISTING_USERNAME);

        assertThat(actual).isEmpty();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFindingByNullUsername() {
        assertThatThrownBy(() -> dao.findByUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee username cannot be null");
    }

    @Test
    void shouldFindAllTrainees() {
        List<Trainee> expected = List.of(
                Trainee.builder().id(EXISTING_ID).build(),
                Trainee.builder().id(2L).build());

        List<Trainee> actual = dao.findAll();

        assertThat(actual)
                .hasSize(TRAINEES_COUNT)
                .containsAll(expected);
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee existingTrainee = testDbClient.findTrainee(EXISTING_ID);
        User updatedUser = existingTrainee.getUser().toBuilder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .isActive(false)
                .build();
        Trainee updatedTrainee = existingTrainee.toBuilder()
                .address("Updated Address")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .user(updatedUser)
                .trainers(new HashSet<>(testDbClient.findTraineeTrainers(EXISTING_ID)))
                .trainings(testDbClient.findTraineeTrainings(EXISTING_ID))
                .build();

        Trainee actual = dao.update(updatedTrainee);

        assertThat(actual.getTrainings())
                .as("Returned trainee should contain associated trainings")
                .hasSize(1);
        assertThat(actual.getTrainers())
                .as("Returned trainee should contain associated trainers")
                .hasSize(1);
        assertThat(actual)
                .usingRecursiveComparison(getTraineeConfigForExisting())
                .isEqualTo(updatedTrainee);
        Trainee existingTraineeAfterUpdate = testDbClient.findTrainee(EXISTING_ID);
        assertThat(existingTraineeAfterUpdate)
                .usingRecursiveComparison(getTraineeConfigForExisting())
                .isEqualTo(updatedTrainee);
        assertThat(testDbClient.findTraineeTrainings(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainingConfigForDirectFields())
                .isEqualTo(updatedTrainee.getTrainings())
                .isEqualTo(actual.getTrainings());
        assertThat(testDbClient.findTraineeTrainers(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison(getTrainerConfigForDirectFields())
                .isEqualTo(updatedTrainee.getTrainers())
                .isEqualTo(actual.getTrainers());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainee() {
        assertThatThrownBy(() -> dao.update(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingTraineeWithoutId() {
        Trainee invalidTraineeWithoutId = Trainee.builder().build();

        assertThatThrownBy(() -> dao.update(invalidTraineeWithoutId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update entity without ID");
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(testDbClient.countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldDeleteByUsernameWhenExists() {
        Long existingTraineeTrainerId = 1L;

        boolean result = dao.deleteByUsername(EXISTING_USERNAME);

        assertThat(result).isTrue();
        assertThat(testDbClient.traineeExists(EXISTING_ID))
                .as("Trainee should be deleted from database")
                .isFalse();
        assertThat(testDbClient.userExists(EXISTING_USERNAME))
                .as("User should be deleted from database")
                .isFalse();
        assertThat(testDbClient.countTraineeTrainings(EXISTING_ID))
                .as("Trainee's trainings should be deleted from database")
                .isZero();
        assertThat(testDbClient.countTraineeTrainers(EXISTING_ID))
                .as("Trainee's trainers link should be deleted from database")
                .isZero();
        assertThat(testDbClient.trainerExists(existingTraineeTrainerId))
                .as("Trainee's trainer should remain in database")
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeletingByNonExistentUsername() {
        String nonExistingUsername = "non.existent";

        boolean result = dao.deleteByUsername(nonExistingUsername);

        assertThat(result).isFalse();
        assertThat(testDbClient.countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(2);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingByNullUsername() {
        assertThatThrownBy(() -> dao.deleteByUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee username cannot be null");
    }

}
