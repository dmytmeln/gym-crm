package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainerDao;
import com.gym.crm.service.ProfileCredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.traineeWithUsername;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileCredentialServiceImplTest {

    private static final String PASSWORD_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";

    private static final int PASSWORD_LENGTH = 10;

    private static final String FIRST_NAME = "John";

    private static final String LAST_NAME = "Doe";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    private ProfileCredentialService service;

    @BeforeEach
    public void setUp() {
        ProfileCredentialServiceImpl implementation = new ProfileCredentialServiceImpl();
        implementation.setTraineeDao(traineeDao);
        implementation.setTrainerDao(trainerDao);
        service = implementation;
    }

    @Test
    public void shouldGenerateUsernameWithoutSerialWhenNoExistingUsers() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe", generatedUsername);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    public void shouldAppendNextSerialWhenBaseUsernameIsTaken() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("John.Doe")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe1", generatedUsername);
    }

    @Test
    public void shouldUseHighestSerialPlusOneWhenMatchingUsernamesExist() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("john.doe"),
                traineeWithUsername("John.Doe1"),
                traineeWithUsername("john.Doe2")
        ));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe3", generatedUsername);
    }

    @Test
    public void shouldUseHighestSerialPlusOneWhenSerialGapsExist() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("John.Doe"),
                traineeWithUsername("john.doe1"),
                traineeWithUsername("John.doe2"),
                traineeWithUsername("john.Doe5")
        ));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe6", generatedUsername);
    }

    @Test
    public void shouldHandleUsersFromBothTraineeAndTrainerDaos() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("john.doe"),
                traineeWithUsername("john.doe1")
        ));
        when(trainerDao.findAll()).thenReturn(List.of(trainerWithUsername("john.doe3")));

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe4", generatedUsername);
        verify(traineeDao).findAll();
        verify(trainerDao).findAll();
    }

    @Test
    public void shouldIgnoreUsersWithDifferentFirstName() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("jane.doe")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    public void shouldIgnoreUsersWithDifferentLastName() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("john.smith")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    public void shouldIgnoreUsersWithDifferentFullName() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(List.of(trainerWithUsername("jane.smith")));

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    public void shouldMatchExistingUsernameCaseInsensitively() {
        when(traineeDao.findAll()).thenReturn(List.of(traineeWithUsername("john.doe")));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe1", generatedUsername);
    }

    @Test
    public void shouldIgnoreUsernamesWithNonNumericSuffix() {
        when(traineeDao.findAll()).thenReturn(List.of(
                traineeWithUsername("john.doeX"),
                traineeWithUsername("john.doe_1")
        ));
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        String generatedUsername = service.generateUsername(FIRST_NAME, LAST_NAME);

        assertEquals("John.Doe", generatedUsername);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenFirstNameIsNull() {
        assertThrows(NullPointerException.class, () -> service.generateUsername(null, LAST_NAME));

        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenLastNameIsNull() {
        assertThrows(NullPointerException.class, () -> service.generateUsername(FIRST_NAME, null));

        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenBothNamesAreNull() {
        assertThrows(NullPointerException.class, () -> service.generateUsername(null, null));

        verifyNoInteractions(traineeDao, trainerDao);
    }

    @Test
    public void shouldGeneratePasswordWithCorrectLength() {
        String generatedPassword = service.generatePassword();

        assertEquals(PASSWORD_LENGTH, generatedPassword.length());
    }

    @Test
    public void shouldGeneratePasswordWithValidCharactersOnly() {
        String generatedPassword = service.generatePassword();

        for (char currentChar : generatedPassword.toCharArray()) {
            assertTrue(
                    PASSWORD_ALPHABET.indexOf(currentChar) >= 0,
                    "Password contains invalid character: " + currentChar
            );
        }
    }

}
