package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerRestControllerTest {

    private static final String BASE_PATH = "/api/v1";
    private static final String USERNAME = "liam.miller";
    private static final String FIRST_NAME = "Liam";
    private static final String LAST_NAME = "Miller";
    private static final String SPECIALIZATION = "Yoga";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TrainerRestController controller;

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
    void shouldRegisterTrainerWhenRequestIsValid() throws Exception {
        TrainerCreateRequest validRequest = buildTrainerCreateRequest(FIRST_NAME, LAST_NAME, SPECIALIZATION);
        TrainerCreateResponse response = new TrainerCreateResponse()
                .username(USERNAME)
                .password("password123");

        when(facade.createTrainer(any(TrainerCreateRequest.class))).thenReturn(response);

        String content = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TrainerCreateResponse actual = objectMapper.readValue(content, TrainerCreateResponse.class);
        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getPassword()).isEqualTo("password123");
        verify(facade).createTrainer(validRequest);
    }

    @Test
    void shouldFailRegisterTrainerWhenFirstNameIsNull() throws Exception {
        TrainerCreateRequest invalidRequest = buildTrainerCreateRequest(null, LAST_NAME, SPECIALIZATION);

        mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTrainerWhenLastNameIsNull() throws Exception {
        TrainerCreateRequest invalidRequest = buildTrainerCreateRequest(FIRST_NAME, null, SPECIALIZATION);

        mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTrainerWhenSpecializationIsNull() throws Exception {
        TrainerCreateRequest invalidRequest = buildTrainerCreateRequest(FIRST_NAME, LAST_NAME, null);

        mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldGetTrainerProfileWhenUsernameIsValid() throws Exception {
        TrainerGetResponse response = new TrainerGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true);

        when(facade.getTrainerByUsername(USERNAME)).thenReturn(response);

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}", USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TrainerGetResponse actual = objectMapper.readValue(content, TrainerGetResponse.class);
        assertThat(actual.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getSpecialization()).isEqualTo(SPECIALIZATION);
        assertThat(actual.getIsActive()).isTrue();
        verify(facade).getTrainerByUsername(USERNAME);
    }

    @Test
    void shouldGetTrainerTrainingsWithAllFilters() throws Exception {
        GetTrainerTrainingResponse trainingResponse = new GetTrainerTrainingResponse()
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .traineeName("billy.herrington")
                .trainingDate(LocalDate.of(2025, 7, 20))
                .trainingDuration(55);

        when(facade.getTrainerTrainings(eq(USERNAME), any())).thenReturn(List.of(trainingResponse));

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}/trainings", USERNAME)
                        .param("fromDate", "2025-07-01")
                        .param("toDate", "2025-07-31")
                        .param("traineeName", "billy.herrington"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<GetTrainerTrainingResponse> actual = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, GetTrainerTrainingResponse.class));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Yoga");
        assertThat(actual.get(0).getTrainingType()).isEqualTo("Yoga");
        assertThat(actual.get(0).getTraineeName()).isEqualTo("billy.herrington");
        verify(facade).getTrainerTrainings(eq(USERNAME), any());
    }

    @Test
    void shouldGetTrainerTrainingsWithNoOptionalFilters() throws Exception {
        when(facade.getTrainerTrainings(eq(USERNAME), any())).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH + "/trainers/{username}/trainings", USERNAME))
                .andExpect(status().isOk());

        verify(facade).getTrainerTrainings(eq(USERNAME), any());
    }

    @Test
    void shouldUpdateTrainerProfileWhenRequestIsValid() throws Exception {
        TrainerUpdateRequest validRequest = buildTrainerUpdateRequest(FIRST_NAME, LAST_NAME);
        TrainerUpdateResponse response = new TrainerUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .specialization(SPECIALIZATION)
                .isActive(true);

        when(facade.updateTrainer(eq(USERNAME), any(TrainerUpdateRequest.class))).thenReturn(response);

        String content = mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TrainerUpdateResponse actual = objectMapper.readValue(content, TrainerUpdateResponse.class);
        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getIsActive()).isTrue();
        verify(facade).updateTrainer(eq(USERNAME), any(TrainerUpdateRequest.class));
    }

    @Test
    void shouldFailUpdateTrainerProfileWhenFirstNameIsNull() throws Exception {
        TrainerUpdateRequest invalidRequest = buildTrainerUpdateRequest(null, LAST_NAME);

        mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTrainerProfileWhenLastNameIsNull() throws Exception {
        TrainerUpdateRequest invalidRequest = buildTrainerUpdateRequest(FIRST_NAME, null);

        mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTrainerProfileWhenIsActiveIsNull() throws Exception {
        TrainerUpdateRequest invalidRequest = new TrainerUpdateRequest();
        invalidRequest.firstName(FIRST_NAME);
        invalidRequest.lastName(LAST_NAME);

        mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldChangeTrainerActivationStatusWhenRequestIsValid() throws Exception {
        ActivationStatusRequest validRequest = new ActivationStatusRequest(true);

        mockMvc.perform(patch(BASE_PATH + "/trainers/{username}/activation", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).updateTrainerActivationStatus(USERNAME, true);
    }

    @Test
    void shouldFailChangeTrainerActivationStatusWhenIsActiveIsNull() throws Exception {
        ActivationStatusRequest invalidRequest = new ActivationStatusRequest();
        invalidRequest.setIsActive(null);

        mockMvc.perform(patch(BASE_PATH + "/trainers/{username}/activation", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    private TrainerCreateRequest buildTrainerCreateRequest(String firstName, String lastName, String specialization) {
        return new TrainerCreateRequest()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization);
    }

    private TrainerUpdateRequest buildTrainerUpdateRequest(String firstName, String lastName) {
        return new TrainerUpdateRequest()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true);
    }

}
