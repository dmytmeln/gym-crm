package com.gym.crm.service.common;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCredentialGeneratorTest {

    private static final String PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    private static final int PASSWORD_LENGTH = 10;

    private static final String FIRST_NAME = "Liam";

    private static final String LAST_NAME = "Miller";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private ProfileCredentialGenerator generator;

    @Test
    void shouldGenerateUsernameWithoutSerialWhenNoExistingUsers() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void shouldAppendNextSerialWhenBaseUsernameIsTaken() {
        Trainee trainee = Trainee.builder()
                .user(User.builder().username("Liam.Miller").build())
                .build();
        when(traineeDao.findAll()).thenReturn(List.of(trainee));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller1", actual);
    }

    @Test
    void shouldUseHighestSerialPlusOneWhenMatchingUsernamesExist() {
        Trainee trainee1 = Trainee.builder()
                .user(User.builder().username("liam.miller").build())
                .build();
        Trainee trainee2 = Trainee.builder()
                .user(User.builder().username("Liam.Miller1").build())
                .build();
        Trainee trainee3 = Trainee.builder()
                .user(User.builder().username("liam.Miller2").build())
                .build();
        when(traineeDao.findAll()).thenReturn(List.of(trainee1, trainee2, trainee3));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller3", actual);
    }

    @Test
    void shouldUseHighestSerialPlusOneWhenSerialGapsExist() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("Liam.Miller").build()).build(),
                Trainee.builder().user(User.builder().username("liam.Miller2").build()).build(),
                Trainee.builder().user(User.builder().username("liam.Miller5").build()).build()));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller6", actual);
    }

    @Test
    void shouldHandleUsersFromBothTraineeAndTrainerDaos() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("liam.miller").build()).build(),
                Trainee.builder().user(User.builder().username("liam.miller1").build()).build()));
        when(trainerDao.findAll()).thenReturn(List.of(
                Trainer.builder().user(User.builder().username("liam.miller3").build()).build()));

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller4", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void shouldIgnoreUsersWithDifferentFirstName() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("sophia.miller").build()).build()));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldIgnoreUsersWithDifferentLastName() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("liam.wilson").build()).build()));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldIgnoreUsersWithDifferentFullName() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(List.of(
                Trainer.builder().user(User.builder().username("sophia.wilson").build()).build()));

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldMatchExistingUsernameCaseInsensitively() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("liam.miller").build()).build()));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller1", actual);
    }

    @Test
    void shouldIgnoreUsernamesWithNonNumericSuffix() {
        when(traineeDao.findAll()).thenReturn(List.of(
                Trainee.builder().user(User.builder().username("liam.millerX").build()).build(),
                Trainee.builder().user(User.builder().username("liam.miller_1").build()).build()));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenFirstNameIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> generator.generateUsername(null, LAST_NAME));

        assertEquals("First Name cannot be null", exception.getMessage());
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenLastNameIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> generator.generateUsername(FIRST_NAME, null));

        assertEquals("Last Name cannot be null", exception.getMessage());
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    void shouldThrowNullPointerExceptionWhenBothNamesAreNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> generator.generateUsername(null, null));

        assertEquals("First Name cannot be null", exception.getMessage());
        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    void shouldGeneratePasswordWithCorrectLength() {
        String actual = generator.generatePassword();

        assertEquals(PASSWORD_LENGTH, actual.length());
    }

    @Test
    void shouldGeneratePasswordWithValidCharactersOnly() {
        String actual = generator.generatePassword();

        assertTrue(
                StringUtils.containsOnly(actual, PASSWORD_ALPHABET),
                "Password contains invalid characters");
    }

}
