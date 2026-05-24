package com.gym.crm.service.impl;

import com.gym.crm.dao.TraineeDao;
import com.gym.crm.dao.TrainingDao;
import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ConflictException;
import com.gym.crm.security.AuthenticationException;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithUsername;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithoutCredentials;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithoutUser;
import static com.gym.crm.factory.TraineeTestFactory.getDefaultTraineeBuilder;
import static com.gym.crm.factory.TraineeTestFactory.getDefaultUserBuilder;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao dao;

    @Mock
    private TrainingDao trainingDao;

    @Mock
    private ProfileCredentialGenerator generator;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TraineeService service;

    @BeforeEach
    void setUp() {
        TraineeServiceImpl implementation = new TraineeServiceImpl();
        implementation.setTraineeDao(dao);
        implementation.setTrainingDao(trainingDao);
        implementation.setCredentialGenerator(generator);
        implementation.setPasswordEncoder(passwordEncoder);
        service = implementation;
    }

    @Test
    void shouldCreateTraineeWithGeneratedCredentials() {
        Trainee traineeWithoutCredentials = buildTraineeWithoutCredentials();
        Trainee expected = buildTraineeWithId();
        String encodedPassword = "encoded-" + DEFAULT_PASSWORD;

        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn(encodedPassword);
        when(dao.save(any(Trainee.class))).thenReturn(expected);

        Trainee actual = service.createTrainee(traineeWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        verify(passwordEncoder).encode(DEFAULT_PASSWORD);
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).save(traineeCaptor.capture());
        Trainee traineeWithCredentials = traineeCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, traineeWithCredentials.getUser().getUsername());
        assertEquals(encodedPassword, traineeWithCredentials.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, traineeWithCredentials.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, traineeWithCredentials.getUser().getLastName());
        assertEquals(expected, actual);
        assertEquals(DEFAULT_PASSWORD, actual.getUser().getPassword());
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainee() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainee(null));

        assertEquals("Trainee cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingTraineeWithNullUser() {
        Trainee traineeWithoutUser = buildTraineeWithoutUser();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainee(traineeWithoutUser));

        assertEquals("User cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldReturnTraineeWhenGettingByExistingUsername() {
        Trainee expected = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(expected));

        Trainee actual = service.getTraineeByUsername(DEFAULT_USERNAME);

        assertEquals(expected, actual);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowEntityNotFoundWhenGettingByNonExistentUsername() {
        when(dao.findByUsername("unknown")).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTraineeByUsername("unknown"));

        assertEquals("Trainee not found with username: unknown", exception.getMessage());
        verify(dao).findByUsername("unknown");
    }

    @Test
    void shouldThrowNullPointerWhenGettingByNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.getTraineeByUsername(null));

        assertEquals("Trainee username cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldReturnAvailableTrainers() {
        List<Trainer> expected = List.of(buildTrainerWithId(1L));
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(dao.findTraineeAvailableTrainers(DEFAULT_USERNAME)).thenReturn(expected);

        List<Trainer> actual = service.getAvailableTrainers(DEFAULT_USERNAME);

        assertEquals(expected, actual);
        verify(dao).findTraineeAvailableTrainers(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowExceptionWhenGettingAvailableTrainersForUnknownTrainee() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getAvailableTrainers(DEFAULT_USERNAME));
    }

    @Test
    void shouldReturnTrainingsByCriteria() {
        TraineeTrainingSearchFilter filter = mock(TraineeTrainingSearchFilter.class);
        when(filter.getUsername()).thenReturn(DEFAULT_USERNAME);
        List<Training> expected = List.of();

        when(trainingDao.findTraineeTrainingsByCriteria(filter)).thenReturn(expected);

        List<Training> actual = service.getTrainingsByCriteria(filter);

        assertEquals(expected, actual);
        verify(trainingDao).findTraineeTrainingsByCriteria(filter);
    }

    @Test
    void shouldReturnTrueWhenUsernameAndPasswordMatch() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        User userWithPassword = trainee.getUser().toBuilder().password("encodedPassword").build();
        trainee = trainee.toBuilder().user(userWithPassword).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches(DEFAULT_PASSWORD, "encodedPassword")).thenReturn(true);

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertTrue(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(passwordEncoder).matches(DEFAULT_PASSWORD, "encodedPassword");
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        User userWithPassword = trainee.getUser().toBuilder().password("encodedPassword").build();
        trainee = trainee.toBuilder().user(userWithPassword).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, "wrongPassword");

        assertFalse(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(passwordEncoder).matches("wrongPassword", "encodedPassword");
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainee() {
        Trainee existingTrainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        Trainee updateRequest = getDefaultTraineeBuilder()
                .user(getDefaultUserBuilder()
                        .username(DEFAULT_USERNAME)
                        .firstName("Sophia")
                        .lastName("Wilson")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .address("456 Oak Ave")
                .build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(existingTrainee));
        when(dao.update(any(Trainee.class))).thenReturn(updateRequest);

        Trainee actual = service.updateTrainee(updateRequest);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        ArgumentCaptor<Trainee> traineeCaptor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(traineeCaptor.capture());
        Trainee mergedTrainee = traineeCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, mergedTrainee.getUser().getUsername());
        assertEquals(updateRequest.getUser().getFirstName(), mergedTrainee.getUser().getFirstName());
        assertEquals(updateRequest.getUser().getLastName(), mergedTrainee.getUser().getLastName());
        assertEquals(updateRequest.getAddress(), mergedTrainee.getAddress());
        assertEquals(updateRequest, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainee() {
        Trainee updateRequest = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainee(updateRequest));

        assertEquals("Trainee not found with username: " + DEFAULT_USERNAME, exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineeTrainersSuccessfully() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        Trainer trainer1 = buildTrainerWithId(1L);
        List<String> trainerUsernames = List.of("trainer1");

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(dao.findTraineeTrainersByUsernames(trainerUsernames)).thenReturn(List.of(trainer1));
        when(dao.update(any(Trainee.class))).thenReturn(trainee);

        Trainee result = service.updateTraineeTrainers(DEFAULT_USERNAME, trainerUsernames);

        assertNotNull(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao).findTraineeTrainersByUsernames(trainerUsernames);
        verify(dao).update(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineeTrainersWithPartialMatch() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        Trainer trainer1 = buildTrainerWithId(1L);
        List<String> trainerUsernames = List.of("trainer1", "nonexistent_trainer");

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(dao.findTraineeTrainersByUsernames(trainerUsernames)).thenReturn(List.of(trainer1));
        when(dao.update(any(Trainee.class))).thenReturn(trainee);

        Trainee result = service.updateTraineeTrainers(DEFAULT_USERNAME, trainerUsernames);

        assertNotNull(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao).findTraineeTrainersByUsernames(trainerUsernames);
        verify(dao).update(any(Trainee.class));
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingTrainersForUnknownTrainee() {
        List<String> trainerUsernames = List.of("trainer1");

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateTraineeTrainers(DEFAULT_USERNAME, trainerUsernames));

        assertEquals("Trainee not found with username: " + DEFAULT_USERNAME, exception.getMessage());
        verify(dao, never()).findTraineeTrainersByUsernames(any());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldUpdateTraineePasswordSuccessfully() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        String newPassword = "newSecurePassword123";
        String encodedNewPassword = "encoded-" + newPassword;
        LoginChangeDto dto = new LoginChangeDto(DEFAULT_USERNAME, "oldPassword", newPassword);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("oldPassword", trainee.getUser().getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        service.updateTraineePassword(dto);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(captor.capture());
        assertEquals(encodedNewPassword, captor.getValue().getUser().getPassword());
    }

    @Test
    void shouldThrowAuthenticationExceptionWhenOldPasswordIsInvalid() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        LoginChangeDto dto = new LoginChangeDto(DEFAULT_USERNAME, "wrongPassword", "newPassword");

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainee));
        when(passwordEncoder.matches("wrongPassword", trainee.getUser().getPassword())).thenReturn(false);

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> service.updateTraineePassword(dto));

        assertEquals("Invalid username or password", exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldUpdateActivationStatus() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        User inactiveUser = trainee.getUser().toBuilder().isActive(false).build();
        Trainee inactiveTrainee = trainee.toBuilder().user(inactiveUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(inactiveTrainee));

        service.updateActivationStatus(DEFAULT_USERNAME, true);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(dao).update(captor.capture());
        assertTrue(captor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldThrowConflictExceptionWhenActivatingAlreadyActiveTrainee() {
        Trainee activeTrainee = buildTraineeWithUsername(DEFAULT_USERNAME);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(activeTrainee));

        ConflictException exception = assertThrows(ConflictException.class, () -> service.updateActivationStatus(DEFAULT_USERNAME, true));

        assertEquals("Trainee with username: " + DEFAULT_USERNAME + " is already active", exception.getMessage());
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenDeactivatingAlreadyInactiveTrainee() {
        Trainee trainee = buildTraineeWithUsername(DEFAULT_USERNAME);
        User inactiveUser = trainee.getUser().toBuilder().isActive(false).build();
        Trainee inactiveTrainee = trainee.toBuilder().user(inactiveUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(inactiveTrainee));

        ConflictException exception = assertThrows(ConflictException.class, () -> service.updateActivationStatus(DEFAULT_USERNAME, false));

        assertEquals("Trainee with username: " + DEFAULT_USERNAME + " is already inactive", exception.getMessage());
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingActivationStatusForUnknownTrainee() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> service.updateActivationStatus(DEFAULT_USERNAME, true));

        assertEquals("Trainee not found with username: " + DEFAULT_USERNAME, exception.getMessage());
        verify(dao, never()).update(any(Trainee.class));
    }

    @Test
    void shouldDeleteTraineeByUsernameSuccessfully() {
        when(dao.deleteByUsername(DEFAULT_USERNAME)).thenReturn(true);

        boolean result = service.deleteTraineeByUsername(DEFAULT_USERNAME);

        assertTrue(result);
        verify(dao).deleteByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldReturnFalseWhenDeletingByNonExistentUsername() {
        String nonExistingUsername = "unknown";
        when(dao.deleteByUsername(nonExistingUsername)).thenReturn(false);

        boolean result = service.deleteTraineeByUsername(nonExistingUsername);

        assertFalse(result);
        verify(dao).deleteByUsername(nonExistingUsername);
    }

}
