package com.gym.crm.facade;

import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.dto.LoginChangeDto;
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
import java.util.Set;

import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_ADDRESS;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_DATE_OF_BIRTH;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_FIRST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_LAST_NAME;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_PASSWORD;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_TRAINEE_ID;
import static com.gym.crm.factory.TraineeTestFactory.DEFAULT_USERNAME;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithId;
import static com.gym.crm.factory.TraineeTestFactory.buildTraineeWithIdAndUserId;
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
    void shouldCreateTraineeAndReturnResponse() {
        TraineeCreateRequest request = new TraineeCreateRequest(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .address(DEFAULT_ADDRESS);
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeCreateResponse expected = new TraineeCreateResponse(DEFAULT_USERNAME, DEFAULT_PASSWORD);

        when(traineeMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toCreateResponse(trainee)).thenReturn(expected);

        TraineeCreateResponse actual = facade.createTrainee(request);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeMapper).toEntity(request);
        verify(traineeService).createTrainee(trainee);
        verify(traineeMapper).toCreateResponse(trainee);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTraineeRequest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTrainee(null));

        assertEquals("TraineeCreateRequest cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldGetTraineeByUsernameAndMapToGetResponse() {
        String username = "username";
        Trainee trainee = buildTraineeWithIdAndUserId();
        TraineeGetResponse expected = new TraineeGetResponse(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME)
                .address(DEFAULT_ADDRESS)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .isActive(true);

        when(traineeService.getTraineeByUsername(username)).thenReturn(trainee);
        when(traineeMapper.toGetResponse(trainee)).thenReturn(expected);

        TraineeGetResponse actual = facade.getTraineeByUsername(username);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getTraineeByUsername(username);
        verify(traineeMapper).toGetResponse(trainee);
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
    void shouldGetTraineeTrainingsByCriteriaAndMapToResponseList() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder().build();
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        GetTraineeTrainingResponse responseDto = new GetTraineeTrainingResponse();
        List<Training> trainings = List.of(training);
        List<GetTraineeTrainingResponse> expected = List.of(responseDto);

        doNothing().when(businessValidator).validate(filter);
        when(traineeService.getTrainingsByCriteria(filter)).thenReturn(trainings);
        when(traineeMapper.toGetTraineeTrainingResponseList(trainings)).thenReturn(expected);

        List<GetTraineeTrainingResponse> actual = facade.getTraineeTrainingsByCriteria(USERNAME, filter);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(businessValidator).validate(filter);
        verify(traineeService).getTrainingsByCriteria(filter);
        verify(traineeMapper).toGetTraineeTrainingResponseList(trainings);
    }

    @Test
    void shouldThrowNullPointerWhenGettingTraineeTrainingsWithNullUsername() {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder().build();

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.getTraineeTrainingsByCriteria(null, filter));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(businessValidator, traineeService, traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingTraineeTrainingsWithNullFilter() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.getTraineeTrainingsByCriteria(USERNAME, null));

        assertEquals("Filter cannot be null", exception.getMessage());
        verifyNoInteractions(businessValidator, traineeService, traineeMapper);
    }

    @Test
    void shouldGetAvailableTrainersForTraineeAndMapToResponseList() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        AssignedTrainerResponse responseDto = new AssignedTrainerResponse();
        List<Trainer> trainers = List.of(trainer);
        List<AssignedTrainerResponse> expected = List.of(responseDto);

        when(traineeService.getAvailableTrainers(USERNAME)).thenReturn(trainers);
        when(traineeMapper.toAssignedTrainerResponseListFromList(trainers)).thenReturn(expected);

        List<AssignedTrainerResponse> actual = facade.getAvailableTrainersForTrainee(USERNAME);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getAvailableTrainers(USERNAME);
        verify(traineeMapper).toAssignedTrainerResponseListFromList(trainers);
    }

    @Test
    void shouldThrowNullPointerWhenGettingAvailableTrainersWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.getAvailableTrainersForTrainee(null));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService, traineeMapper);
    }

    @Test
    void shouldUpdateTraineeAndMapToUpdateResponse() {
        TraineeUpdateRequest request = new TraineeUpdateRequest("Sophia", "Wilson", false)
                .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
                .address("456 Oak Ave");
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeUpdateResponse expected = new TraineeUpdateResponse("Sophia", "Wilson")
                .username(USERNAME)
                .isActive(false);

        when(traineeMapper.toEntity(request, USERNAME)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toUpdateResponse(trainee)).thenReturn(expected);

        TraineeUpdateResponse actual = facade.updateTrainee(USERNAME, request);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeMapper).toEntity(request, USERNAME);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeMapper).toUpdateResponse(trainee);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTraineeRequest() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainee(USERNAME, null));

        assertEquals("TraineeUpdateRequest cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldUpdateTraineeTrainersAndMapToResponse() {
        List<String> trainerUsernames = List.of("trainer1", "trainer2");
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest().trainerUsernames(trainerUsernames);
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        AssignedTrainerResponse responseDto = new AssignedTrainerResponse();
        Set<Trainer> trainers = Set.of(trainer);
        List<AssignedTrainerResponse> expectedTrainers = List.of(responseDto);
        TraineeAssignedTrainersUpdateResponse expectedResponse = new TraineeAssignedTrainersUpdateResponse().trainers(expectedTrainers);
        Trainee updatedTrainee = Trainee.builder()
                .id(DEFAULT_TRAINEE_ID)
                .trainers(trainers)
                .build();

        when(traineeService.updateTraineeTrainers(USERNAME, trainerUsernames)).thenReturn(updatedTrainee);
        when(traineeMapper.toAssignedTrainersUpdateResponse(trainers)).thenReturn(expectedResponse);

        TraineeAssignedTrainersUpdateResponse actual = facade.updateTraineeTrainers(USERNAME, request);

        assertNotNull(actual);
        assertEquals(expectedResponse, actual);
        verify(traineeService).updateTraineeTrainers(USERNAME, trainerUsernames);
        verify(traineeMapper).toAssignedTrainersUpdateResponse(trainers);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeTrainersWithNullUsername() {
        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest().trainerUsernames(List.of("trainer1"));

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.updateTraineeTrainers(null, request));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService, traineeMapper);
    }

    @Test
    void shouldUpdateTraineeActivationStatusToActive() {
        ActivationStatusRequest request = new ActivationStatusRequest(true);

        facade.updateTraineeActivationStatus(USERNAME, request);

        verify(traineeService).updateActivationStatus(USERNAME, true);
    }

    @Test
    void shouldUpdateTraineeActivationStatusToInactive() {
        ActivationStatusRequest request = new ActivationStatusRequest(false);

        facade.updateTraineeActivationStatus(USERNAME, request);

        verify(traineeService).updateActivationStatus(USERNAME, false);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingActivationStatusWithNullUsername() {
        ActivationStatusRequest request = new ActivationStatusRequest(true);

        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> facade.updateTraineeActivationStatus(null, request));

        assertEquals("Username cannot be null", exception.getMessage());
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
    void shouldChangePasswordForTrainee() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldPassword123", "newPassword123");
        LoginChangeDto dto = new LoginChangeDto(USERNAME, "oldPassword123", "newPassword123");

        when(authMapper.toDto(request)).thenReturn(dto);
        doNothing().when(businessValidator).validate(request);
        doNothing().when(businessValidator).validate(dto);

        facade.changePassword(USERNAME, request);

        verify(authMapper).toDto(request);
        verify(businessValidator).validate(request);
        verify(businessValidator).validate(dto);
        verify(authenticationService).changePassword(dto);
    }

    @Test
    void shouldThrowValidationExceptionWhenChangingPasswordWithInvalidDto() {
        LoginChangeRequest request = new LoginChangeRequest(USERNAME, "oldPassword", "newPassword");
        LoginChangeDto dto = new LoginChangeDto(USERNAME, "oldPassword", "newPassword");

        when(authMapper.toDto(request)).thenReturn(dto);
        doNothing().when(businessValidator).validate(request);
        doThrow(new ValidationException("Validation error"))
                .when(businessValidator).validate(dto);

        assertThrows(ValidationException.class, () -> facade.changePassword(USERNAME, request));
        verify(businessValidator).validate(request);
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
    void shouldThrowNullPointerWhenCreatingNullTrainerDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTrainer(null));

        assertEquals("TrainerCreateDto cannot be null", exception.getMessage());

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
    void shouldThrowNullPointerWhenGettingAllTrainersWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getAllTrainers(null));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService, trainerMapper);
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
    void shouldThrowNullPointerWhenGettingAllTrainingsWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getAllTrainings(null));

        assertEquals("Username cannot be null", exception.getMessage());

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    void shouldGetTrainingsByTrainerCriteria() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder().build();
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
