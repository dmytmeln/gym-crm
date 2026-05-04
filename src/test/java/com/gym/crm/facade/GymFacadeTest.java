package com.gym.crm.facade;

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
import com.gym.crm.entity.Trainee;
import com.gym.crm.entity.Trainer;
import com.gym.crm.entity.Training;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.mapper.TraineeMapper;
import com.gym.crm.mapper.TrainerMapper;
import com.gym.crm.mapper.TrainingMapper;
import com.gym.crm.service.TraineeService;
import com.gym.crm.service.TrainerService;
import com.gym.crm.service.TrainingService;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
        facade.setTraineeMapper(traineeMapper);
        facade.setTrainerMapper(trainerMapper);
        facade.setTrainingMapper(trainingMapper);
    }

    @Test
    void shouldCreateTraineeAndReturnResponseDto() {
        TraineeCreateDto createDto = buildTraineeCreateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeCreateResponseDto expected = buildTraineeCreateResponseDto();

        when(traineeMapper.toEntity(createDto)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toCreateResponseDto(trainee)).thenReturn(expected);

        TraineeCreateResponseDto actual = facade.createTrainee(createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeMapper).toEntity(createDto);
        verify(traineeService).createTrainee(trainee);
        verify(traineeMapper).toCreateResponseDto(trainee);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTrainee() {
        TraineeCreateDto createDto = buildTraineeCreateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

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

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldUpdateTraineeAndMapToDto() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeMapper.toEntity(updateDto, DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.updateTrainee(DEFAULT_TRAINEE_ID, updateDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeMapper).toEntity(updateDto, DEFAULT_TRAINEE_ID);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTrainee() {
        TraineeUpdateDto updateDto = buildTraineeUpdateDto();
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);

        when(traineeMapper.toEntity(updateDto, DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTrainee(DEFAULT_TRAINEE_ID, updateDto));

        verify(traineeMapper).toEntity(updateDto, DEFAULT_TRAINEE_ID);
        verify(traineeService).updateTrainee(trainee);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainee(null, buildTraineeUpdateDto()));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTraineeDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainee(DEFAULT_TRAINEE_ID, null));

        assertEquals("TraineeUpdateDto cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldDeleteTrainee() {
        when(traineeService.deleteTrainee(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean actual = facade.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertTrue(actual);
        verify(traineeService).deleteTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonExistentTrainee() {
        when(traineeService.deleteTrainee(DEFAULT_TRAINEE_ID)).thenReturn(false);

        boolean actual = facade.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertFalse(actual);
        verify(traineeService).deleteTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deleteTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldGetTraineeAndMapToDto() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeService.getTrainee(DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.getTrainee(DEFAULT_TRAINEE_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getTrainee(DEFAULT_TRAINEE_ID);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTrainee() {
        when(traineeService.getTrainee(DEFAULT_TRAINEE_ID)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTrainee(DEFAULT_TRAINEE_ID));

        verify(traineeService).getTrainee(DEFAULT_TRAINEE_ID);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldGetAllTraineesAndReturnEmptyList() {
        when(traineeService.getAllTrainees()).thenReturn(List.of());
        when(traineeMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TraineeResponseDto> actual = facade.getAllTrainees();

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
    void shouldCheckTraineeUsernameAndPasswordMatch() {
        String username = "username";
        String password = "password";
        when(traineeService.doesUsernameAndPasswordMatch(username, password)).thenReturn(true);

        boolean actual = facade.doesTraineeUsernameAndPasswordMatch(username, password);

        assertTrue(actual);
        verify(traineeService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldReturnFalseWhenTraineeUsernameAndPasswordDoNotMatch() {
        String username = "username";
        String password = "password";
        when(traineeService.doesUsernameAndPasswordMatch(username, password)).thenReturn(false);

        boolean actual = facade.doesTraineeUsernameAndPasswordMatch(username, password);

        assertFalse(actual);
        verify(traineeService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenCheckingTraineeMatch() {
        String username = "username";
        String password = "password";
        when(traineeService.doesUsernameAndPasswordMatch(username, password)).thenThrow(EntityNotFoundException.forUsername("Trainee", username));

        assertThrows(EntityNotFoundException.class, () -> facade.doesTraineeUsernameAndPasswordMatch(username, password));

        verify(traineeService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldThrowNullPointerWhenCheckingTraineeMatchWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.doesTraineeUsernameAndPasswordMatch(null, "password"));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenCheckingTraineeMatchWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.doesTraineeUsernameAndPasswordMatch("username", null));

        assertEquals("Password cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldUpdateTraineeTrainersAndMapToDto() {
        List<Long> trainerIds = List.of(1L, 2L);
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto expected = buildTraineeResponseDto();

        when(traineeService.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(expected);

        TraineeResponseDto actual = facade.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTraineeTrainers() {
        List<Long> trainerIds = List.of(1L);
        when(traineeService.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds)).thenThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds));

        verify(traineeService).updateTraineeTrainers(DEFAULT_TRAINEE_ID, trainerIds);
        verifyNoInteractions(traineeMapper);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeTrainersWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineeTrainers(null, List.of(1L)));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineeTrainersWithNullTrainerIds() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineeTrainers(DEFAULT_TRAINEE_ID, null));

        assertEquals("Trainer IDs cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldUpdateTraineePassword() {
        String newPassword = "newPassword";

        facade.updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword);

        verify(traineeService).updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTraineePassword() {
        String newPassword = "newPassword";
        doThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID))
                .when(traineeService).updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword);

        assertThrows(EntityNotFoundException.class, () -> facade.updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword));

        verify(traineeService).updateTraineePassword(DEFAULT_TRAINEE_ID, newPassword);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineePasswordWithNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineePassword(null, "password"));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTraineePasswordWithNullNewPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTraineePassword(DEFAULT_TRAINEE_ID, null));

        assertEquals("New password cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldActivateTrainee() {
        facade.activateTrainee(DEFAULT_TRAINEE_ID);

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenActivatingTrainee() {
        doThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID))
                .when(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.activateTrainee(DEFAULT_TRAINEE_ID));

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenActivatingTrainee() {
        doThrow(new IllegalStateException("Already active"))
                .when(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(IllegalStateException.class, () -> facade.activateTrainee(DEFAULT_TRAINEE_ID));

        verify(traineeService).activateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenActivatingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.activateTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());
        verifyNoInteractions(traineeService);
    }

    @Test
    void shouldDeactivateTrainee() {
        facade.deactivateTrainee(DEFAULT_TRAINEE_ID);

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenDeactivatingTrainee() {
        doThrow(EntityNotFoundException.forId("Trainee", DEFAULT_TRAINEE_ID))
                .when(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.deactivateTrainee(DEFAULT_TRAINEE_ID));

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenDeactivatingTrainee() {
        doThrow(new IllegalStateException("Already deactivated"))
                .when(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);

        assertThrows(IllegalStateException.class, () -> facade.deactivateTrainee(DEFAULT_TRAINEE_ID));

        verify(traineeService).deactivateTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deactivateTrainee(null));

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

        when(trainerMapper.toEntity(createDto)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toCreateResponseDto(trainer)).thenReturn(expected);

        TrainerCreateResponseDto actual = facade.createTrainer(createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerMapper).toEntity(createDto);
        verify(trainerService).createTrainer(trainer);
        verify(trainerMapper).toCreateResponseDto(trainer);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTrainer() {
        TrainerCreateDto createDto = buildTrainerCreateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

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

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldUpdateTrainerAndMapToDto() {
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto expected = buildTrainerResponseDto();

        when(trainerMapper.toEntity(updateDto, DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerResponseDto actual = facade.updateTrainer(DEFAULT_TRAINER_ID, updateDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerMapper).toEntity(updateDto, DEFAULT_TRAINER_ID);
        verify(trainerService).updateTrainer(trainer);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTrainer() {
        TrainerUpdateDto updateDto = buildTrainerUpdateDto();
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);

        when(trainerMapper.toEntity(updateDto, DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.updateTrainer(DEFAULT_TRAINER_ID, updateDto));

        verify(trainerMapper).toEntity(updateDto, DEFAULT_TRAINER_ID);
        verify(trainerService).updateTrainer(trainer);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainer(null, buildTrainerUpdateDto()));

        assertEquals("Trainer ID cannot be null", exception.getMessage());

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingWithNullTrainerDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainer(DEFAULT_TRAINER_ID, null));

        assertEquals("TrainerUpdateDto cannot be null", exception.getMessage());

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    void shouldGetTrainerAndMapToDto() {
        Trainer trainer = buildTrainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto expected = buildTrainerResponseDto();

        when(trainerService.getTrainer(DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(expected);

        TrainerResponseDto actual = facade.getTrainer(DEFAULT_TRAINER_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getTrainer(DEFAULT_TRAINER_ID);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTrainer() {
        when(trainerService.getTrainer(DEFAULT_TRAINER_ID)).thenThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTrainer(DEFAULT_TRAINER_ID));

        verify(trainerService).getTrainer(DEFAULT_TRAINER_ID);
        verifyNoInteractions(trainerMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainer(null));

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

        List<TrainerResponseDto> actual = facade.getAllTrainers();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainerService).getAllTrainers();
        verify(trainerMapper).toDtoList(trainers);
    }

    @Test
    void shouldGetAllTrainersAndReturnEmptyList() {
        when(trainerService.getAllTrainers()).thenReturn(List.of());
        when(trainerMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TrainerResponseDto> actual = facade.getAllTrainers();

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
    void shouldCheckTrainerUsernameAndPasswordMatch() {
        String username = "username";
        String password = "password";
        when(trainerService.doesUsernameAndPasswordMatch(username, password)).thenReturn(true);

        boolean actual = facade.doesTrainerUsernameAndPasswordMatch(username, password);

        assertTrue(actual);
        verify(trainerService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldReturnFalseWhenTrainerUsernameAndPasswordDoNotMatch() {
        String username = "username";
        String password = "password";
        when(trainerService.doesUsernameAndPasswordMatch(username, password)).thenReturn(false);

        boolean actual = facade.doesTrainerUsernameAndPasswordMatch(username, password);

        assertFalse(actual);
        verify(trainerService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenCheckingTrainerMatch() {
        String username = "username";
        String password = "password";
        when(trainerService.doesUsernameAndPasswordMatch(username, password)).thenThrow(EntityNotFoundException.forUsername("Trainer", username));

        assertThrows(EntityNotFoundException.class, () -> facade.doesTrainerUsernameAndPasswordMatch(username, password));

        verify(trainerService).doesUsernameAndPasswordMatch(username, password);
    }

    @Test
    void shouldThrowNullPointerWhenCheckingTrainerMatchWithNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.doesTrainerUsernameAndPasswordMatch(null, "password"));

        assertEquals("Username cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenCheckingTrainerMatchWithNullPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.doesTrainerUsernameAndPasswordMatch("username", null));

        assertEquals("Password cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldUpdateTrainerPassword() {
        String newPassword = "newPassword";

        facade.updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword);

        verify(trainerService).updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenUpdatingTrainerPassword() {
        String newPassword = "newPassword";
        doThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID))
                .when(trainerService).updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword);

        assertThrows(EntityNotFoundException.class, () -> facade.updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword));

        verify(trainerService).updateTrainerPassword(DEFAULT_TRAINER_ID, newPassword);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerPasswordWithNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainerPassword(null, "password"));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenUpdatingTrainerPasswordWithNullNewPassword() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.updateTrainerPassword(DEFAULT_TRAINER_ID, null));

        assertEquals("New password cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldActivateTrainer() {
        facade.activateTrainer(DEFAULT_TRAINER_ID);

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenActivatingTrainer() {
        doThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID))
                .when(trainerService).activateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.activateTrainer(DEFAULT_TRAINER_ID));

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenActivatingTrainer() {
        doThrow(new IllegalStateException("Already active"))
                .when(trainerService).activateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(IllegalStateException.class, () -> facade.activateTrainer(DEFAULT_TRAINER_ID));

        verify(trainerService).activateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenActivatingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.activateTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldDeactivateTrainer() {
        facade.deactivateTrainer(DEFAULT_TRAINER_ID);

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenDeactivatingTrainer() {
        doThrow(EntityNotFoundException.forId("Trainer", DEFAULT_TRAINER_ID))
                .when(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(EntityNotFoundException.class, () -> facade.deactivateTrainer(DEFAULT_TRAINER_ID));

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldPropagateIllegalStateExceptionWhenDeactivatingTrainer() {
        doThrow(new IllegalStateException("Already deactivated"))
                .when(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);

        assertThrows(IllegalStateException.class, () -> facade.deactivateTrainer(DEFAULT_TRAINER_ID));

        verify(trainerService).deactivateTrainer(DEFAULT_TRAINER_ID);
    }

    @Test
    void shouldThrowNullPointerWhenDeactivatingNullTrainerId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.deactivateTrainer(null));

        assertEquals("Trainer ID cannot be null", exception.getMessage());
        verifyNoInteractions(trainerService);
    }

    @Test
    void shouldCreateTrainingAndReturnResponseDto() {
        TrainingCreateDto createDto = buildTrainingCreateDto();
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto expected = buildTrainingResponseDto();

        when(trainingMapper.toEntity(createDto)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expected);

        TrainingResponseDto actual = facade.createTraining(createDto);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingMapper).toEntity(createDto);
        verify(trainingService).createTraining(training);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void shouldPropagateExceptionWhenCreatingTraining() {
        TrainingCreateDto createDto = buildTrainingCreateDto();
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);

        when(trainingMapper.toEntity(createDto)).thenReturn(training);
        when(trainingService.createTraining(training)).thenThrow(new RuntimeException("Service error"));

        assertThrows(RuntimeException.class, () -> facade.createTraining(createDto));

        verify(trainingService).createTraining(training);
        verifyNoInteractions(traineeService, trainerService);
    }

    @Test
    void shouldThrowNullPointerWhenCreatingNullTrainingDto() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.createTraining(null));

        assertEquals("TrainingCreateDto cannot be null", exception.getMessage());

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    void shouldGetTrainingAndMapToDto() {
        Training training = buildTrainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto expected = buildTrainingResponseDto();

        when(trainingService.getTraining(DEFAULT_TRAINING_ID)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(expected);

        TrainingResponseDto actual = facade.getTraining(DEFAULT_TRAINING_ID);

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingService).getTraining(DEFAULT_TRAINING_ID);
        verify(trainingMapper).toDto(training);
    }

    @Test
    void shouldPropagateEntityNotFoundExceptionWhenGettingTraining() {
        when(trainingService.getTraining(DEFAULT_TRAINING_ID)).thenThrow(EntityNotFoundException.forId("Training", DEFAULT_TRAINING_ID));

        assertThrows(EntityNotFoundException.class, () -> facade.getTraining(DEFAULT_TRAINING_ID));

        verify(trainingService).getTraining(DEFAULT_TRAINING_ID);
        verifyNoInteractions(trainingMapper);
    }

    @Test
    void shouldThrowNullPointerWhenGettingNullTrainingId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTraining(null));

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

        List<TrainingResponseDto> actual = facade.getAllTrainings();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).toDtoList(trainings);
    }

    @Test
    void shouldGetAllTrainingsAndReturnEmptyList() {
        when(trainingService.getAllTrainings()).thenReturn(List.of());
        when(trainingMapper.toDtoList(List.of())).thenReturn(List.of());

        List<TrainingResponseDto> actual = facade.getAllTrainings();

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).toDtoList(List.of());
    }

}
