package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.generator.ProfileCredentialGenerator;
import com.gym.crm.generator.impl.ProfileCredentialGeneratorImpl;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithUsername;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCredentialGeneratorImplTest {

    private static final String PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    private static final int PASSWORD_LENGTH = 10;

    private static final String FIRST_NAME = "Liam";

    private static final String LAST_NAME = "Miller";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    private ProfileCredentialGenerator generator;

    @BeforeEach
    void setUp() {
        ProfileCredentialGeneratorImpl implementation = new ProfileCredentialGeneratorImpl();
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainerDao(trainerDao);
        generator = implementation;
    }

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
        when(traineeDao.findAll()).thenReturn(List.of(buildTraineeWithUsername("Liam.Miller")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller1", actual);
    }

    @Test
    void shouldUseHighestSerialPlusOneWhenMatchingUsernamesExist() {
        when(traineeDao.findAll()).thenReturn(List.of(
                buildTraineeWithUsername("liam.miller"),
                buildTraineeWithUsername("Liam.Miller1"),
                buildTraineeWithUsername("liam.Miller2")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller3", actual);
    }

    @Test
    void shouldUseHighestSerialPlusOneWhenSerialGapsExist() {
        when(traineeDao.findAll()).thenReturn(List.of(
                buildTraineeWithUsername("Liam.Miller"),
                buildTraineeWithUsername("liam.Miller2"),
                buildTraineeWithUsername("Liam.miller2"),
                buildTraineeWithUsername("liam.Miller5")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller6", actual);
    }

    @Test
    void shouldHandleUsersFromBothTraineeAndTrainerDaos() {
        when(traineeDao.findAll()).thenReturn(List.of(
                buildTraineeWithUsername("liam.miller"),
                buildTraineeWithUsername("liam.miller1")));
        when(trainerDao.findAll()).thenReturn(List.of(buildTrainerWithUsername("liam.miller3")));

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller4", actual);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    void shouldIgnoreUsersWithDifferentFirstName() {
        when(traineeDao.findAll()).thenReturn(List.of(buildTraineeWithUsername("sophia.miller")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldIgnoreUsersWithDifferentLastName() {
        when(traineeDao.findAll()).thenReturn(List.of(buildTraineeWithUsername("liam.wilson")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldIgnoreUsersWithDifferentFullName() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(List.of(buildTrainerWithUsername("sophia.wilson")));

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller", actual);
    }

    @Test
    void shouldMatchExistingUsernameCaseInsensitively() {
        when(traineeDao.findAll()).thenReturn(List.of(buildTraineeWithUsername("liam.miller")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String actual = generator.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("Liam.Miller1", actual);
    }

    @Test
    void shouldIgnoreUsernamesWithNonNumericSuffix() {
        when(traineeDao.findAll()).thenReturn(List.of(
                buildTraineeWithUsername("liam.millerX"),
                buildTraineeWithUsername("liam.miller_1")));
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
