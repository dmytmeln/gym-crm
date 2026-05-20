package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.TrainingCreateRequest;
import com.gia.openapi.model.TrainingTypeResponse;
import com.gym.crm.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingRestControllerTest {

    private static final String BASE_PATH = "/api/v1";
    private static final String TRAINEE_USERNAME = "billy.herrington";
    private static final String TRAINER_USERNAME = "ricardo.milos";
    private static final String TRAINING_NAME = "Morning Cardio";
    private static final LocalDate TRAINING_DATE = LocalDate.of(2025, 7, 20);
    private static final Integer TRAINING_DURATION = 55;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TrainingRestController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validatorFactoryBean)
                .addPlaceholderValue("app.api.base-path", BASE_PATH)
                .build();
    }

    @Test
    void shouldAddTrainingWhenRequestIsValid() throws Exception {
        TrainingCreateRequest validRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).createTraining(TRAINER_USERNAME, validRequest);
    }

    @Test
    void shouldFailAddTrainingWhenTrainingNameIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, TRAINER_USERNAME, null, TRAINING_DATE, TRAINING_DURATION);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailAddTrainingWhenTrainingDateIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, null, TRAINING_DURATION);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailAddTrainingWhenTrainingDurationIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, TRAINING_DATE, null);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailAddTrainingWhenTraineeUsernameIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                null, TRAINER_USERNAME, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailAddTrainingWhenTrainerUsernameIsNull() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, null, TRAINING_NAME, TRAINING_DATE, TRAINING_DURATION);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailAddTrainingWhenTrainingDurationIsZero() throws Exception {
        TrainingCreateRequest invalidRequest = buildTrainingCreateRequest(
                TRAINEE_USERNAME, TRAINER_USERNAME, TRAINING_NAME, TRAINING_DATE, 0);

        mockMvc.perform(post(BASE_PATH + "/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldGetTrainingTypes() throws Exception {
        TrainingTypeResponse cardio = new TrainingTypeResponse()
                .id(1)
                .name("Cardio");
        TrainingTypeResponse yoga = new TrainingTypeResponse()
                .id(2)
                .name("Yoga");

        when(facade.getAllTrainingTypes(TRAINER_USERNAME)).thenReturn(List.of(cardio, yoga));

        String content = mockMvc.perform(get(BASE_PATH + "/trainings/types")
                        .param("username", TRAINER_USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TrainingTypeResponse> actual = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrainingTypeResponse.class));
        assertThat(actual).hasSize(2);
        assertThat(actual.get(0).getName()).isEqualTo("Cardio");
        assertThat(actual.get(1).getName()).isEqualTo("Yoga");
        verify(facade).getAllTrainingTypes(TRAINER_USERNAME);
    }

    @Test
    void shouldGetEmptyTrainingTypesList() throws Exception {
        when(facade.getAllTrainingTypes(TRAINER_USERNAME)).thenReturn(List.of());

        String content = mockMvc.perform(get(BASE_PATH + "/trainings/types")
                        .param("username", TRAINER_USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TrainingTypeResponse> actual = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, TrainingTypeResponse.class));
        assertThat(actual).isEmpty();
        verify(facade).getAllTrainingTypes(TRAINER_USERNAME);
    }

    private TrainingCreateRequest buildTrainingCreateRequest(
            String traineeUsername, String trainerUsername, String trainingName,
            LocalDate trainingDate, Integer trainingDuration) {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTraineeUsername(traineeUsername);
        request.setTrainerUsername(trainerUsername);
        request.setTrainingName(trainingName);
        request.setTrainingDate(trainingDate);
        request.setTrainingDuration(trainingDuration);

        return request;
    }

}
