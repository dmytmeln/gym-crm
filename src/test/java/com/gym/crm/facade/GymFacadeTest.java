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
import static com.gym.crm.factory.TraineeTestFactory.traineeCreateDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeCreateResponseDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeResponseDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeUpdateDto;
import static com.gym.crm.factory.TraineeTestFactory.traineeWithId;
import static com.gym.crm.factory.TrainerTestFactory.DEFAULT_TRAINER_ID;
import static com.gym.crm.factory.TrainerTestFactory.trainerCreateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerCreateResponseDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerResponseDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerUpdateDto;
import static com.gym.crm.factory.TrainerTestFactory.trainerWithId;
import static com.gym.crm.factory.TrainingTestFactory.DEFAULT_TRAINING_ID;
import static com.gym.crm.factory.TrainingTestFactory.trainingCreateDto;
import static com.gym.crm.factory.TrainingTestFactory.trainingResponseDto;
import static com.gym.crm.factory.TrainingTestFactory.trainingWithId;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymFacadeTest {

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

    private GymFacade gymFacade;

    @BeforeEach
    public void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
        gymFacade.setTraineeMapper(traineeMapper);
        gymFacade.setTrainerMapper(trainerMapper);
        gymFacade.setTrainingMapper(trainingMapper);
    }

    @Test
    public void shouldCreateTraineeAndReturnResponseDto() {
        TraineeCreateDto createDto = traineeCreateDto();
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);
        TraineeCreateResponseDto responseDto = traineeCreateResponseDto();
        when(traineeMapper.toEntity(createDto)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toCreateResponseDto(trainee)).thenReturn(responseDto);

        TraineeCreateResponseDto result = gymFacade.createTrainee(createDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(traineeMapper).toEntity(createDto);
        verify(traineeService).createTrainee(trainee);
        verify(traineeMapper).toCreateResponseDto(trainee);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTraineeDto() {
        assertThrows(NullPointerException.class, () -> gymFacade.createTrainee(null));

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    public void shouldUpdateTraineeAndMapToDto() {
        TraineeUpdateDto updateDto = traineeUpdateDto();
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto responseDto = traineeResponseDto();
        when(traineeMapper.toEntity(updateDto, DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(responseDto);

        TraineeResponseDto result = gymFacade.updateTrainee(DEFAULT_TRAINEE_ID, updateDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(traineeMapper).toEntity(updateDto, DEFAULT_TRAINEE_ID);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingWithNullTraineeId() {
        assertThrows(NullPointerException.class, () -> gymFacade.updateTrainee(null, traineeUpdateDto()));

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingWithNullTraineeDto() {
        assertThrows(NullPointerException.class, () -> gymFacade.updateTrainee(DEFAULT_TRAINEE_ID, null));

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    public void shouldDeleteTrainee() {
        when(traineeService.deleteTrainee(DEFAULT_TRAINEE_ID)).thenReturn(true);

        boolean result = gymFacade.deleteTrainee(DEFAULT_TRAINEE_ID);

        assertTrue(result);
        verify(traineeService).deleteTrainee(DEFAULT_TRAINEE_ID);
    }

    @Test
    public void shouldThrowNullPointerWhenDeletingNullTraineeId() {
        assertThrows(NullPointerException.class, () -> gymFacade.deleteTrainee(null));

        verifyNoInteractions(traineeService);
    }

    @Test
    public void shouldGetTraineeAndMapToDto() {
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto responseDto = traineeResponseDto();

        when(traineeService.getTrainee(DEFAULT_TRAINEE_ID)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(responseDto);

        TraineeResponseDto result = gymFacade.getTrainee(DEFAULT_TRAINEE_ID);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(traineeService).getTrainee(DEFAULT_TRAINEE_ID);
        verify(traineeMapper).toDto(trainee);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTraineeId() {
        assertThrows(NullPointerException.class, () -> gymFacade.getTrainee(null));

        verifyNoInteractions(traineeMapper, traineeService);
    }

    @Test
    public void shouldGetAllTraineesAndMapToDtoList() {
        Trainee trainee = traineeWithId(DEFAULT_TRAINEE_ID);
        TraineeResponseDto responseDto = traineeResponseDto();
        List<Trainee> trainees = List.of(trainee);
        List<TraineeResponseDto> traineeDtos = List.of(responseDto);
        when(traineeService.getAllTrainees()).thenReturn(trainees);
        when(traineeMapper.toDtoList(trainees)).thenReturn(traineeDtos);

        List<TraineeResponseDto> result = gymFacade.getAllTrainees();

        assertNotNull(result);
        assertEquals(traineeDtos, result);
        verify(traineeService).getAllTrainees();
        verify(traineeMapper).toDtoList(trainees);
    }

    @Test
    public void shouldCreateTrainerAndReturnResponseDto() {
        TrainerCreateDto createDto = trainerCreateDto();
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);
        TrainerCreateResponseDto responseDto = trainerCreateResponseDto();
        when(trainerMapper.toEntity(createDto)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toCreateResponseDto(trainer)).thenReturn(responseDto);

        TrainerCreateResponseDto result = gymFacade.createTrainer(createDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(trainerMapper).toEntity(createDto);
        verify(trainerService).createTrainer(trainer);
        verify(trainerMapper).toCreateResponseDto(trainer);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTrainerDto() {
        assertThrows(NullPointerException.class, () -> gymFacade.createTrainer(null));

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    public void shouldUpdateTrainerAndMapToDto() {
        TrainerUpdateDto updateDto = trainerUpdateDto();
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto responseDto = trainerResponseDto();
        when(trainerMapper.toEntity(updateDto, DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(responseDto);

        TrainerResponseDto result = gymFacade.updateTrainer(DEFAULT_TRAINER_ID, updateDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(trainerMapper).toEntity(updateDto, DEFAULT_TRAINER_ID);
        verify(trainerService).updateTrainer(trainer);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingWithNullTrainerId() {
        assertThrows(NullPointerException.class, () -> gymFacade.updateTrainer(null, trainerUpdateDto()));

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    public void shouldThrowNullPointerWhenUpdatingWithNullTrainerDto() {
        assertThrows(NullPointerException.class, () -> gymFacade.updateTrainer(DEFAULT_TRAINER_ID, null));

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    public void shouldGetTrainerAndMapToDto() {
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto responseDto = trainerResponseDto();
        when(trainerService.getTrainer(DEFAULT_TRAINER_ID)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(responseDto);

        TrainerResponseDto result = gymFacade.getTrainer(DEFAULT_TRAINER_ID);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(trainerService).getTrainer(DEFAULT_TRAINER_ID);
        verify(trainerMapper).toDto(trainer);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTrainerId() {
        assertThrows(NullPointerException.class, () -> gymFacade.getTrainer(null));

        verifyNoInteractions(trainerMapper, trainerService);
    }

    @Test
    public void shouldGetAllTrainersAndMapToDtoList() {
        Trainer trainer = trainerWithId(DEFAULT_TRAINER_ID);
        TrainerResponseDto responseDto = trainerResponseDto();
        List<Trainer> trainers = List.of(trainer);
        List<TrainerResponseDto> trainerDtos = List.of(responseDto);
        when(trainerService.getAllTrainers()).thenReturn(trainers);
        when(trainerMapper.toDtoList(trainers)).thenReturn(trainerDtos);

        List<TrainerResponseDto> result = gymFacade.getAllTrainers();

        assertNotNull(result);
        assertEquals(trainerDtos, result);
        verify(trainerService).getAllTrainers();
        verify(trainerMapper).toDtoList(trainers);
    }

    @Test
    public void shouldCreateTrainingAndReturnResponseDto() {
        TrainingCreateDto createDto = trainingCreateDto();
        Training training = trainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = trainingResponseDto();
        when(trainingMapper.toEntity(createDto)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(responseDto);

        TrainingResponseDto result = gymFacade.createTraining(createDto);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(trainingMapper).toEntity(createDto);
        verify(trainingService).createTraining(training);
        verify(trainingMapper).toDto(training);
    }

    @Test
    public void shouldThrowNullPointerWhenCreatingNullTrainingDto() {
        assertThrows(NullPointerException.class, () -> gymFacade.createTraining(null));

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    public void shouldGetTrainingAndMapToDto() {
        Training training = trainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = trainingResponseDto();
        when(trainingService.getTraining(DEFAULT_TRAINING_ID)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(responseDto);

        TrainingResponseDto result = gymFacade.getTraining(DEFAULT_TRAINING_ID);

        assertNotNull(result);
        assertEquals(responseDto, result);
        verify(trainingService).getTraining(DEFAULT_TRAINING_ID);
        verify(trainingMapper).toDto(training);
    }

    @Test
    public void shouldThrowNullPointerWhenGettingNullTrainingId() {
        assertThrows(NullPointerException.class, () -> gymFacade.getTraining(null));

        verifyNoInteractions(trainingMapper, trainingService);
    }

    @Test
    public void shouldGetAllTrainingsAndMapToDtoList() {
        Training training = trainingWithId(DEFAULT_TRAINING_ID);
        TrainingResponseDto responseDto = trainingResponseDto();
        List<Training> trainings = List.of(training);
        List<TrainingResponseDto> trainingDtos = List.of(responseDto);
        when(trainingService.getAllTrainings()).thenReturn(trainings);
        when(trainingMapper.toDtoList(trainings)).thenReturn(trainingDtos);

        List<TrainingResponseDto> result = gymFacade.getAllTrainings();

        assertNotNull(result);
        assertEquals(trainingDtos, result);
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).toDtoList(trainings);
    }

}
