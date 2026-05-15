package com.gym.crm.facade;

import com.gia.openapi.model.LoginChangeRequest;
import com.gym.crm.dto.LoginChangeDto;
import com.gym.crm.dto.TraineeCreateDto;
import com.gym.crm.dto.TraineeCreateResponseDto;
import com.gym.crm.dto.TraineeResponseDto;
import com.gym.crm.dto.TraineeUpdateDto;
import com.gym.crm.dto.TrainerCreateDto;
import com.gym.crm.dto.TrainerCreateResponseDto;
import com.gym.crm.dto.TrainerResponseDto;
import com.gym.crm.dto.TrainerUpdateDto;
import com.gym.crm.dto.TrainingCreateDto;
import com.gym.crm.dto.TrainingResponseDto;
import com.gym.crm.dto.filter.TraineeTrainingSearchFilter;
import com.gym.crm.dto.filter.TrainerTrainingSearchFilter;
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.mapper.AuthMapper;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.AuthenticationService;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
import com.gym.crm.service.common.BusinessValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeCreateResponseDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeResponseDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeUpdateDto;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerCreateResponseDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerResponseDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.buildTrainerWithId;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingCreateDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingResponseDto;
import static com.gym.crm.factory.TrainingTestFactory.buildTrainingWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    private static final String USERNAME = "username";

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private BusinessValidator businessValidator;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private AuthMapper authMapper;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService, authenticationService, businessValidator);
        facade.setTraineeMapper(traineeMapper);
        facade.setTrainerMapper(trainerMapper);
        facade.setTrainingMapper(trainingMapper);
        facade.setAuthMapper(authMapper);
    }

    @Test
    void shouldCreateTraineeAndReturnResponseDto() {
        TraineeCreateDto createDto = buildTraineeCreateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeCreateResponseDto expected = buildTraineeCreateResponseDto();

        doNothing().when(businessValidator).validate(createDto);
        when(traineeMapper.toEntity(createDto)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toCreateResponseDto(trainee)).thenReturn(expected);

        TraineeCreateResponseDto actual = facade.createTrainee(createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(createDto);
        verify(traineeMapper).toEntity(createDto);
        verify(traineeService).createTrainee(trainee);
        verify(traineeMapper).toCreateResponseDto(trainee);
    }

    @Test
    void shouldThrowValidationExceptionWhenCreatingTraineeWithInvalidDto() {
        TraineeCreateDto createDto = buildTraineeCreateDto();

        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(createDto);

        assertThrows(ValidationException.class, () -> facade.createTrainee(createDto));

        verify(businessValidator).validate(createDto);
        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTrainee() {
        TraineeCreateDto createDto = buildTraineeCreateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        doNothing().when(businessValidator).validate(createDto);
        when(traineeMapper.toEntity(createDto)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> facade.createTrainee(createDto));

        verify(traineeService).createTrainee(trainee);
        verifyNoInteractions(trainerService, trainingService);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTraineeDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTrainee(null));

        assertEquals("TraineeCreateDto cannot be null", exception.getMessage());

        verifyNoInteractions(businessValidator, traineeMapper, traineeService);
    }

    @Test
    void shouldUpdateTraineeAndMapToDto() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        doNothing().when(businessValidator).validate(updateDto);
        when(traineeMapper.toEntity(updateDto, DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.updateTrainee(USERNAME, DEFAULT_TRAINEE_ID, updateDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(updateDto);
        verify(traineeMapper).toEntity(updateDto, DEFAULT_TRAINEE_ID);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldThrowValidationExceptionWhenUpdatingTraineeWithInvalidDto() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();

        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(updateDto);

        assertThrows(ValidationException.class, () -> facade.updateTrainee(USERNAME, DEFAULT_TRAINEE_ID, updateDto));

        verify(businessValidator).validate(updateDto);
        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTrainee() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        doNothing().when(businessValidator).validate(updateDto);
        when(traineeMapper.toEntity(updateDto, DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTrainee(USERNAME, DEFAULT_TRAINEE_ID, updateDto));

        verify(traineeMapper).toEntity(updateDto, DEFAULT_TRAINEE_ID);
        verify(traineeService).updateTrainee(trainee);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainee(USERNAME, null, buildTraineeUpdateDto()));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTraineeDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainee(USERNAME, DEFAULT_TRAINEE_ID, null));

        assertEquals("TraineeUpdateDto cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldDeleteTrainee() {
        when(traineeService.deleteTrainee(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean actual = facade.deleteTrainee(USERNAME, DEFAULT_TRAINEE_ID);

        assertTrue(actual);
        verify(traineeService).deleteTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        when(traineeService.deleteTrainee(DEFAULT_TRAINEE_ID)).thenReturn(false);

        boolean actual = facade.deleteTrainee(USERNAME, DEFAULT_TRAINEE_ID);

        assertFalse(actual);
        verify(traineeService).deleteTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deleteTrainee(USERNAME, null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldGetTraineeAndMapToDto() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeService.getTrainee(DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.getTrainee(USERNAME, DEFAULT_TRAINEE_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getTrainee(DEFAULT_TRAINEE_ID);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTrainee() {
        when(traineeService.getTrainee(DEFAULT_TRAINEE_ID)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTrainee(USERNAME, DEFAULT_TRAINEE_ID));

        verify(traineeService).getTrainee(DEFAULT_TRAINEE_ID);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainee(USERNAME, null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldGetAllTraineesAndReturnEmptyList() {
        when(traineeService.getAllTrainees()).thenReturn(List.of());
        when(traineeMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TraineeResponseDto> actual = facade.getAllTrainees(USERNAME);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(traineeService).getAllTrainees();
        verify(traineeMapper).toDtoList(List.of());
    }

    @Test
    void shouldGetTraineeByUsernameAndMapToDto() {
        String username = "username";
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeService.getTraineeByUsername(username)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.getTraineeByUsername(username);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getTraineeByUsername(username);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTraineeByUsername() {
        String username = "username";
        when(traineeService.getTraineeByUsername(username)).thenThrow(EntityNotFoundException.forUsername("Trainee", username));

        assertThrows(EntityNotFoundException.class, () -> facade.getTraineeByUsername(username));

        verify(traineeService).getTraineeByUsername(username);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingTraineeByNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTraineeByUsername(null));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService, traineeMapper);
    }

    @Test
    void shouldUpdateTraineeTrainersAndMapToDto() {
        List<Long> trainerIds = List.of(1L, 2L);
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeService.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.updateTraineeTrainers(USERNAME, DEFAULT_TRAINEE_ID, trainerIds);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTraineeTrainers() {
        List<Long> trainerIds = List.of(1L);
        when(traineeService.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTraineeTrainers(USERNAME, DEFAULT_TRAINEE_ID, trainerIds));

        verify(traineeService).updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeTrainersWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineeTrainers(USERNAME, null, List.of(1L)));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeTrainersWithNullTrainerIds() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineeTrainers(USERNAME, DEFAULT_TRAINEE_ID, null));

        assertEquals("Trainer IDs cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldChangePasswordForTrainee() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldPassword123", "newPassword123");
        LoginChangeDto dto = new LoginChangeDto(USERNAME, "oldPassword123", "newPassword123");

        when(authMapper.toDto(request)).thenReturn(dto);
        doNothing().when(businessValidator).validate(dto);

        facade.changePassword(USERNAME, request);

        verify(authMapper).toDto(request);
        verify(businessValidator).validate(dto);
        verify(authenticationService).changePassword(dto);
    }

    @Test
    void shouldChangePasswordForTrainer() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldPassword123", "newPassword123");
        LoginChangeDto dto = new LoginChangeDto(USERNAME, "oldPassword123", "newPassword123");

        when(authMapper.toDto(request)).thenReturn(dto);
        doNothing().when(businessValidator).validate(dto);

        facade.changePassword(USERNAME, request);

        verify(authMapper).toDto(request);
        verify(businessValidator).validate(dto);
        verify(authenticationService).changePassword(dto);
    }

    @Test
    void shouldThrowValidationExceptionWhenChangingPasswordWithInvalidDto() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldPassword", "newPassword");
        LoginChangeDto dto = new LoginChangeDto(USERNAME, "oldPassword", "newPassword");

        when(authMapper.toDto(request)).thenReturn(dto);
        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(dto);

        assertThrows(ValidationException.class, () -> facade.changePassword(USERNAME, request));
        verify(businessValidator).validate(dto);
        verifyNoInteractions(authenticationService);
    }

    @Test
    void shouldThrowNullPointerWhenChangingPasswordWithNullRequest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.changePassword(USERNAME, null));

        assertEquals("LoginChangeRequest cannot be null", exception.getMessage());
        verifyNoInteractions(authMapper, businessValidator, authenticationService);
    }

    @Test
    void shouldActivateTrainee() {
        facade.activateTrainee(USERNAME, DEFAULT_TRAINEE_ID);

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenActivatingTrainee() {
        doThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID))
                .when(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.activateTrainee(USERNAME, DEFAULT_TRAINEE_ID));

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenActivatingTrainee() {
        doThrow(new IllegalStateException("Already active"))
                .when(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(IllegalStateException.class, () -> facade.activateTrainee(USERNAME, DEFAULT_TRAINEE_ID));

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenActivatingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.activateTrainee(USERNAME, null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldDeactivateTrainee() {
        facade.deactivateTrainee(USERNAME, DEFAULT_TRAINEE_ID);

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenDeactivatingTrainee() {
        doThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID))
                .when(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.deactivateTrainee(USERNAME, DEFAULT_TRAINEE_ID));

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenDeactivatingTrainee() {
        doThrow(new IllegalStateException("Already deactivated"))
                .when(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(IllegalStateException.class, () -> facade.deactivateTrainee(USERNAME, DEFAULT_TRAINEE_ID));

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deactivateTrainee(USERNAME, null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        String username = "username";
        when(traineeService.deleteTraineeByUsername(username)).thenReturn(true);

        boolean actual = facade.deleteTraineeByUsername(username);

        assertTrue(actual);
        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    void shouldReturnFalseWhenDeletingTraineeByNonExistentUsername() {
        String username = "username";
        when(traineeService.deleteTraineeByUsername(username)).thenReturn(false);

        boolean actual = facade.deleteTraineeByUsername(username);

        assertFalse(actual);
        verify(traineeService).deleteTraineeByUsername(username);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingNullTraineeUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deleteTraineeByUsername(null));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldCreateTrainerAndReturnResponseDto() {
        TrainerCreateDto createDto = buildTrainerCreateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerCreateResponseDto expected = buildTrainerCreateResponseDto();

        doNothing().when(businessValidator).validate(createDto);
        when(trainerMapper.toEntity(createDto)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toCreateResponseDto(trainer)).thenReturn(expected);

        TrainerCreateResponseDto actual = facade.createTrainer(createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(createDto);
        verify(trainerMapper).toEntity(createDto);
        verify(trainerService).createTrainer(trainer);
        verify(trainerMapper).toCreateResponseDto(trainer);
    }

    @Test
    void shouldThrowValidationExceptionWhenCreatingTrainerWithInvalidDto() {
        TrainerCreateDto createDto = buildTrainerCreateDto();

        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(createDto);

        assertThrows(ValidationException.class, () -> facade.createTrainer(createDto));

        verify(businessValidator).validate(createDto);
        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTrainer() {
        TrainerCreateDto createDto = buildTrainerCreateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        doNothing().when(businessValidator).validate(createDto);
        when(trainerMapper.toEntity(createDto)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> facade.createTrainer(createDto));

        verify(trainerService).createTrainer(trainer);
        verifyNoInteractions(traineeService, trainingService);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainerDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTrainer(null));

        assertEquals("TrainerCreateDto cannot be null", exception.getMessage());

        verifyNoInteractions(businessValidator, trainerMapper, trainerService);
    }

    @Test
    void shouldUpdateTrainerAndMapToDto() {
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto expected = buildTrainerResponseDto();

        doNothing().when(businessValidator).validate(updateDto);
        when(trainerMapper.toEntity(updateDto, DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerResponseDto actual = facade.updateTrainer(USERNAME, DEFAULT_TRAINER_ID, updateDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(updateDto);
        verify(trainerMapper).toEntity(updateDto, DEFAULT_TRAINER_ID);
        verify(trainerService).updateTrainer(trainer);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void shouldThrowValidationExceptionWhenUpdatingTrainerWithInvalidDto() {
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();

        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(updateDto);

        assertThrows(ValidationException.class, () -> facade.updateTrainer(USERNAME, DEFAULT_TRAINER_ID, updateDto));

        verify(businessValidator).validate(updateDto);
        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTrainer() {
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        doNothing().when(businessValidator).validate(updateDto);
        when(trainerMapper.toEntity(updateDto, DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTrainer(USERNAME, DEFAULT_TRAINER_ID, updateDto));

        verify(trainerMapper).toEntity(updateDto, DEFAULT_TRAINER_ID);
        verify(trainerService).updateTrainer(trainer);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainer(USERNAME, null, buildTrainerUpdateDto()));

        assertEquals("Trainer ID cannot be null", exception.getMessage());

        verifyNoInteractions(businessValidator, trainerMapper, trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTrainerDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainer(USERNAME, DEFAULT_TRAINER_ID, null));

        assertEquals("TrainerUpdateDto cannot be null", exception.getMessage());

        verifyNoInteractions(businessValidator, trainerMapper, trainerService);
    }

    @Test
    void shouldGetTrainerAndMapToDto() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto expected = buildTrainerResponseDto();

        when(trainerService.getTrainer(DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerResponseDto actual = facade.getTrainer(USERNAME, DEFAULT_TRAINER_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getTrainer(DEFAULT_TRAINER_ID);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTrainer() {
        when(trainerService.getTrainer(DEFAULT_TRAINER_ID)).thenThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTrainer(USERNAME, DEFAULT_TRAINER_ID));

        verify(trainerService).getTrainer(DEFAULT_TRAINER_ID);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainer(USERNAME, null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldGetAllTrainersAndMapToDtoList() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto responseDto = buildTrainerResponseDto();
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponseDto> expected = List.of(responseDto);

        when(trainerService.getAllTrainers()).thenReturn(trainers);
        when(trainerMapper.toDtoList(trainers)).thenReturn(expected);

        List<TrainerResponseDto> actual = facade.getAllTrainers(USERNAME);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getAllTrainers();
        verify(trainerMapper).toDtoList(trainers);
    }

    @Test
    void shouldGetAllTrainersAndReturnEmptyList() {
        when(trainerService.getAllTrainers()).thenReturn(List.of());
        when(trainerMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TrainerResponseDto> actual = facade.getAllTrainers(USERNAME);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(trainerService).getAllTrainers();
        verify(trainerMapper).toDtoList(List.of());
    }

    @Test
    void shouldGetTrainerByUsernameAndMapToDto() {
        String username = "username";
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto expected = buildTrainerResponseDto();

        when(trainerService.getTrainerByUsername(username)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerResponseDto actual = facade.getTrainerByUsername(username);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getTrainerByUsername(username);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTrainerByUsername() {
        String username = "username";
        when(trainerService.getTrainerByUsername(username)).thenThrow(EntityNotFoundException.forUsername("Trainer", username));

        assertThrows(EntityNotFoundException.class, () -> facade.getTrainerByUsername(username));

        verify(trainerService).getTrainerByUsername(username);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingTrainerByNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainerByUsername(null));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService, trainerMapper);
    }

    @Test
    void shouldGetAllTrainersNotAssignedToTraineeAndMapToDtoList() {
        String username = "username";
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto responseDto = buildTrainerResponseDto();
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponseDto> expected = List.of(responseDto);

        when(trainerService.getAllTrainersNotAssignedToTrainee(username)).thenReturn(trainers);
        when(trainerMapper.toDtoList(trainers)).thenReturn(expected);

        List<TrainerResponseDto> actual = facade.getAllTrainersNotAssignedToTrainee(username);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getAllTrainersNotAssignedToTrainee(username);
        verify(trainerMapper).toDtoList(trainers);
    }

    @Test
    void shouldGetAllTrainersNotAssignedToTraineeAndReturnEmptyList() {
        String username = "username";

        when(trainerService.getAllTrainersNotAssignedToTrainee(username)).thenReturn(List.of());
        when(trainerMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TrainerResponseDto> actual = facade.getAllTrainersNotAssignedToTrainee(username);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(trainerService).getAllTrainersNotAssignedToTrainee(username);
        verify(trainerMapper).toDtoList(List.of());
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingUnassignedTrainers() {
        String username = "username";

        when(trainerService.getAllTrainersNotAssignedToTrainee(username)).thenThrow(EntityNotFoundException.forUsername("Trainee", username));

        assertThrows(EntityNotFoundException.class, () -> facade.getAllTrainersNotAssignedToTrainee(username));

        verify(trainerService).getAllTrainersNotAssignedToTrainee(username);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingUnassignedTrainersWithNullTraineeUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getAllTrainersNotAssignedToTrainee(null));

        assertEquals("Trainee username cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService, trainerMapper);
    }

    @Test
    void shouldActivateTrainer() {
        facade.activateTrainer(USERNAME, DEFAULT_TRAINER_ID);

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenActivatingTrainer() {
        doThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID))
                .when(trainerService).activateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.activateTrainer(USERNAME, DEFAULT_TRAINER_ID));

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenActivatingTrainer() {
        doThrow(new IllegalStateException("Already active"))
                .when(trainerService).activateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(IllegalStateException.class, () -> facade.activateTrainer(USERNAME, DEFAULT_TRAINER_ID));

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenActivatingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.activateTrainer(USERNAME, null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldDeactivateTrainer() {
        facade.deactivateTrainer(USERNAME, DEFAULT_TRAINER_ID);

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenDeactivatingTrainer() {
        doThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID))
                .when(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.deactivateTrainer(USERNAME, DEFAULT_TRAINER_ID));

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenDeactivatingTrainer() {
        doThrow(new IllegalStateException("Already deactivated"))
                .when(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(IllegalStateException.class, () -> facade.deactivateTrainer(USERNAME, DEFAULT_TRAINER_ID));

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deactivateTrainer(USERNAME, null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldCreateTrainingAndReturnResponseDto() {
        TrainingCreateDto createDto = buildTrainingCreateDto();
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto expected = buildTrainingResponseDto();

        doNothing().when(businessValidator).validate(createDto);
        when(trainingMapper.toEntity(createDto)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expected);

        TrainingResponseDto actual = facade.createTraining(USERNAME, createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(createDto);
        verify(trainingMapper).toEntity(createDto);
        verify(trainingService).createTraining(training);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void shouldThrowValidationExceptionWhenCreatingTrainingWithInvalidDto() {
        TrainingCreateDto createDto = buildTrainingCreateDto();

        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(createDto);

        assertThrows(ValidationException.class, () -> facade.createTraining(USERNAME, createDto));

        verify(businessValidator).validate(createDto);
        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTraining() {
        TrainingCreateDto createDto = buildTrainingCreateDto();
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);

        doNothing().when(businessValidator).validate(createDto);
        when(trainingMapper.toEntity(createDto)).thenReturn(training);
        when(trainingService.createTraining(training)).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> facade.createTraining(USERNAME, createDto));

        verify(trainingService).createTraining(training);
        verifyNoInteractions(traineeService, trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainingDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTraining(USERNAME, null));

        assertEquals("TrainingCreateDto cannot be null", exception.getMessage());

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    void shouldGetTrainingAndMapToDto() {
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto expected = buildTrainingResponseDto();

        when(trainingService.getTraining(DEFAULT_TRAINING_ID)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expected);

        TrainingResponseDto actual = facade.getTraining(USERNAME, DEFAULT_TRAINING_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingService).getTraining(DEFAULT_TRAINING_ID);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTraining() {
        when(trainingService.getTraining(DEFAULT_TRAINING_ID)).thenThrow(EntityNotFoundException.forId("Training", DEFAULT_TRAINING_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTraining(USERNAME, DEFAULT_TRAINING_ID));

        verify(trainingService).getTraining(DEFAULT_TRAINING_ID);
        verifyNoInteractions(trainingMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainingId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTraining(USERNAME, null));

        assertEquals("Training ID cannot be null", exception.getMessage());

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    void shouldGetAllTrainingsAndMapToDtoList() {
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = buildTrainingResponseDto();
        List<Training> trainings = List.of(training);
        List<TrainingResponseDto> expected = List.of(responseDto);

        when(trainingService.getAllTrainings()).thenReturn(trainings);
        when(trainingMapper.toDtoList(trainings)).thenReturn(expected);

        List<TrainingResponseDto> actual = facade.getAllTrainings(USERNAME);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).toDtoList(trainings);
    }

    @Test
    void shouldGetAllTrainingsAndReturnEmptyList() {
        when(trainingService.getAllTrainings()).thenReturn(List.of());
        when(trainingMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TrainingResponseDto> actual = facade.getAllTrainings(USERNAME);

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).toDtoList(List.of());
    }

    @Test
    void shouldGetTrainingsByTraineeCriteria() {
        TraineeTrainingSearchFilter filter = mock(TraineeTrainingSearchFilter.class);
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = buildTrainingResponseDto();
        List<Training> trainings = List.of(training);
        List<TrainingResponseDto> expected = List.of(responseDto);

        doNothing().when(businessValidator).validate(filter);
        when(trainingService.getTrainingsByTraineeCriteria(filter)).thenReturn(trainings);
        when(trainingMapper.toDtoList(trainings)).thenReturn(expected);

        List<TrainingResponseDto> actual = facade.getTrainingsByTraineeCriteria(USERNAME, filter);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(filter);
        verify(trainingService).getTrainingsByTraineeCriteria(filter);
        verify(trainingMapper).toDtoList(trainings);
    }

    @Test
    void shouldThrowNullPointerWhenTraineeFilterIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainingsByTraineeCriteria(USERNAME, null));

        assertEquals("Filter cannot be null", exception.getMessage());
        verifyNoInteractions(businessValidator, trainingService, trainingMapper);
    }

    @Test
    void shouldGetTrainingsByTrainerCriteria() {
        TrainerTrainingSearchFilter filter = mock(TrainerTrainingSearchFilter.class);
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = buildTrainingResponseDto();
        List<Training> trainings = List.of(training);
        List<TrainingResponseDto> expected = List.of(responseDto);

        doNothing().when(businessValidator).validate(filter);
        when(trainingService.getTrainingsByTrainerCriteria(filter)).thenReturn(trainings);
        when(trainingMapper.toDtoList(trainings)).thenReturn(expected);

        List<TrainingResponseDto> actual = facade.getTrainingsByTrainerCriteria(USERNAME, filter);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(filter);
        verify(trainingService).getTrainingsByTrainerCriteria(filter);
        verify(trainingMapper).toDtoList(trainings);
    }

    @Test
    void shouldThrowNullPointerWhenTrainerFilterIsNull() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainingsByTrainerCriteria(USERNAME, null));

        assertEquals("Filter cannot be null", exception.getMessage());
        verifyNoInteractions(businessValidator, trainingService, trainingMapper);
    }

}
