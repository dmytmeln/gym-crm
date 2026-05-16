package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeRestControllerTest {

    private static final String BASE_PATH = "/api/v1/trainees";
    private static final String USERNAME = "liam.miller";
    private static final String FIRST_NAME = "Liam";
    private static final String LAST_NAME = "Miller";
    private static final String SPECIALIZATION = "Yoga";

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private GymFacade facade;

    @InjectMocks
    private TraineeRestController controller;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validatorFactoryBean)
                .build();
    }

    @Test
    void shouldRegisterTraineeWhenRequestIsValid() throws Exception {
        TraineeCreateRequest validRequest = buildTraineeCreateRequest(FIRST_NAME, LAST_NAME);
        TraineeCreateResponse response = new TraineeCreateResponse()
                .username(USERNAME)
                .password("password123");

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        String content = mockMvc.perform(post(BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeCreateResponse actual = objectMapper.readValue(content, TraineeCreateResponse.class);
        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getPassword()).isEqualTo("password123");
        verify(facade).createTrainee(validRequest);
    }

    @Test
    void shouldFailRegisterTraineeWhenFirstNameIsNull() throws Exception {
        TraineeCreateRequest invalidRequest = buildTraineeCreateRequest(null, LAST_NAME);

        mockMvc.perform(post(BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTraineeWhenLastNameIsNull() throws Exception {
        TraineeCreateRequest invalidRequest = buildTraineeCreateRequest(FIRST_NAME, null);

        mockMvc.perform(post(BASE_PATH + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldGetTraineeProfileWhenUsernameIsValid() throws Exception {
        TraineeGetResponse response = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        String content = mockMvc.perform(get(BASE_PATH + "/{username}", USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeGetResponse actual = objectMapper.readValue(content, TraineeGetResponse.class);
        assertThat(actual.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actual.getIsActive()).isTrue();
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void shouldGetAvailableTrainersWhenUsernameIsValid() throws Exception {
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username("ricardo.milos")
                .firstName("Ricardo")
                .lastName("Milos")
                .specialization(SPECIALIZATION);

        when(facade.getAvailableTrainersForTrainee(USERNAME)).thenReturn(List.of(trainerResponse));

        String content = mockMvc.perform(get(BASE_PATH + "/{username}/available-trainers", USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AssignedTrainerResponse> actual = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AssignedTrainerResponse.class));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getUsername()).isEqualTo("ricardo.milos");
        assertThat(actual.get(0).getFirstName()).isEqualTo("Ricardo");
        assertThat(actual.get(0).getSpecialization()).isEqualTo(SPECIALIZATION);
        verify(facade).getAvailableTrainersForTrainee(USERNAME);
    }

    @Test
    void shouldGetTraineeTrainingsWithAllFilters() throws Exception {
        GetTraineeTrainingResponse trainingResponse = new GetTraineeTrainingResponse()
                .trainingName("Morning Cardio")
                .trainingType("Cardio")
                .trainerName("ronnie.coleman")
                .trainingDate(LocalDate.of(2025, 7, 20))
                .trainingDuration(55);

        when(facade.getTraineeTrainingsByCriteria(eq(USERNAME), any())).thenReturn(List.of(trainingResponse));

        String content = mockMvc.perform(get(BASE_PATH + "/{username}/trainings", USERNAME)
                        .param("fromDate", "2025-07-01")
                        .param("toDate", "2025-07-31")
                        .param("trainerName", "ronnie.coleman")
                        .param("trainingType", "Cardio"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<GetTraineeTrainingResponse> actual = objectMapper.readValue(content,
                objectMapper.getTypeFactory().constructCollectionType(List.class, GetTraineeTrainingResponse.class));
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0).getTrainingName()).isEqualTo("Morning Cardio");
        assertThat(actual.get(0).getTrainingType()).isEqualTo("Cardio");
        assertThat(actual.get(0).getTrainerName()).isEqualTo("ronnie.coleman");
        verify(facade).getTraineeTrainingsByCriteria(eq(USERNAME), any());
    }

    @Test
    void shouldGetTraineeTrainingsWithNoOptionalFilters() throws Exception {
        when(facade.getTraineeTrainingsByCriteria(eq(USERNAME), any())).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH + "/{username}/trainings", USERNAME))
                .andExpect(status().isOk());

        verify(facade).getTraineeTrainingsByCriteria(eq(USERNAME), any());
    }

    @Test
    void shouldUpdateTraineeProfileWhenRequestIsValid() throws Exception {
        TraineeUpdateRequest validRequest = buildTraineeUpdateRequest(FIRST_NAME, LAST_NAME);
        TraineeUpdateResponse response = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(facade.updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class))).thenReturn(response);

        String content = mockMvc.perform(put(BASE_PATH + "/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeUpdateResponse actual = objectMapper.readValue(content, TraineeUpdateResponse.class);
        assertThat(actual.getUsername()).isEqualTo(USERNAME);
        assertThat(actual.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actual.getIsActive()).isTrue();
        verify(facade).updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class));
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenFirstNameIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = buildTraineeUpdateRequest(null, LAST_NAME);

        mockMvc.perform(put(BASE_PATH + "/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenLastNameIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = buildTraineeUpdateRequest(FIRST_NAME, null);

        mockMvc.perform(put(BASE_PATH + "/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenIsActiveIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = new TraineeUpdateRequest();
        invalidRequest.firstName(FIRST_NAME);
        invalidRequest.lastName(LAST_NAME);

        mockMvc.perform(put(BASE_PATH + "/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldUpdateTraineeTrainersWhenRequestIsValid() throws Exception {
        TraineeAssignedTrainersUpdateRequest validRequest = buildTraineeAssignedTrainersUpdateRequest(List.of("ricardo.milos"));
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username("ricardo.milos")
                .firstName("Ricardo")
                .lastName("Milos")
                .specialization(SPECIALIZATION);
        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse()
                .trainers(List.of(trainerResponse));

        when(facade.updateTraineeTrainers(eq(USERNAME), any())).thenReturn(response);

        String content = mockMvc.perform(put(BASE_PATH + "/{username}/trainers", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeAssignedTrainersUpdateResponse actual = objectMapper.readValue(content, TraineeAssignedTrainersUpdateResponse.class);
        assertThat(actual.getTrainers()).hasSize(1);
        assertThat(actual.getTrainers().get(0).getUsername()).isEqualTo("ricardo.milos");
        assertThat(actual.getTrainers().get(0).getSpecialization()).isEqualTo(SPECIALIZATION);
        verify(facade).updateTraineeTrainers(eq(USERNAME), any());
    }

    @Test
    void shouldFailUpdateTraineeTrainersWhenTrainerUsernamesIsNull() throws Exception {
        TraineeAssignedTrainersUpdateRequest invalidRequest = new TraineeAssignedTrainersUpdateRequest();
        invalidRequest.setTrainerUsernames(null);

        mockMvc.perform(put(BASE_PATH + "/{username}/trainers", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeTrainersWhenTrainerUsernamesIsEmpty() throws Exception {
        TraineeAssignedTrainersUpdateRequest invalidRequest = buildTraineeAssignedTrainersUpdateRequest(List.of());

        mockMvc.perform(put(BASE_PATH + "/{username}/trainers", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldChangeTraineeActivationStatusWhenRequestIsValid() throws Exception {
        ActivationStatusRequest validRequest = new ActivationStatusRequest(true);

        mockMvc.perform(patch(BASE_PATH + "/{username}/activation", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).updateTraineeActivationStatus(eq(USERNAME), any());
    }

    @Test
    void shouldFailChangeTraineeActivationStatusWhenIsActiveIsNull() throws Exception {
        ActivationStatusRequest invalidRequest = new ActivationStatusRequest();
        invalidRequest.setIsActive(null);

        mockMvc.perform(patch(BASE_PATH + "/{username}/activation", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(facade);
    }

    @Test
    void shouldDeleteTraineeProfileWhenUsernameIsValid() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/{username}", USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    private TraineeCreateRequest buildTraineeCreateRequest(String firstName, String lastName) {
        return new TraineeCreateRequest()
                .firstName(firstName)
                .lastName(lastName);
    }

    private TraineeUpdateRequest buildTraineeUpdateRequest(String firstName, String lastName) {
        return new TraineeUpdateRequest()
                .firstName(firstName)
                .lastName(lastName)
                .isActive(true);
    }

    private TraineeAssignedTrainersUpdateRequest buildTraineeAssignedTrainersUpdateRequest(List<String> trainerUsernames) {
        return new TraineeAssignedTrainersUpdateRequest()
                .trainerUsernames(trainerUsernames);
    }

}
