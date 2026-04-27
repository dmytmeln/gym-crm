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
import com.gym.crm.model.Trainee;
import com.gym.crm.model.Trainer;
import com.gym.crm.model.Training;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    void shouldThrowNullPointerWhenGettingNullTraineeId() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> facade.getTrainee(null));

        assertEquals("Trainee ID cannot be null", exception.getMessage());

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    void shouldGetAllTraineesAndMapToDtoList() {
        Trainee trainee = buildTraineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto responseDto = buildTraineeResponseDto();
        List<Trainee> trainees = List.of(trainee);
        List<TraineeResponseDto> expected = List.of(responseDto);

        when(traineeService.getAllTrainees()).thenReturn(trainees);
        when(traineeMapper.toDtoList(trainees)).thenReturn(expected);

        List<TraineeResponseDto> actual = facade.getAllTrainees();

        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(traineeService).getAllTrainees();
        verify(traineeMapper).toDtoList(trainees);
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

}
