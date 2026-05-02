package com.gym.crm.dao.impl;

import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

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
    void shouldCreateTraineeAndUser() {
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

        Trainee actual = dao.create(validTrainee);

        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getUser().getId()).isNotNull();
        assertThat(actual.getTrainings()).isEmpty();
        assertThat(actual.getTrainers()).isEmpty();
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("id", "user.id", "trainers", "trainings")
                .isEqualTo(validTrainee);
        Trainee existingTrainee = findTraineeInDb(actual.getId());
        assertThat(existingTrainee)
                .usingRecursiveComparison()
                .ignoringFields("id", "user.id", "trainers", "trainings")
                .isEqualTo(actual);
        assertThat(countTraineeTrainings(actual.getId()))
                .as("New trainee should have no trainings in database")
                .isZero();
        assertThat(countTraineeTrainers(actual.getId()))
                .as("New trainee should have no trainers in database")
                .isZero();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingNullTrainee() {
        assertThatThrownBy(() -> dao.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingTraineeWithNullUser() {
        Trainee invalidTraineeWithoutUser = Trainee.builder().build();

        assertThatThrownBy(() -> dao.create(invalidTraineeWithoutUser))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User cannot be null");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTraineeWithId() {
        User validUser = User.builder().build();
        Trainee invalidTraineeWithId = Trainee.builder()
                .id(EXISTING_ID)
                .user(validUser)
                .build();

        assertThatThrownBy(() -> dao.create(invalidTraineeWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be null for creation.");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTraineeWithUserId() {
        User invalidUserWithId = User.builder().id(EXISTING_USER_ID).build();
        Trainee validTraineeWithInvalidUser = Trainee.builder().user(invalidUserWithId).build();

        assertThatThrownBy(() -> dao.create(validTraineeWithInvalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID must be null for creation.");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldFindByIdWhenExists() {
        Trainee expected = buildSeededTrainee();

        Optional<Trainee> actual = dao.findById(EXISTING_ID);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("trainers", "trainings")
                .isEqualTo(expected);
        assertThat(findTraineeTrainingsInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("trainee", "trainingType", "trainer")
                .isEqualTo(actual.get().getTrainings());
        assertThat(findTraineeTrainersInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("user", "specialization", "trainings", "trainees")
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
        Trainee expected = buildSeededTrainee();

        Optional<Trainee> actual = dao.findByUsername(EXISTING_USERNAME);

        assertThat(actual).isPresent();
        assertThat(actual.get())
                .usingRecursiveComparison()
                .ignoringFields("trainers", "trainings")
                .isEqualTo(expected);
        assertThat(findTraineeTrainingsInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("trainee", "trainingType", "trainer")
                .isEqualTo(actual.get().getTrainings());
        assertThat(findTraineeTrainersInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("user", "specialization", "trainings", "trainees")
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
        List<Trainee> actual = dao.findAll();

        assertThat(actual).hasSize(TRAINEES_COUNT);
        assertThat(actual)
                .extracting(Trainee::getId, Trainee::getAddress, Trainee::getDateOfBirth, t -> t.getUser().getId(),
                        t -> t.getTrainers().size(), t -> t.getTrainings().size())
                .containsExactlyInAnyOrder(
                        tuple(1L, "NYC", LocalDate.of(1990, 5, 15), 1L, 1, 1),
                        tuple(2L, null, LocalDate.of(1992, 8, 20), 2L, 0, 1));
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee existingTrainee = dao.findById(EXISTING_ID).orElseThrow(() -> new AssertionError("Trainee not found"));
        User updatedUser = existingTrainee.getUser().toBuilder()
                .firstName("UpdatedFirstName")
                .lastName("UpdatedLastName")
                .isActive(false)
                .build();
        Trainee updatedTrainee = existingTrainee.toBuilder()
                .address("Updated Address")
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .user(updatedUser)
                .build();

        Trainee actual = dao.update(updatedTrainee);

        assertThat(actual.getTrainings())
                .as("Returned trainee should contain associated trainings")
                .hasSize(1);
        assertThat(actual.getTrainers())
                .as("Returned trainee should contain associated trainers")
                .hasSize(1);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("trainings.trainee", "trainings.trainingType", "trainings.trainer",
                        "trainers.trainees", "trainers.specialization", "trainers.trainings")
                .isEqualTo(updatedTrainee);
        Trainee existingTraineeAfterUpdate = findTraineeInDb(EXISTING_ID);
        assertThat(existingTraineeAfterUpdate)
                .usingRecursiveComparison()
                .ignoringFields("trainings", "trainers")
                .isEqualTo(updatedTrainee);
        assertThat(findTraineeTrainingsInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("trainee", "trainingType", "trainer")
                .isEqualTo(actual.getTrainings());
        assertThat(findTraineeTrainersInDb(EXISTING_ID))
                .hasSize(1)
                .usingRecursiveComparison()
                .ignoringFields("user", "specialization", "trainings", "trainees")
                .isEqualTo(actual.getTrainers());
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainee() {
        assertThatThrownBy(() -> dao.update(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingTraineeWithoutId() {
        Trainee invalidTraineeWithoutId = Trainee.builder().build();

        assertThatThrownBy(() -> dao.update(invalidTraineeWithoutId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update entity without ID");
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(TRAINEES_COUNT);
        assertThat(countUsers())
                .as("Users count should remain unchanged")
                .isEqualTo(USERS_COUNT);
    }

    @Test
    void shouldDeleteByUsernameWhenExists() {
        Long existingTraineeTrainerId = 1L;

        boolean result = dao.deleteByUsername(EXISTING_USERNAME);

        assertThat(result).isTrue();
        assertThat(isTraineeInDb(EXISTING_ID))
                .as("Trainee should be deleted from database")
                .isFalse();
        assertThat(isUserInDb(EXISTING_USERNAME))
                .as("User should be deleted from database")
                .isFalse();
        assertThat(countTraineeTrainings(EXISTING_ID))
                .as("Trainee's trainings should be deleted from database")
                .isZero();
        assertThat(countTraineeTrainers(EXISTING_ID))
                .as("Trainee's trainers link should be deleted from database")
                .isZero();
        assertThat(isTrainerInDb(existingTraineeTrainerId))
                .as("Trainee's trainer should remain in database")
                .isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeletingByNonExistentUsername() {
        String nonExistingUsername = "non.existent";

        boolean result = dao.deleteByUsername(nonExistingUsername);

        assertThat(result).isFalse();
        assertThat(countTrainees())
                .as("Trainees count should remain unchanged")
                .isEqualTo(2);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingByNullUsername() {
        assertThatThrownBy(() -> dao.deleteByUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee username cannot be null");
    }

    private Trainee buildSeededTrainee() {
        User user = User.builder()
                .id(EXISTING_USER_ID)
                .firstName("Liam")
                .lastName("Miller")
                .username(EXISTING_USERNAME)
                .password("pass123")
                .isActive(true)
                .build();
        return Trainee.builder()
                .id(EXISTING_ID)
                .address("NYC")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .user(user)
                .build();
    }

    private Trainee findTraineeInDb(Long id) {
        Trainee trainee = jdbcClient.sql("SELECT * FROM trainee WHERE id = ?")
                .params(id)
                .query((rs, rowNum) -> Trainee.builder()
                        .id(rs.getLong("id"))
                        .address(rs.getString("address"))
                        .dateOfBirth(rs.getDate("date_of_birth").toLocalDate())
                        .user(User.builder().id(rs.getLong("user_id")).build())
                        .build())
                .single();
        User user = findUserInDb(trainee.getUser().getId());
        return trainee.toBuilder().user(user).build();
    }

    private User findUserInDb(Long id) {
        return jdbcClient.sql("SELECT * FROM user WHERE id = ?")
                .params(id)
                .query(User.class)
                .single();
    }

    private long countTrainees() {
        return jdbcClient.sql("SELECT COUNT(*) FROM trainee")
                .query(Long.class)
                .single();
    }

    private long countUsers() {
        return jdbcClient.sql("SELECT COUNT(*) FROM user")
                .query(Long.class)
                .single();
    }

    private long countTraineeTrainings(Long traineeId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM training WHERE trainee_id = ?")
                .params(traineeId)
                .query(Long.class)
                .single();
    }

    private long countTraineeTrainers(Long traineeId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM trainee_trainer WHERE trainee_id = ?")
                .params(traineeId)
                .query(Long.class)
                .single();
    }

    private boolean isTraineeInDb(Long traineeId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM trainee WHERE id = ?")
                .params(traineeId)
                .query(Long.class)
                .single() > 0;
    }

    private boolean isUserInDb(String username) {
        return jdbcClient.sql("SELECT COUNT(*) FROM user WHERE username = ?")
                .params(username)
                .query(Long.class)
                .single() > 0;
    }

    private boolean isTrainerInDb(Long trainerId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM trainer WHERE id = ?")
                .params(trainerId)
                .query(Long.class)
                .single() > 0;
    }

    private List<Training> findTraineeTrainingsInDb(Long traineeId) {
        return jdbcClient.sql("SELECT t.* FROM training t WHERE t.trainee_id = ?")
                .params(traineeId)
                .query(Training.class)
                .list();
    }

    private List<Trainer> findTraineeTrainersInDb(Long traineeId) {
        return jdbcClient.sql("""
                        SELECT tr.* FROM trainer tr
                        INNER JOIN trainee_trainer tt ON tt.trainer_id = tr.id
                        WHERE tt.trainee_id = ?
                        """)
                .params(traineeId)
                .query(Trainer.class)
                .list();
    }

}
