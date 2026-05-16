package com.gym.crm.service.impl;

import com.gym.crm.dao.TrainerDao;
import com.gym.crm.dao.TrainingTypeDao;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.User;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.common.ProfileCredentialGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_SPECIALIZATION;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithoutCredentials;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultTrainerBuilder;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultTrainingTypeBuilder;
import static com.gym.crm.factory.TrainerTestFactory.getDefaultUserBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerDao dao;

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @Mock
    private ProfileCredentialGenerator generator;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TrainerService service;

    @BeforeEach
    void setUp() {
        TrainerServiceImpl implementation = new TrainerServiceImpl();
        implementation.setTrainerDao(dao);
        implementation.setTrainingTypeDao(trainingTypeDao);
        implementation.setCredentialGenerator(generator);
        implementation.setPasswordEncoder(passwordEncoder);
        service = implementation;
    }

    @Test
    void shouldCreateTrainerWithGeneratedCredentials() {
        Trainer trainerWithoutCredentials = buildTrainerWithoutCredentials();
        Trainer expected = buildTrainerWithId(DEFAULT_TRAINER_ID);
        String encodedPassword = "encoded-" + DEFAULT_PASSWORD;

        when(trainingTypeDao.findByName(DEFAULT_SPECIALIZATION)).thenReturn(Optional.of(trainerWithoutCredentials.getSpecialization()));
        when(generator.generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)).thenReturn(DEFAULT_USERNAME);
        when(generator.generatePassword()).thenReturn(DEFAULT_PASSWORD);
        when(passwordEncoder.encode(DEFAULT_PASSWORD)).thenReturn(encodedPassword);
        when(dao.save(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.createTrainer(trainerWithoutCredentials);

        verify(generator).generateUsername(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME);
        verify(generator).generatePassword();
        verify(passwordEncoder).encode(DEFAULT_PASSWORD);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).save(trainerCaptor.capture());
        Trainer trainerWithCredentials = trainerCaptor.getValue();
        assertEquals(DEFAULT_USERNAME, trainerWithCredentials.getUser().getUsername());
        assertEquals(encodedPassword, trainerWithCredentials.getUser().getPassword());
        assertEquals(DEFAULT_FIRST_NAME, trainerWithCredentials.getUser().getFirstName());
        assertEquals(DEFAULT_LAST_NAME, trainerWithCredentials.getUser().getLastName());
        assertEquals(expected.getSpecialization(), trainerWithCredentials.getSpecialization());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldThrowExceptionWhenCreatingTrainerWithInvalidSpecialization() {
        Trainer trainer = buildTrainerWithoutCredentials();

        when(trainingTypeDao.findByName(DEFAULT_SPECIALIZATION)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.createTrainer(trainer));

        assertEquals("TrainingType not found with name: " + DEFAULT_SPECIALIZATION, exception.getMessage());
        verify(trainingTypeDao).findByName(DEFAULT_SPECIALIZATION);
        verifyNoInteractions(generator, dao);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingTrainerWithNullUser() {
        Trainer trainer = Trainer.builder().build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createTrainer(trainer));

        assertEquals("Trainer user cannot be null", exception.getMessage());
        verifyNoInteractions(trainingTypeDao, generator, dao);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        Trainer expected = buildTrainerWithId();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(expected));

        Trainer actual = service.getTrainerByUsername(DEFAULT_USERNAME);

        assertEquals(expected, actual);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowExceptionWhenGettingUnknownTrainerByUsername() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.getTrainerByUsername(DEFAULT_USERNAME));

        assertEquals("Trainer not found with username: " + DEFAULT_USERNAME, exception.getMessage());
    }

    @Test
    void shouldReturnTrueWhenUsernameAndPasswordMatch() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User userWithPassword = trainer.getUser().toBuilder().password("encodedPassword").build();
        trainer = trainer.toBuilder().user(userWithPassword).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches(DEFAULT_PASSWORD, "encodedPassword")).thenReturn(true);

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertTrue(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(passwordEncoder).matches(DEFAULT_PASSWORD, "encodedPassword");
    }

    @Test
    void shouldReturnFalseWhenPasswordDoesNotMatch() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, "wrong");

        assertFalse(result);
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

    @Test
    void shouldThrowNullPointerWhenMatchingWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, null));

        assertEquals("Password cannot be null", exception.getMessage());
    }

    @Test
    void shouldReturnFalseWhenNoTrainerExists() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        boolean result = service.doesUsernameAndPasswordMatch(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        assertFalse(result);
    }

    @Test
    void shouldThrowNullPointerWhenMatchingWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.doesUsernameAndPasswordMatch(null, DEFAULT_PASSWORD));

        assertEquals("Trainer username cannot be null", exception.getMessage());
    }

    @Test
    void shouldMergeUpdatedFieldsAndPreserveExistingUsernameWhenUpdatingTrainer() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        Trainer updateRequest = getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .user(getDefaultTrainerBuilder().build().getUser().toBuilder()
                        .firstName("Elena")
                        .lastName("Rodriguez")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .specialization(getDefaultTrainingTypeBuilder().id(2L).trainingTypeName("Strength").build())
                .build();
        Trainer expected = getDefaultTrainerBuilder()
                .id(DEFAULT_TRAINER_ID)
                .user(getDefaultTrainerBuilder().build().getUser().toBuilder()
                        .firstName("Elena")
                        .lastName("Rodriguez")
                        .password("newPassword")
                        .isActive(false)
                        .build())
                .specialization(getDefaultTrainingTypeBuilder().id(2L).trainingTypeName("Strength").build())
                .build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(expected);

        Trainer actual = service.updateTrainer(updateRequest);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        Trainer mergedTrainer = trainerCaptor.getValue();
        assertEquals(DEFAULT_TRAINER_ID, mergedTrainer.getId());
        assertEquals(DEFAULT_USERNAME, mergedTrainer.getUser().getUsername());
        assertEquals("Elena", mergedTrainer.getUser().getFirstName());
        assertEquals("Rodriguez", mergedTrainer.getUser().getLastName());
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingUnknownTrainer() {
        Trainer updateRequest = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateTrainer(updateRequest));

        assertEquals("Trainer not found with username: " + DEFAULT_USERNAME, exception.getMessage());
        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao, never()).update(any(Trainer.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingNullTrainer() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainer(null));

        assertEquals("Trainer cannot be null", exception.getMessage());
        verifyNoInteractions(dao, generator);
    }

    @Test
    void shouldUpdateTrainerWithoutChangingSpecialization() {
        Trainer existingTrainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User updatedUser = existingTrainer.getUser().toBuilder().firstName("NewName").build();
        Trainer updateRequest = existingTrainer.toBuilder().user(updatedUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(existingTrainer));
        when(dao.update(any(Trainer.class))).thenReturn(updateRequest);

        service.updateTrainer(updateRequest);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(trainingTypeDao, never()).findByName(any());
        verify(dao).update(any(Trainer.class));
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerWithNullUser() {
        Trainer trainer = Trainer.builder().id(DEFAULT_TRAINER_ID).build();

        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainer(trainer));

        assertEquals("Trainer user cannot be null", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerWithNullUsername() {
        Trainer trainer = buildTrainerWithoutCredentials().toBuilder()
                .user(getDefaultUserBuilder().username(null).build())
                .build();

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.updateTrainer(trainer));

        assertEquals("Trainer username cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateTrainerPassword() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        String newPassword = "newPassword";
        String encodedNewPassword = "encoded-" + newPassword;
        com.gym.crm.dto.LoginChangeDto dto = new com.gym.crm.dto.LoginChangeDto(trainer.getUser().getUsername(), "oldPassword", newPassword);

        when(dao.findByUsername(trainer.getUser().getUsername())).thenReturn(Optional.of(trainer));
        when(passwordEncoder.matches("oldPassword", trainer.getUser().getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

        service.updateTrainerPassword(dto);

        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(encodedNewPassword, trainerCaptor.getValue().getUser().getPassword());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingPasswordForUnknownTrainer() {
        com.gym.crm.dto.LoginChangeDto dto = new com.gym.crm.dto.LoginChangeDto("nonexistent", "oldPassword", "newPassword");
        when(dao.findByUsername("nonexistent")).thenReturn(Optional.empty());

        com.gym.crm.security.AuthenticationException exception = assertThrows(com.gym.crm.security.AuthenticationException.class, () -> service.updateTrainerPassword(dto));

        assertEquals("Invalid username or password", exception.getMessage());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingPasswordWithNullDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateTrainerPassword(null));

        assertEquals("LoginChangeDto cannot be null", exception.getMessage());
    }

    @Test
    void shouldUpdateActivationStatusToActive() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User inactiveUser = trainer.getUser().toBuilder().isActive(false).build();
        trainer = trainer.toBuilder().user(inactiveUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        service.updateActivationStatus(DEFAULT_USERNAME, true);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(true, trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldUpdateActivationStatusToInactive() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User activeUser = trainer.getUser().toBuilder().isActive(true).build();
        trainer = trainer.toBuilder().user(activeUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        service.updateActivationStatus(DEFAULT_USERNAME, false);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        ArgumentCaptor<Trainer> trainerCaptor = ArgumentCaptor.forClass(Trainer.class);
        verify(dao).update(trainerCaptor.capture());
        assertEquals(false, trainerCaptor.getValue().getUser().getIsActive());
    }

    @Test
    void shouldDoNothingWhenUpdatingToAlreadyActiveStatus() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User activeUser = trainer.getUser().toBuilder().isActive(true).build();
        trainer = trainer.toBuilder().user(activeUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        service.updateActivationStatus(DEFAULT_USERNAME, true);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao, never()).update(any());
    }

    @Test
    void shouldDoNothingWhenUpdatingToAlreadyInactiveStatus() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        User inactiveUser = trainer.getUser().toBuilder().isActive(false).build();
        trainer = trainer.toBuilder().user(inactiveUser).build();

        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.of(trainer));

        service.updateActivationStatus(DEFAULT_USERNAME, false);

        verify(dao).findByUsername(DEFAULT_USERNAME);
        verify(dao, never()).update(any());
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingActivationStatusWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> service.updateActivationStatus(null, true));

        assertEquals("Trainer username cannot be null", exception.getMessage());
        verifyNoInteractions(dao);
    }

    @Test
    void shouldThrowEntityNotFoundWhenUpdatingActivationStatusForNonExistingTrainer() {
        when(dao.findByUsername(DEFAULT_USERNAME)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> service.updateActivationStatus(DEFAULT_USERNAME, true));

        assertEquals("Trainer not found with username: " + DEFAULT_USERNAME, exception.getMessage());
        verify(dao).findByUsername(DEFAULT_USERNAME);
    }

}
