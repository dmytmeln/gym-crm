package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ActivationStatusRequest;
import com.gia.openapi.model.AssignedTrainerResponse;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.GetTraineeTrainingResponse;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateRequest;
import com.gia.openapi.model.TraineeAssignedTrainersUpdateResponse;
import com.gia.openapi.model.TraineeCreateRequest;
import com.gia.openapi.model.TraineeCreateResponse;
import com.gia.openapi.model.TraineeGetResponse;
import com.gia.openapi.model.TraineeUpdateRequest;
import com.gia.openapi.model.TraineeUpdateResponse;
import com.gym.crm.exception.ApiError;
import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.ConflictException;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationException;
import com.gym.crm.facade.GymFacade;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.entity.EntityType.TRAINEE;
import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.CONFLICT_ERROR;
import static com.gym.crm.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.exception.ApiError.VALIDATION_ERROR;
import static com.gym.crm.test.helper.JsonUtil.readJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TraineeRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class TraineeRestControllerTest {

    private static final String EXPECTED_ERROR_MESSAGE_TEMPLATE = "%s: %s";
    private static final String BASE_PATH = "/api/v1";
    private static final String USERNAME = "liam.miller";
    private static final String FIRST_NAME = "Liam";
    private static final String LAST_NAME = "Miller";
    private static final String SPECIALIZATION = "Yoga";

    @MockitoBean
    private GymFacade facade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldRegisterTraineeWhenRequestIsValid() throws Exception {
        String requestBody = readJson("json/trainee/register_request.json");
        String expectedResponseBody = readJson("json/trainee/register_response.json");
        TraineeCreateRequest expectedRequest = objectMapper.readValue(requestBody, TraineeCreateRequest.class);
        TraineeCreateResponse mockResponse = objectMapper.readValue(expectedResponseBody, TraineeCreateResponse.class);

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(mockResponse);

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/trainees/register")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, true);
        verify(facade).createTrainee(expectedRequest);
    }

    @Test
    void shouldFailRegisterTraineeWhenFirstNameIsNull() throws Exception {
        String requestBody = readJson("json/trainee/register_invalid_request.json");
        String expectedResponseBody = readJson("json/trainee/register_firstname_null_error.json");

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/trainees/register")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JSONAssert.assertEquals(expectedResponseBody, actualResponseBody, true);
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailRegisterTraineeWhenLastNameIsNull() throws Exception {
        TraineeCreateRequest invalidRequest = buildTraineeCreateRequest(null);

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/trainees/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: lastName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldGetTraineeProfileWhenUsernameIsValid() throws Exception {
        TraineeGetResponse mockResponse = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(mockResponse);

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeGetResponse actualResponse = objectMapper.readValue(actualResponseBody, TraineeGetResponse.class);
        assertThat(actualResponse.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualResponse.getLastName()).isEqualTo(LAST_NAME);
        assertThat(actualResponse.getIsActive()).isTrue();
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void shouldReturn404WhenTraineeNotFound() throws Exception {
        EntityNotFoundException exception = EntityNotFoundException.forUsername(TRAINEE, USERNAME);

        doThrow(exception).when(facade).getTraineeByUsername(USERNAME);

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(NOT_FOUND_ERROR, exception));
    }

    @Test
    void shouldReturn401WhenAuthenticationFailsOnGetTraineeProfile() throws Exception {
        doThrow(new AuthenticationException("User is not authenticated")).when(facade).getTraineeByUsername(USERNAME);

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(AUTHENTICATION_ERROR.getMessage());
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccursOnGetTraineeProfile() throws Exception {
        doThrow(new RuntimeException("Unexpected failure")).when(facade).getTraineeByUsername(USERNAME);

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(SERVICE_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(SERVICE_ERROR.getMessage());
    }

    @Test
    void shouldReturn500WhenHibernateExceptionOccursOnGetTraineeProfile() throws Exception {
        doThrow(new HibernateException("Database connectivity failure")).when(facade).getTraineeByUsername(USERNAME);

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(DATABASE_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(DATABASE_ERROR.getMessage());
    }

    @Test
    void shouldGetAvailableTrainersWhenUsernameIsValid() throws Exception {
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username("ricardo.milos")
                .firstName("Ricardo")
                .lastName("Milos")
                .specialization(SPECIALIZATION);

        when(facade.getAvailableTrainersForTrainee(USERNAME)).thenReturn(List.of(trainerResponse));

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}/available-trainers", USERNAME))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<AssignedTrainerResponse> actualResponse = objectMapper.readValue(actualResponseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AssignedTrainerResponse.class));
        assertThat(actualResponse).hasSize(1);
        assertThat(actualResponse.get(0).getUsername()).isEqualTo("ricardo.milos");
        assertThat(actualResponse.get(0).getFirstName()).isEqualTo("Ricardo");
        assertThat(actualResponse.get(0).getSpecialization()).isEqualTo(SPECIALIZATION);
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

        when(facade.getTraineeTrainings(any())).thenReturn(List.of(trainingResponse));

        String actualResponseBody = mockMvc.perform(get(BASE_PATH + "/trainees/{username}/trainings", USERNAME)
                        .param("fromDate", "2025-07-01")
                        .param("toDate", "2025-07-31")
                        .param("trainerName", "ronnie.coleman")
                        .param("trainingType", "Cardio"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<GetTraineeTrainingResponse> actualResponse = objectMapper.readValue(actualResponseBody,
                objectMapper.getTypeFactory().constructCollectionType(List.class, GetTraineeTrainingResponse.class));
        assertThat(actualResponse).hasSize(1);
        assertThat(actualResponse.get(0).getTrainingName()).isEqualTo("Morning Cardio");
        assertThat(actualResponse.get(0).getTrainingType()).isEqualTo("Cardio");
        assertThat(actualResponse.get(0).getTrainerName()).isEqualTo("ronnie.coleman");
        verify(facade).getTraineeTrainings(any());
    }

    @Test
    void shouldGetTraineeTrainingsWithNoOptionalFilters() throws Exception {
        when(facade.getTraineeTrainings(any())).thenReturn(List.of());

        mockMvc.perform(get(BASE_PATH + "/trainees/{username}/trainings", USERNAME))
                .andExpect(status().isOk());

        verify(facade).getTraineeTrainings(any());
    }

    @Test
    void shouldUpdateTraineeProfileWhenRequestIsValid() throws Exception {
        TraineeUpdateRequest validRequest = buildTraineeUpdateRequest(FIRST_NAME, LAST_NAME);
        TraineeUpdateResponse mockResponse = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(facade.updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class))).thenReturn(mockResponse);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeUpdateResponse actualResponse = objectMapper.readValue(actualResponseBody, TraineeUpdateResponse.class);
        assertThat(actualResponse.getUsername()).isEqualTo(USERNAME);
        assertThat(actualResponse.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(actualResponse.getIsActive()).isTrue();
        verify(facade).updateTrainee(eq(USERNAME), any(TraineeUpdateRequest.class));
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenFirstNameIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = buildTraineeUpdateRequest(null, LAST_NAME);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: firstName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenLastNameIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = buildTraineeUpdateRequest(FIRST_NAME, null);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: lastName: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeProfileWhenIsActiveIsNull() throws Exception {
        TraineeUpdateRequest invalidRequest = new TraineeUpdateRequest(FIRST_NAME, LAST_NAME, null);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: isActive: must not be null");
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
        TraineeAssignedTrainersUpdateResponse mockResponse = new TraineeAssignedTrainersUpdateResponse()
                .trainers(List.of(trainerResponse));

        when(facade.updateTraineeTrainers(eq(USERNAME), any())).thenReturn(mockResponse);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}/trainers", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TraineeAssignedTrainersUpdateResponse actualResponse = objectMapper.readValue(actualResponseBody, TraineeAssignedTrainersUpdateResponse.class);
        assertThat(actualResponse.getTrainers()).hasSize(1);
        assertThat(actualResponse.getTrainers().get(0).getUsername()).isEqualTo("ricardo.milos");
        assertThat(actualResponse.getTrainers().get(0).getSpecialization()).isEqualTo(SPECIALIZATION);
        verify(facade).updateTraineeTrainers(eq(USERNAME), any());
    }

    @Test
    void shouldFailUpdateTraineeTrainersWhenTrainerUsernamesIsNull() throws Exception {
        TraineeAssignedTrainersUpdateRequest invalidRequest = new TraineeAssignedTrainersUpdateRequest(null);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}/trainers", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: trainerUsernames: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailUpdateTraineeTrainersWhenTrainerUsernamesIsEmpty() throws Exception {
        TraineeAssignedTrainersUpdateRequest invalidRequest = buildTraineeAssignedTrainersUpdateRequest(List.of());

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/trainees/{username}/trainers", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: trainerUsernames: size must be between 1 and 2147483647");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldChangeTraineeActivationStatusWhenRequestIsValid() throws Exception {
        ActivationStatusRequest validRequest = new ActivationStatusRequest(true);

        mockMvc.perform(patch(BASE_PATH + "/trainees/{username}/activation", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).updateTraineeActivationStatus(eq(USERNAME), any());
    }

    @Test
    void shouldFailChangeTraineeActivationStatusWhenIsActiveIsNull() throws Exception {
        ActivationStatusRequest invalidRequest = new ActivationStatusRequest(null);

        String actualResponseBody = mockMvc.perform(patch(BASE_PATH + "/trainees/{username}/activation", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: isActive: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn409WhenChangingTraineeActivationStatusToAlreadySameStatus() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest(true);
        ConflictException exception = new ConflictException("Trainee with username: username is already active");

        doThrow(exception).when(facade).updateTraineeActivationStatus(eq(USERNAME), any());

        String actualResponseBody = mockMvc.perform(patch(BASE_PATH + "/trainees/{username}/activation", USERNAME)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(CONFLICT_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Conflict error: Trainee with username: username is already active");
        verify(facade).updateTraineeActivationStatus(eq(USERNAME), any());
    }

    @Test
    void shouldDeleteTraineeProfileWhenUsernameIsValid() throws Exception {
        mockMvc.perform(delete(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void shouldReturn404WhenDeletingNonExistentTrainee() throws Exception {
        EntityNotFoundException exception = EntityNotFoundException.forUsername(TRAINEE, USERNAME);

        doThrow(exception).when(facade).deleteTraineeByUsername(USERNAME);

        String actualResponseBody = mockMvc.perform(delete(BASE_PATH + "/trainees/{username}", USERNAME))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(NOT_FOUND_ERROR, exception));
    }

    @Test
    void shouldReturn400WhenValidationExceptionOccursDuringRegistration() throws Exception {
        TraineeCreateRequest validRequest = buildTraineeCreateRequest(LAST_NAME);
        ValidationException exception = new ValidationException("Custom validation failed");

        doThrow(exception).when(facade).createTrainee(any(TraineeCreateRequest.class));

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/trainees/register")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(VALIDATION_ERROR, exception));
    }

    private TraineeCreateRequest buildTraineeCreateRequest(String lastName) {
        return new TraineeCreateRequest()
                .firstName(FIRST_NAME)
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

    private String buildExpectedErrorMessage(ApiError apiError, Exception exception) {
        return String.format(EXPECTED_ERROR_MESSAGE_TEMPLATE, apiError.getMessage(), exception.getMessage());
    }

}
