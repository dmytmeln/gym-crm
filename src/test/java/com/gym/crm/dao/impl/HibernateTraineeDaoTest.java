package com.gym.crm.dao.impl;

import com.gym.crm.dao.TraineeEntityDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringJUnitConfig(HibernateTraineeDao.class)
class HibernateTraineeDaoTest extends AbstractDaoTest {

    @Autowired
    private TraineeEntityDao dao;

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
        assertThat(isTraineeExists(actual.getId()))
                .as("Trainee must be created")
                .isTrue();
        assertThat(isUserExists(actual.getUser().getUsername()))
                .as("User must be created")
                .isTrue();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingNullTrainee() {
        assertThatThrownBy(() -> dao.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
    }

    @Test
    void shouldThrowNullPointerExceptionWhenCreatingTraineeWithNullUser() {
        Trainee invalidTraineeWithoutUser = Trainee.builder().build();

        assertThatThrownBy(() -> dao.create(invalidTraineeWithoutUser))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("User cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTraineeWithId() {
        Trainee invalidTraineeWithId = Trainee.builder()
                .id(1L)
                .user(User.builder().build())
                .build();

        assertThatThrownBy(() -> dao.create(invalidTraineeWithId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Trainee ID must be null for creation.");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenCreatingTraineeWithUserId() {
        User invalidUserWithId = User.builder().id(1L).build();
        Trainee validTraineeWithInvalidUser = Trainee.builder().user(invalidUserWithId).build();

        assertThatThrownBy(() -> dao.create(validTraineeWithInvalidUser))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User ID must be null for creation.");
    }

    @Test
    void shouldFindByIdWhenExists() {
        Long existingId = 1L;

        Optional<Trainee> actual = dao.findById(existingId);

        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(existingId);
        assertThat(actual.get().getAddress()).isEqualTo("NYC");
        assertThat(actual.get().getUser().getUsername()).isEqualTo("liam.miller");
    }

    @Test
    void shouldReturnEmptyWhenNotFoundById() {
        long nonExistingId = 99999L;

        Optional<Trainee> actual = dao.findById(nonExistingId);

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
        String existingUsername = "liam.miller";

        Optional<Trainee> actual = dao.findByUsername(existingUsername);

        assertThat(actual).isPresent();
        assertThat(actual.get().getUser().getUsername()).isEqualTo(existingUsername);
        assertThat(actual.get().getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnEmptyWhenNotFoundByUsername() {
        String nonExistingUsername = "non.existent";

        Optional<Trainee> actual = dao.findByUsername(nonExistingUsername);

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

        assertThat(actual).hasSize(2);
    }

    @Test
    void shouldUpdateTrainee() {
        Long existingId = 1L;
        String newAddress = "Updated Address";
        Trainee existingTrainee = dao.findById(existingId).orElseThrow(() -> new AssertionError("Trainee not found"));
        Trainee updatedTrainee = existingTrainee.toBuilder()
                .address(newAddress)
                .build();

        Trainee actual = dao.update(updatedTrainee);

        assertThat(actual.getAddress()).isEqualTo(newAddress);
        assertThat(getTraineeAddress(existingId))
                .as("Trainee address should be updated in database")
                .isEqualTo(newAddress);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenUpdatingNullTrainee() {
        assertThatThrownBy(() -> dao.update(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee cannot be null");
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenUpdatingTraineeWithoutId() {
        Trainee invalidTraineeWithoutId = Trainee.builder().build();

        assertThatThrownBy(() -> dao.update(invalidTraineeWithoutId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot update entity without ID");
    }

    @Test
    void shouldDeleteByUsernameWhenExists() {
        String existingUsername = "liam.miller";
        Long existingId = 1L;

        boolean actual = dao.deleteByUsername(existingUsername);

        assertThat(actual).isTrue();
        assertThat(isTraineeExists(existingId))
                .as("Trainee should be deleted from database")
                .isFalse();
        assertThat(isUserExists(existingUsername))
                .as("User should be deleted from database")
                .isFalse();
        assertThat(getTraineeTrainingsCount(existingId))
                .as("Trainee's trainings should be deleted from database")
                .isZero();
    }

    @Test
    void shouldReturnFalseWhenDeletingByNonExistentUsername() {
        String nonExistingUsername = "non.existent";

        boolean actual = dao.deleteByUsername(nonExistingUsername);

        assertThat(actual).isFalse();
    }

    @Test
    void shouldThrowNullPointerExceptionWhenDeletingByNullUsername() {
        assertThatThrownBy(() -> dao.deleteByUsername(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Trainee username cannot be null");
    }

    private long getTraineeTrainingsCount(Long traineeId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM training WHERE trainee_id = ?")
                .params(traineeId)
                .query(Long.class)
                .single();
    }

    private boolean isTraineeExists(Long traineeId) {
        return jdbcClient.sql("SELECT COUNT(*) FROM trainee WHERE id = ?")
                .params(traineeId)
                .query(Long.class)
                .single() > 0;
    }

    private boolean isUserExists(String username) {
        return jdbcClient.sql("SELECT COUNT(*) FROM user WHERE username = ?")
                .params(username)
                .query(Long.class)
                .single() > 0;
    }

    private String getTraineeAddress(Long traineeId) {
        return jdbcClient.sql("SELECT address FROM trainee WHERE id = ?")
                .params(traineeId)
                .query(String.class)
                .single();
    }

}
