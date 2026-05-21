package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.GetTrainerTrainingResponse;
import com.gia.openapi.model.TrainerCreateRequest;
import com.gia.openapi.model.TrainerCreateResponse;
import com.gia.openapi.model.TrainerGetResponse;
import com.gia.openapi.model.TrainerUpdateRequest;
import com.gia.openapi.model.TrainerUpdateResponse;
import com.gym.crm.entity.EntityType;
import com.gym.crm.exception.ApiError;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.GlobalRestExceptionHandler;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.facade.GymFacade;
import com.gym.crm.security.AuthenticationException;
import org.hibernate.HibernateException;
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
import static org.mockito.Mockito.doThrow;
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
                .setControllerAdvice(new GlobalRestExceptionHandler())
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
        TrainerCreateRequest invalidRequestWithNullFirstName = buildTrainerCreateRequest(null, LAST_NAME, SPECIALIZATION);

        String content = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullFirstName)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: firstName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTrainerWhenLastNameIsNull() throws Exception {
        TrainerCreateRequest invalidRequestWithNullLastName = buildTrainerCreateRequest(FIRST_NAME, null, SPECIALIZATION);

        String content = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullLastName)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: lastName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTrainerWhenSpecializationIsNull() throws Exception {
        TrainerCreateRequest invalidRequestWithNullSpecialization = buildTrainerCreateRequest(FIRST_NAME, LAST_NAME, null);

        String content = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullSpecialization)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: specialization: must not be null");
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
    void shouldReturn404WhenTrainerNotFound() throws Exception {
        EntityNotFoundException exception = EntityNotFoundException.forUsername(EntityType.TRAINER, USERNAME);

        doThrow(exception)
                .when(facade).getTrainerByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}", USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.NOT_FOUND.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(
                "%s: %s".formatted(ApiError.NOT_FOUND.getMessage(), exception.getMessage()));
    }

    @Test
    void shouldReturn401WhenAuthenticationFailsOnGetTrainerProfile() throws Exception {
        doThrow(new AuthenticationException("User is not authenticated"))
                .when(facade).getTrainerByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}", USERNAME))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.AUTHENTICATION.getMessage());
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccursOnGetTrainerProfile() throws Exception {
        doThrow(new RuntimeException("Unexpected failure"))
                .when(facade).getTrainerByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}", USERNAME))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.SERVICE.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.SERVICE.getMessage());
    }

    @Test
    void shouldReturn500WhenHibernateExceptionOccursOnGetTrainerProfile() throws Exception {
        doThrow(new HibernateException("Database connectivity failure"))
                .when(facade).getTrainerByUsername(USERNAME);

        String content = mockMvc.perform(get(BASE_PATH + "/trainers/{username}", USERNAME))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.DATABASE.getCode());
        assertThat(error.getErrorMessage()).isEqualTo(ApiError.DATABASE.getMessage());
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
        TrainerUpdateRequest invalidRequestWithNullFirstName = buildTrainerUpdateRequest(null, LAST_NAME);

        String content = mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullFirstName)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: firstName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTrainerProfileWhenLastNameIsNull() throws Exception {
        TrainerUpdateRequest invalidRequestWithNullLastName = buildTrainerUpdateRequest(FIRST_NAME, null);

        String content = mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullLastName)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: lastName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTrainerProfileWhenIsActiveIsNull() throws Exception {
        TrainerUpdateRequest invalidRequestWithNullIsActive = new TrainerUpdateRequest();
        invalidRequestWithNullIsActive.firstName(FIRST_NAME);
        invalidRequestWithNullIsActive.lastName(LAST_NAME);

        String content = mockMvc.perform(put(BASE_PATH + "/trainers/{username}", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullIsActive)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: isActive: must not be null");
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
        ActivationStatusRequest invalidRequestWithNullIsActive = new ActivationStatusRequest(null);

        String content = mockMvc.perform(patch(BASE_PATH + "/trainers/{username}/activation", USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestWithNullIsActive)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("Validation error: isActive: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn400WhenValidationExceptionOccursDuringRegistration() throws Exception {
        TrainerCreateRequest validRequest = buildTrainerCreateRequest(FIRST_NAME, LAST_NAME, SPECIALIZATION);
        ValidationException exception = new ValidationException("Custom validation failed");

        doThrow(exception)
                .when(facade).createTrainer(any(TrainerCreateRequest.class));

        String content = mockMvc.perform(post(BASE_PATH + "/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse error = objectMapper.readValue(content, ErrorResponse.class);
        assertThat(error.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(error.getErrorMessage()).isEqualTo("%s: %s".formatted(ApiError.VALIDATION.getMessage(), exception.getMessage()));
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
