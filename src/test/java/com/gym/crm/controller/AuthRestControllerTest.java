package com.gym.crm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import com.gia.openapi.model.LoginChangeRequest;
import com.gia.openapi.model.LoginRequest;
import com.gym.crm.exception.ApiError;
import com.gym.crm.exception.AuthenticationException;
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

import static com.gym.crm.entity.EntityType.USER;
import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.exception.ApiError.VALIDATION_ERROR;
import static com.gym.crm.test.helper.JsonUtil.readJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthRestControllerTest {

    private static final String EXPECTED_ERROR_MESSAGE_TEMPLATE = "%s: %s";
    private static final String BASE_PATH = "/api/v1";
    private static final String USERNAME = "liam.miller";
    private static final String PASSWORD = "password123";
    private static final String NEW_PASSWORD = "newPassword123";

    @MockitoBean
    private GymFacade facade;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLoginWhenCredentialsAreValid() throws Exception {
        String requestBody = readJson("json/auth/login_request.json");
        LoginRequest expectedRequest = objectMapper.readValue(requestBody, LoginRequest.class);

        mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        verify(facade).login(expectedRequest);
    }

    @Test
    void shouldFailLoginWhenUsernameIsNull() throws Exception {
        String requestBody = readJson("json/auth/login_invalid_request.json");
        String expectedResponseBody = readJson("json/auth/login_username_null_error.json");

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
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
    void shouldFailLoginWhenPasswordIsNull() throws Exception {
        LoginRequest invalidRequest = buildLoginRequest(null);

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: password: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn404WhenLoginUserNotFound() throws Exception {
        LoginRequest validRequest = buildLoginRequest(PASSWORD);
        EntityNotFoundException exception = EntityNotFoundException.forUsername(USER, USERNAME);

        doThrow(exception).when(facade).login(any());

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(buildExpectedErrorMessage(NOT_FOUND_ERROR, exception));
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(PASSWORD);

        doThrow(new RuntimeException("Unexpected failure")).when(facade).login(any());

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(SERVICE_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(SERVICE_ERROR.getMessage());
    }

    @Test
    void shouldReturn500WhenHibernateExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(PASSWORD);

        doThrow(new HibernateException("Database connectivity failure")).when(facade).login(any());

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isInternalServerError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(DATABASE_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(DATABASE_ERROR.getMessage());
    }

    @Test
    void shouldChangePasswordWhenRequestIsValid() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());

        verify(facade).changePassword(any(LoginChangeRequest.class));
    }

    @Test
    void shouldFailChangePasswordWhenUsernameIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(null, PASSWORD, NEW_PASSWORD);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: username: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenOldPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(USERNAME, null, NEW_PASSWORD);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: oldPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldFailChangePasswordWhenNewPasswordIsNull() throws Exception {
        LoginChangeRequest invalidRequest = buildLoginChangeRequest(USERNAME, PASSWORD, null);

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo("Validation error: newPassword: must not be null");
        verifyNoInteractions(facade);
    }

    @Test
    void shouldReturn401WhenAuthenticationFailsDuringPasswordChange() throws Exception {
        LoginChangeRequest validRequest = buildLoginChangeRequest(USERNAME, PASSWORD, NEW_PASSWORD);

        doThrow(new AuthenticationException("User is not authenticated")).when(facade).changePassword(any(LoginChangeRequest.class));

        String actualResponseBody = mockMvc.perform(put(BASE_PATH + "/auth/password")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponse actualErrorResponse = objectMapper.readValue(actualResponseBody, ErrorResponse.class);
        assertThat(actualErrorResponse.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(actualErrorResponse.getErrorMessage()).isEqualTo(AUTHENTICATION_ERROR.getMessage());
    }

    @Test
    void shouldReturn400WhenValidationExceptionOccursDuringLogin() throws Exception {
        LoginRequest validRequest = buildLoginRequest(PASSWORD);
        ValidationException exception = new ValidationException("Custom validation failed");

        doThrow(exception).when(facade).login(any());

        String actualResponseBody = mockMvc.perform(post(BASE_PATH + "/auth/login")
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

    private LoginRequest buildLoginRequest(String password) {
        return new LoginRequest()
                .username(USERNAME)
                .password(password);
    }

    private LoginChangeRequest buildLoginChangeRequest(String username, String oldPassword, String newPassword) {
        return new LoginChangeRequest()
                .username(username)
                .oldPassword(oldPassword)
                .newPassword(newPassword);
    }

    private String buildExpectedErrorMessage(ApiError apiError, Exception exception) {
        return String.format(EXPECTED_ERROR_MESSAGE_TEMPLATE, apiError.getMessage(), exception.getMessage());
    }

}
